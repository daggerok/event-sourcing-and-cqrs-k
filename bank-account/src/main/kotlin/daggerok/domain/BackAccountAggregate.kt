package daggerok.domain

import daggerok.api.Aggregate
import daggerok.api.event.DomainEvent
import daggerok.domain.event.BankAccountActivatedEvent
import daggerok.domain.event.BankAccountRegisteredEvent
import java.time.Instant
import java.util.UUID
import mu.KLogging

data class BackAccountAggregate(
    val state: BankAccountState = BankAccountState(),
    val eventStream: MutableList<DomainEvent<UUID>> = mutableListOf(),
) : Aggregate<UUID> {

    fun registerBankAccount(aggregateId: UUID, username: String, password: String): BackAccountAggregate {
        logger.debug { "registerBankAccount($aggregateId, $username, $password)" }
        if (state.aggregateId == aggregateId) throw RuntimeException("Bank account already registered")
        return applyEvent(
            BankAccountRegisteredEvent(
                aggregateId = aggregateId,
                username = username,
                password = password,
                registeredAt = Instant.now()
            )
        )
    }

    fun activateBankAccount(aggregateId: UUID): BackAccountAggregate {
        logger.debug { "activateBankAccount($aggregateId)" }
        return applyEvent(BankAccountActivatedEvent(aggregateId = aggregateId, activatedAt = Instant.now()))
    }

    private fun applyEvent(domainEvent: DomainEvent<UUID>): BackAccountAggregate {
        logger.debug { "applyEvent($domainEvent)" }
        state.mutate(domainEvent)
        eventStream += domainEvent
        return this
    }

    companion object {
        private val logger = KLogging().logger

        fun rebuild(
            snapshot: BackAccountAggregate = BackAccountAggregate(),
            events: List<DomainEvent<UUID>> = listOf(),
        ) = events.fold(snapshot, BackAccountAggregate::applyEvent)
    }
}
