package daggerok.eventsore.inmemory

import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import java.util.UUID
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InMemoryEventStoreTests {

    val storage = mutableListOf<DomainEvent<UUID>>()
    val eventStore: EventStore<UUID, DomainEvent<UUID>> = InMemoryEventStore(storage)

    val events = listOf(
        object : DomainEvent<UUID> {
            override val aggregateId: UUID = UUID.randomUUID()
            override fun toString(): String = "DomainEvent1(aggregateId=$aggregateId)"
        },
        object : DomainEvent<UUID> {
            override val aggregateId: UUID = UUID.randomUUID()
            override fun toString(): String = "DomainEvent2(aggregateId=$aggregateId)"
        },
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

    private companion object : KLogging()
}
