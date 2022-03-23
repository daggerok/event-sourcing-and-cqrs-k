package daggerok.eventsore.mapdb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import daggerok.api.event.DomainEvent
import java.time.Instant
import java.util.UUID

data class BankAccountRegisteredEvent @JsonCreator constructor(
    @JsonProperty("aggregateId") override var aggregateId: UUID,
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String,
    @JsonProperty("registeredAt") val registeredAt: Instant,
) : MapBDDomainEvent<UUID>

data class BankAccountActivatedEvent @JsonCreator constructor(
    @JsonProperty("aggregateId") override var aggregateId: UUID,
    @JsonProperty("activatedAt") val activatedAt: Instant,
) : MapBDDomainEvent<UUID>

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY)
@JsonSubTypes(
    JsonSubTypes.Type(BankAccountRegisteredEvent::class),
    JsonSubTypes.Type(BankAccountActivatedEvent::class),
)
interface MapBDDomainEvent<T> : DomainEvent<T>
