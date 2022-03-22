package daggerok.domain.event

import daggerok.api.event.DomainEvent
import java.time.Instant
import java.util.UUID

data class BankAccountRegisteredEvent(
    override var aggregateId: UUID,
    val username: String,
    val password: String,
    val registeredAt: Instant,
) : DomainEvent<UUID>

data class BankAccountActivatedEvent(
    override var aggregateId: UUID,
    val activatedAt: Instant,
) : DomainEvent<UUID>
