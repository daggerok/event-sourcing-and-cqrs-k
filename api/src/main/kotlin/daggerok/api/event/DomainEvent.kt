package daggerok.api.event

interface DomainEvent<ID> {
    val aggregateId: ID
}
