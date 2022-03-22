package daggerok.domain

import daggerok.api.Repository
import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import java.util.UUID
import mu.KLogging

class BankAccountRepository(private val eventStore: EventStore<UUID, DomainEvent<UUID>>) :
    Repository<UUID, BackAccountAggregate> {

    override fun findByAggregateId(aggregateId: UUID): BackAccountAggregate {
        logger.debug { "findByAggregateId($aggregateId)" }
        val eventStream = eventStore.load(aggregateId)
        return BackAccountAggregate.rebuild(events = eventStream)
    }

    override fun save(aggregate: BackAccountAggregate): BackAccountAggregate {
        logger.debug { "save($aggregate)" }
        eventStore.append(*aggregate.eventStream.toTypedArray())
        return aggregate
    }

    private companion object : KLogging()
}