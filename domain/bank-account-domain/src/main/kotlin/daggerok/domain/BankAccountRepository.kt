package daggerok.domain

import daggerok.api.Repository
import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import java.util.UUID
import mu.KLogging

class BankAccountRepository(private val eventStore: EventStore<UUID, out DomainEvent<UUID>>) :
    Repository<UUID, BackAccountAggregate> {

    override fun findByAggregateId(aggregateId: UUID): BackAccountAggregate =
        logger.debug { "findByAggregateId(aggregateId=$aggregateId)" }
            .let { eventStore.load(aggregateId) }
            .let { BackAccountAggregate.rebuild(events = it) }
            .apply { events.clear() }

    override fun save(aggregate: BackAccountAggregate): BackAccountAggregate =
        logger.debug { "save(aggregate=$aggregate)" }
            .also { eventStore.append(aggregate.events) }
            .let { aggregate }
            .apply { events.clear() }

    private companion object : KLogging()
}
