package daggerok.domain

import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import daggerok.domain.event.MapBDDomainEvent
import java.util.UUID
import mu.KLogging
import org.mapdb.IndexTreeList

class MapDBEventStore(private val storage: IndexTreeList<MapBDDomainEvent<UUID>>) :
    EventStore<UUID, MapBDDomainEvent<UUID>> {

    override fun append(vararg events: DomainEvent<UUID>): Unit =
        logger.debug { "append(events=${events.toList()})" }
            .let { events.filterIsInstance<MapBDDomainEvent<UUID>>() }
            .let { storage.addAll(it) }

    override fun load(aggregateId: UUID): List<MapBDDomainEvent<UUID>> =
        logger.debug { "load(aggregateId=$aggregateId)" }
            .let { storage.filterNotNull().filter { it.aggregateId == aggregateId } }

    override fun loadAll(): List<MapBDDomainEvent<UUID>> =
        logger.debug { "loadAll()" }
            .let { storage.filterNotNull() }

    private companion object : KLogging()
}
