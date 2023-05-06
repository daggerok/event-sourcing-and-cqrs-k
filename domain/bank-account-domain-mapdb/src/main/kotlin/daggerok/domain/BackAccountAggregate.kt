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
    val events: MutableList<DomainEvent<UUID>> = mutableListOf(),
) : Aggregate<UUID> {

    fun registerBankAccount(aggregateId: UUID, username: String, password: String): BackAccountAggregate =
        logger.debug { "registerBankAccount(aggregateId=$aggregateId, username=$username, password=$password)" }
            .also { if (state.aggregateId == aggregateId) throw RuntimeException("Bank account already registered") }
            .let {
                BankAccountRegisteredEvent(
                    aggregateId = aggregateId,
                    username = username,
                    password = password,
                    registeredAt = Instant.now()
                )
            }
            .let(this::applyEvent)

    fun activateBankAccount(aggregateId: UUID): BackAccountAggregate =
        logger.debug { "activateBankAccount(aggregateId=$aggregateId)" }
            .let { BankAccountActivatedEvent(aggregateId = aggregateId, activatedAt = Instant.now()) }
            .let(this::applyEvent)

    private fun applyEvent(domainEvent: DomainEvent<UUID>): BackAccountAggregate =
        logger.debug { "applyEvent(domainEvent=$domainEvent)" }
            .also { state.mutate(domainEvent) }
            .also { events += domainEvent }
            .let { this }

    companion object {
        private val logger = KLogging().logger

        fun rebuild(snapshot: BackAccountAggregate = BackAccountAggregate(),
                    events: List<DomainEvent<UUID>> = listOf()): BackAccountAggregate =
            logger.debug { "rebuild(snapshot=$snapshot, events=$events)" }
                .let { events.fold(snapshot, BackAccountAggregate::applyEvent) }
    }
}
