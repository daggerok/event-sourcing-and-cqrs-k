package daggerok.autoconfigure

import daggerok.api.Repository
import daggerok.api.command.CommandHandler
import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import daggerok.domain.BackAccountAggregate
import daggerok.domain.BankAccountRepository
import daggerok.domain.command.BankAccountCommandHandler
import daggerok.domain.query.FindBankAccountActivatedStateQueryHandler
import daggerok.domain.query.FindBankAccountRegistrationDateQueryHandler
import daggerok.domain.InMemoryEventStore
import java.util.UUID
import mu.KLogging
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
class BankAccountDomainInMemoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MutableList::class, name = ["storage"])
    fun storage(): MutableList<DomainEvent<UUID>> =
        logger.debug { "storage()" }
            .let { mutableListOf() }

    @Bean
    @ConditionalOnMissingBean(InMemoryEventStore::class, name = ["eventStore", "inMemoryEventStore"])
    fun eventStore(storage: MutableList<DomainEvent<UUID>>): EventStore<UUID, DomainEvent<UUID>> =
        logger.debug { "InMemoryEventStore(storage=$storage)" }
            .let { InMemoryEventStore(storage) }

    @Bean
    @ConditionalOnMissingBean(BankAccountRepository::class, name = ["repository", "bankAccountRepository"])
    fun repository(eventStore: EventStore<UUID, DomainEvent<UUID>>): Repository<UUID, BackAccountAggregate> =
        logger.debug { "BankAccountRepository(eventStore=$eventStore)" }
            .let { BankAccountRepository(eventStore) }

    @Bean
    @ConditionalOnMissingBean(BankAccountCommandHandler::class, name = ["commandHandler", "bankAccountCommandHandler"])
    fun commandHandler(repository: Repository<UUID, BackAccountAggregate>): CommandHandler<UUID, BackAccountAggregate> =
        logger.debug { "BankAccountCommandHandler(repository=$repository)" }
            .let { BankAccountCommandHandler(repository) }

    @Bean
    @ConditionalOnMissingBean(
        FindBankAccountRegistrationDateQueryHandler::class,
        name = ["findBankAccountActivatedStateQueryHandler"],
    )
    fun findBankAccountRegistrationDateQueryHandler(repository: Repository<UUID, BackAccountAggregate>) =
        logger.debug { "FindBankAccountRegistrationDateQueryHandler(repository=$repository)" }
            .let { FindBankAccountRegistrationDateQueryHandler(repository) }

    @Bean
    @ConditionalOnMissingBean(
        FindBankAccountActivatedStateQueryHandler::class,
        name = ["findBankAccountActivatedStateQueryHandler"],
    )
    fun findBankAccountActivatedStateQueryHandler(repository: Repository<UUID, BackAccountAggregate>) =
        logger.debug { "FindBankAccountActivatedStateQueryHandler(repository=$repository)" }
            .let { FindBankAccountActivatedStateQueryHandler(repository) }

    private companion object : KLogging()
}
