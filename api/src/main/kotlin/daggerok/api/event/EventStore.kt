package daggerok.api.event

import java.util.UUID

interface EventStore<ID, E : DomainEvent<ID>> {
    fun append(events: List<DomainEvent<UUID>>) {
        append(*events.toTypedArray())
    }
    fun append(vararg events: DomainEvent<UUID>)
    fun load(aggregateId: ID): List<E>
    fun loadAll(): List<E>
}
