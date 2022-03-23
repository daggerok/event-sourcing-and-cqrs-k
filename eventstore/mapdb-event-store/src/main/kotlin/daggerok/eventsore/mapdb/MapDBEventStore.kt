package daggerok.eventsore.mapdb

import daggerok.api.event.EventStore
import java.util.UUID
import mu.KLogging
import org.mapdb.IndexTreeList

class MapDBEventStore(private val storage: IndexTreeList<MapBDDomainEvent<UUID>>) :
    EventStore<UUID, MapBDDomainEvent<UUID>> {

    override fun append(vararg eventStream: MapBDDomainEvent<UUID>) {
        eventStream.forEach { logger.debug { "append($it)" } }
        storage += eventStream
    }

    override fun load(aggregateId: UUID): List<MapBDDomainEvent<UUID>> {
        logger.debug { "load($aggregateId)" }
        return storage.filterNotNull().filter { it.aggregateId == aggregateId }
    }

    override fun loadAll(): List<MapBDDomainEvent<UUID>> {
        logger.debug { "loadAll()" }
        return storage.filterNotNull()
    }

    private companion object : KLogging()
}
