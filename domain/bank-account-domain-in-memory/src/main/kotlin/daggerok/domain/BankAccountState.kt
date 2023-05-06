package daggerok.domain

import daggerok.api.State
import daggerok.api.event.DomainEvent
import daggerok.domain.event.BankAccountActivatedEvent
import daggerok.domain.event.BankAccountRegisteredEvent
import java.time.Instant
import java.util.UUID
import mu.KLogging

data class BankAccountState(
    var aggregateId: UUID = UUID.fromString("0-0-0-0-0"),
    var username: String = "",
    var password: String = "",
    var registeredAt: Instant = Instant.ofEpochMilli(0),
    var activated: Boolean = false,
    var activatedAt: Instant = Instant.ofEpochMilli(0),
) : State<UUID, DomainEvent<UUID>> {

    override fun mutate(event: DomainEvent<UUID>): State<UUID, DomainEvent<UUID>> =
        logger.debug { "mutate(event=$event)" }
            .let {
                when (event) {
                    is BankAccountRegisteredEvent -> mutateBankAccountRegisteredEvent(event)
                    is BankAccountActivatedEvent -> mutateBankAccountActivatedEvent(event)
                    else -> mutateUnknownEvent(event)
                }
            }

    private fun mutateBankAccountRegisteredEvent(event: BankAccountRegisteredEvent): State<UUID, DomainEvent<UUID>> {
        logger.debug { "mutateBankAccountRegisteredEvent(event=$event)" }
        aggregateId = event.aggregateId
        username = event.username
        password = event.password
        registeredAt = event.registeredAt
        return this
    }

    private fun mutateBankAccountActivatedEvent(event: BankAccountActivatedEvent): State<UUID, DomainEvent<UUID>> {
        logger.debug { "mutateBankAccountActivatedEvent(event=$event)" }
        activated = true
        activatedAt = event.activatedAt
        return this
    }

    private fun mutateUnknownEvent(event: DomainEvent<UUID>): State<UUID, DomainEvent<UUID>> =
        logger.debug { "mutateUnknownEvent(event=$event)" }
            .let { this }

    private companion object : KLogging()
}
