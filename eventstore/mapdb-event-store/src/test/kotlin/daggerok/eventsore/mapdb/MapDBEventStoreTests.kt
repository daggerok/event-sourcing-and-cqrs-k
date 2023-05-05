package daggerok.eventsore.mapdb

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import daggerok.api.event.EventStore
import java.nio.file.Paths
import java.time.Duration
import java.time.Instant
import java.util.UUID
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mapdb.DBMaker
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import org.mapdb.serializer.GroupSerializerObjectArray

@DisplayName("MapDBEventStore tests")
class MapDBEventStoreTests {

    val pwd = System.getProperty("user.dir")
    val username = System.getProperty("user.name")
    val path = Paths.get(pwd, "target", "$username.db").apply {
        logger.debug { "creating $parent directory..." }
        parent.toFile().mkdirs()
    }

    val db = DBMaker.fileDB(path.toFile())
        .allocateStartSize(1024)
        .allocateIncrement(1024)
        .fileChannelEnable()
        .fileMmapEnableIfSupported()
        .executorEnable()
        .closeOnJvmShutdown() // .closeOnJvmShutdownWeakReference()
        .fileLockWait(Duration.ofSeconds(30).toMillis())
        .make()

    val domainEventsSerializer = object : GroupSerializerObjectArray<MapBDDomainEvent<UUID>>() {

        val objectMapper = ObjectMapper().apply {
            logger.debug { "registering JavaTimeModule..." }
            registerModules(JavaTimeModule())
        }

        override fun serialize(out: DataOutput2, domainEvent: MapBDDomainEvent<UUID>) {
            val jsonString = objectMapper.writeValueAsString(domainEvent)
            out.writeUTF(jsonString)
        }

        override fun deserialize(input: DataInput2, available: Int): MapBDDomainEvent<UUID> {
            val jsonString = input.readUTF()
            val type = object : TypeReference<MapBDDomainEvent<UUID>>() {}
            return objectMapper.readValue(jsonString, type)
        }
    }

    val storage = db.indexTreeList("eventStream", domainEventsSerializer).createOrOpen().apply {
        logger.debug { "clearing database..." }
        clear()
    }

    val eventStore: EventStore<UUID, MapBDDomainEvent<UUID>> = MapDBEventStore(storage)

    val events = listOf(
        BankAccountRegisteredEvent(
            aggregateId = UUID.randomUUID(),
            username = "daggerok",
            password = "pwd",
            registeredAt = Instant.now(),
        ),
        BankAccountActivatedEvent(
            aggregateId = UUID.randomUUID(),
            activatedAt = Instant.now(),
        )
    )

    @Test
    fun `should append and load`() {
        // given
        eventStore.append(events.first(), events.last())

        // then
        val domainEvents = eventStore.loadAll()
        logger.debug { "domainEvents: $domainEvents" }
        assertThat(domainEvents).hasSize(2)

        // and
        val events1 = eventStore.load(events.first().aggregateId)
        logger.debug { "events 1: $events1" }

        // and
        val events2 = eventStore.load(events.last().aggregateId)
        logger.debug { "events 2: $events2" }
    }

    companion object : KLogging()
}
