package daggerok.api

interface Repository<ID, A : Aggregate<ID>> {
    fun findByAggregateId(aggregateId: ID): A
    fun save(aggregate: A): A
}
