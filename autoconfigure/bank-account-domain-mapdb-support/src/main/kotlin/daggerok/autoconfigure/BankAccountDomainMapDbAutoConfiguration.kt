package daggerok.autoconfigure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import daggerok.api.Repository
import daggerok.api.command.CommandHandler
import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import daggerok.domain.BackAccountAggregate
import daggerok.domain.BankAccountRepository
import daggerok.domain.command.BankAccountCommandHandler
import daggerok.domain.query.FindBankAccountActivatedStateQueryHandler
import daggerok.domain.query.FindBankAccountRegistrationDateQueryHandler
import daggerok.eventsore.mapdb.MapBDDomainEvent
import daggerok.eventsore.mapdb.MapDBEventStore
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.UUID
import mu.KLogging
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.DataInput2
import org.mapdb.DataOutput2
import org.mapdb.IndexTreeList
import org.mapdb.Serializer
import org.mapdb.serializer.GroupSerializerObjectArray
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

/**
 * See logging to debug which beans have been used by default and which beans have been overridden
 */
@Lazy
@ConditionalOnMissingClass
@Configuration(proxyBeanMethods = false)
class BankAccountDomainMapDbAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(String::class, name = ["pwd"])
    fun pwd(): String =
        logger.debug { "pwd()" }
            .let { System.getProperty("user.dir") }

    @Bean
    @ConditionalOnMissingBean(String::class, name = ["username"])
    fun username(): String =
        logger.debug { "username()" }
            .let { System.getProperty("user.name") }

    @Bean
    @ConditionalOnMissingBean(Path::class, name = ["path"])
    fun path(pwd: String, username: String): Path =
        logger.debug { "path(pwd=$pwd, username=$username)" }
            .let { Paths.get(pwd, "target", "$username.db") }
            .apply {
                logger.debug { "creating $parent directory..." }
                parent.toFile().mkdirs()
            }

    @Bean
    @ConditionalOnMissingBean(DB::class, name = ["db"])
    fun db(path: Path): DB =
        logger.debug { "db(path=$path)" }
            .let {
                DBMaker.fileDB(path.toFile())
                    .allocateStartSize(1024)
                    .allocateIncrement(1024)
                    .fileChannelEnable()
                    .fileMmapEnableIfSupported()
                    .executorEnable()
                    .closeOnJvmShutdown() // .closeOnJvmShutdownWeakReference()
                    .fileLockWait(Duration.ofSeconds(30).toMillis())
                    .make()
            }

    @Bean
    fun domainEventsSerializer(): Serializer<MapBDDomainEvent<UUID>> =
        logger.debug { "domainEventsSerializer()" }
            .let {
                object : GroupSerializerObjectArray<MapBDDomainEvent<UUID>>() {
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
            }

    @Bean
    @ConditionalOnMissingBean(MutableList::class, name = ["storage"])
    fun storage(db: DB, domainEventsSerializer: Serializer<MapBDDomainEvent<UUID>>): IndexTreeList<MapBDDomainEvent<UUID>> =
        logger.debug { "storage(db=$db)" }
            .let { db.indexTreeList("eventStream", domainEventsSerializer).createOrOpen() }
            .apply {
                logger.debug { "clearing database..." }
                clear()
            }

    @Bean
    @ConditionalOnMissingBean(MapDBEventStore::class, name = ["eventStore", "mapDbEventStore"])
    fun eventStore(storage: IndexTreeList<MapBDDomainEvent<UUID>>): EventStore<UUID, MapBDDomainEvent<UUID>> =
        logger.debug { "eventStore(storage=$storage)" }
            .let { MapDBEventStore(storage) }

    @Bean
    @ConditionalOnMissingBean(BankAccountRepository::class, name = ["repository", "bankAccountRepository"])
    fun repository(eventStore: EventStore<UUID, DomainEvent<UUID>>): Repository<UUID, BackAccountAggregate> =
        logger.debug { "repository(eventStore=$eventStore)" }
            .let { BankAccountRepository(eventStore) }

    @Bean
    @ConditionalOnMissingBean(BankAccountCommandHandler::class, name = ["commandHandler", "bankAccountCommandHandler"])
    fun commandHandler(repository: Repository<UUID, BackAccountAggregate>): CommandHandler<UUID, BackAccountAggregate> =
        logger.debug { "commandHandler(repository=$repository)" }
            .let { BankAccountCommandHandler(repository) }

    @Bean
    @ConditionalOnMissingBean(
        FindBankAccountRegistrationDateQueryHandler::class,
        name = ["findBankAccountActivatedStateQueryHandler"],
    )
    fun findBankAccountRegistrationDateQueryHandler(repository: Repository<UUID, BackAccountAggregate>) =
        logger.debug { "findBankAccountRegistrationDateQueryHandler(repository=$repository)" }
            .let { FindBankAccountRegistrationDateQueryHandler(repository) }

    @Bean
    @ConditionalOnMissingBean(
        FindBankAccountActivatedStateQueryHandler::class,
        name = ["findBankAccountActivatedStateQueryHandler"],
    )
    fun findBankAccountActivatedStateQueryHandler(repository: Repository<UUID, BackAccountAggregate>) =
        logger.debug { "findBankAccountActivatedStateQueryHandler(repository=$repository)" }
            .let { FindBankAccountActivatedStateQueryHandler(repository) }

    private companion object : KLogging()
}
