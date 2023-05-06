package daggerok.domain

import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import java.util.UUID
import mu.KLogging

class InMemoryEventStore(private val storage: MutableList<DomainEvent<UUID>>) :
    EventStore<UUID, DomainEvent<UUID>> {

    override fun append(vararg events: DomainEvent<UUID>) =
        logger.debug { "append(events=${events.toList()})" }
            .let { events.forEach(storage::add) }

    override fun load(aggregateId: UUID): List<DomainEvent<UUID>> =
        logger.debug { "load(aggregateId=$aggregateId)" }
            .let { storage.filter { it.aggregateId == aggregateId } }

    override fun loadAll(): List<DomainEvent<UUID>> =
        logger.debug { "loadAll()" }
            .let { storage }

    private companion object : KLogging()
}
