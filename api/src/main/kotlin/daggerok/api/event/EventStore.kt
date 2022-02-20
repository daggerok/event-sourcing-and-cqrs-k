package daggerok.api.event

interface EventStore<ID, E : DomainEvent<ID>> {
    fun append(vararg eventStream: E)
    fun load(aggregateId: ID): List<E>
    fun loadAll(): List<E>
}
