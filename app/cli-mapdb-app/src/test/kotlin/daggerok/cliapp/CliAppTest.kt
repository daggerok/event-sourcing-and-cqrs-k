package daggerok.cliapp

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import daggerok.api.event.EventStore
import daggerok.domain.BankAccountRepository
import daggerok.domain.command.ActivateBankAccountCommand
import daggerok.domain.command.BankAccountCommandHandler
import daggerok.domain.command.RegisterBankAccountCommand
import daggerok.domain.query.FindBankAccountActivatedStateQuery
import daggerok.domain.query.FindBankAccountActivatedStateQueryHandler
import daggerok.domain.query.FindBankAccountRegistrationDateQuery
import daggerok.domain.query.FindBankAccountRegistrationDateQueryHandler
import daggerok.eventsore.mapdb.MapBDDomainEvent
import daggerok.eventsore.mapdb.MapDBEventStore
import java.nio.file.Paths
import java.time.Duration
import java.util.UUID
import mu.KLogging
import org.junit.jupiter.api.Test
import org.mapdb.DBMaker
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import org.mapdb.serializer.GroupSerializerObjectArray

class CliAppTest {

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
    val repository = BankAccountRepository(eventStore)
    val commandHandler = BankAccountCommandHandler(repository)

    @Test
    fun main() {
        commandHandler.handle(
            RegisterBankAccountCommand(
                aggregateId = UUID.fromString("0-0-0-0-1"),
                username = "daggerok",
                password = "Passw0rd123",
            )
        )
        commandHandler.handle(ActivateBankAccountCommand(aggregateId = UUID.fromString("0-0-0-0-1")))

        val findBankAccountRegistrationDateQueryHandler = FindBankAccountRegistrationDateQueryHandler(repository)
        val result1 = findBankAccountRegistrationDateQueryHandler
            .handle(FindBankAccountRegistrationDateQuery(UUID.fromString("0-0-0-0-01")))
        logger.debug { result1 }

        val findBankAccountActivatedStateQueryHandler = FindBankAccountActivatedStateQueryHandler(repository)
        val result2 = findBankAccountActivatedStateQueryHandler
            .handle(FindBankAccountActivatedStateQuery(UUID.fromString("0-0-0-0-1")))
        logger.debug { result2 }
    }

    companion object : KLogging()
}
