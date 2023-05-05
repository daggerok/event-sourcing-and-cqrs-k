package daggerok.api.event

interface EventStore<ID, E : DomainEvent<ID>> {
    fun append(events: Collection<DomainEvent<ID>>): Unit =
        append(*events.toTypedArray())
    fun append(vararg events: DomainEvent<ID>)
    fun load(aggregateId: ID): List<E>
    fun loadAll(): Iterable<E>
}
