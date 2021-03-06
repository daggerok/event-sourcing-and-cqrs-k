package daggerok.eventsore.inmemory

import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import java.util.UUID
import mu.KLogging

class InMemoryEventStore(private val storage: MutableList<DomainEvent<UUID>>) :
    EventStore<UUID, DomainEvent<UUID>> {

    override fun append(vararg eventStream: DomainEvent<UUID>) =
        eventStream.onEach { logger.debug { "append($it)" } }
            .forEach(storage::add)

    override fun load(aggregateId: UUID): List<DomainEvent<UUID>> {
        logger.debug { "load($aggregateId)" }
        return storage.filter { it.aggregateId == aggregateId }
    }

    override fun loadAll(): List<DomainEvent<UUID>> {
        logger.debug { "loadAll()" }
        return storage
    }

    private companion object : KLogging()
}
