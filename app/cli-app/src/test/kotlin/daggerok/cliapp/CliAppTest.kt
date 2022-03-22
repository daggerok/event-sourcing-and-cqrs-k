package daggerok.cliapp

import daggerok.api.event.DomainEvent
import daggerok.domain.BankAccountRepository
import daggerok.domain.command.ActivateBankAccountCommand
import daggerok.domain.command.BankAccountCommandHandler
import daggerok.domain.command.RegisterBankAccountCommand
import daggerok.domain.query.FindBankAccountActivatedStateQuery
import daggerok.domain.query.FindBankAccountActivatedStateQueryHandler
import daggerok.domain.query.FindBankAccountRegistrationDateQuery
import daggerok.domain.query.FindBankAccountRegistrationDateQueryHandler
import daggerok.eventsore.inmemory.InMemoryEventStore
import java.util.UUID
import mu.KLogging
import org.junit.jupiter.api.Test

class CliAppTest {

    @Test
    fun main() {
        val db = mutableListOf<DomainEvent<UUID>>()
        val eventStore = InMemoryEventStore(db)
        val repository = BankAccountRepository(eventStore)
        val commandHandler = BankAccountCommandHandler(repository)

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
