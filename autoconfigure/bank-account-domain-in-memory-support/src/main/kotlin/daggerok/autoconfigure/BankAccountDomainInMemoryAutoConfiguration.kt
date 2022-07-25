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
import daggerok.eventsore.inmemory.InMemoryEventStore
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
        mutableListOf<DomainEvent<UUID>>()
            .also { logger.debug { "storage: $it" } }

    @Bean
    @ConditionalOnMissingBean(InMemoryEventStore::class, name = ["eventStore", "inMemoryEventStore"])
    fun eventStore(storage: MutableList<DomainEvent<UUID>>): EventStore<UUID, DomainEvent<UUID>> =
        InMemoryEventStore(storage)
            .also { logger.debug { "eventStore: $it" } }

    @Bean
    @ConditionalOnMissingBean(BankAccountRepository::class, name = ["repository", "bankAccountRepository"])
    fun repository(eventStore: EventStore<UUID, DomainEvent<UUID>>): Repository<UUID, BackAccountAggregate> =
        BankAccountRepository(eventStore)
            .also { logger.debug { "repository: $it" } }

    @Bean
    @ConditionalOnMissingBean(BankAccountCommandHandler::class, name = ["commandHandler", "bankAccountCommandHandler"])
    fun commandHandler(repository: Repository<UUID, BackAccountAggregate>): CommandHandler<UUID, BackAccountAggregate> =
        BankAccountCommandHandler(repository)
            .also { logger.debug { "commandHandler: $it" } }

    @Bean
    @ConditionalOnMissingBean(
        FindBankAccountRegistrationDateQueryHandler::class,
        name = ["findBankAccountActivatedStateQueryHandler"],
    )
    fun findBankAccountRegistrationDateQueryHandler(repository: Repository<UUID, BackAccountAggregate>) =
        FindBankAccountRegistrationDateQueryHandler(repository)
            .also { logger.debug { "findBankAccountRegistrationDateQueryHandler: $it" } }

    @Bean
    @ConditionalOnMissingBean(
        FindBankAccountActivatedStateQueryHandler::class,
        name = ["findBankAccountActivatedStateQueryHandler"],
    )
    fun findBankAccountActivatedStateQueryHandler(repository: Repository<UUID, BackAccountAggregate>) =
        FindBankAccountActivatedStateQueryHandler(repository)
            .also { logger.debug { "findBankAccountActivatedStateQueryHandler: $it" } }

    private companion object : KLogging()
}
