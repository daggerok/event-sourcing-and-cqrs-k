package daggerok

import daggerok.api.Repository
import daggerok.api.command.CommandHandler
import daggerok.api.event.DomainEvent
import daggerok.api.event.EventStore
import daggerok.domain.BackAccountAggregate
import daggerok.domain.BankAccountRepository
import daggerok.domain.command.ActivateBankAccountCommand
import daggerok.domain.command.BankAccountCommandHandler
import daggerok.domain.command.RegisterBankAccountCommand
import daggerok.domain.event.InMemoryEventStore
import daggerok.domain.query.FindBankAccountActivatedStateQuery
import daggerok.domain.query.FindBankAccountActivatedStateQueryHandler
import daggerok.domain.query.FindBankAccountActivatedStateResult
import daggerok.domain.query.FindBankAccountRegistrationDateQuery
import daggerok.domain.query.FindBankAccountRegistrationDateQueryHandler
import daggerok.domain.query.FindBankAccountRegistrationDateResult
import java.util.UUID
import mu.KLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class BankAccountResource(
    private val commandHandler: CommandHandler<UUID, BackAccountAggregate>,
    private val findBankAccountActivatedStateQueryHandler: FindBankAccountActivatedStateQueryHandler,
    private val findBankAccountRegistrationDateQueryHandler: FindBankAccountRegistrationDateQueryHandler,
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register-bank-account")
    fun register(@RequestBody command: RegisterBankAccountCommand): BackAccountAggregate {
        logger.debug { "register($command)" }
        return commandHandler.handle(command)
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/activate-bank-account")
    fun activate(@RequestBody command: ActivateBankAccountCommand): BackAccountAggregate {
        logger.debug { "activate($command)" }
        return commandHandler.handle(command)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/find-bank-account-registration-date/{aggregateId}")
    fun findBankAccountRegistrationDate(@PathVariable aggregateId: String): FindBankAccountRegistrationDateResult {
        logger.debug { "findBankAccountRegistrationDate($aggregateId)" }
        return findBankAccountRegistrationDateQueryHandler
            .handle(FindBankAccountRegistrationDateQuery(UUID.fromString(aggregateId)))
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/find-bank-account-activated-status/{aggregateId}")
    fun findBankAccountActivatedStatus(@PathVariable aggregateId: String): FindBankAccountActivatedStateResult {
        logger.debug { "findBankAccountActivatedStatus($aggregateId)" }
        return findBankAccountActivatedStateQueryHandler
            .handle(FindBankAccountActivatedStateQuery(UUID.fromString(aggregateId)))
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onError(e: Throwable): Map<String, String> {
        logger.warn { "onError($e)" }
        val error = e.message ?: "Unknown error"
        return mapOf("error" to error)
    }

    private companion object : KLogging()
}

@SpringBootApplication
class WebApplication {

    @Bean
    @ConditionalOnMissingBean
    fun storage(): MutableList<DomainEvent<UUID>> = mutableListOf()

    @Bean
    @ConditionalOnMissingBean
    fun eventStore(): EventStore<UUID, DomainEvent<UUID>> = InMemoryEventStore(storage())

    @Bean
    @ConditionalOnMissingBean
    fun repository(): Repository<UUID, BackAccountAggregate> = BankAccountRepository(eventStore())

    @Bean
    @ConditionalOnMissingBean
    fun commandHandler(): CommandHandler<UUID, BackAccountAggregate> = BankAccountCommandHandler(repository())

    @Bean
    @ConditionalOnMissingBean
    fun findBankAccountRegistrationDateQueryHandler() = FindBankAccountRegistrationDateQueryHandler(repository())

    @Bean
    @ConditionalOnMissingBean
    fun findBankAccountActivatedStateQueryHandler() = FindBankAccountActivatedStateQueryHandler(repository())
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}
