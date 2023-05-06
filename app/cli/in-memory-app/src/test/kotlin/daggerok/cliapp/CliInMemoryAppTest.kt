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
import daggerok.domain.InMemoryEventStore
import java.util.UUID
import mu.KLogging
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("cli-in-memory-app test")
class CliInMemoryAppTest {

    @Test
    fun main() {
        // setup
        val db = mutableListOf<DomainEvent<UUID>>()
        val eventStore = InMemoryEventStore(db)
        val repository = BankAccountRepository(eventStore)
        val commandHandler = BankAccountCommandHandler(repository)

        // given
        commandHandler.handle(
            RegisterBankAccountCommand(
                aggregateId = UUID.fromString("0-0-0-0-1"),
                username = "daggerok",
                password = "Passw0rd123",
            )
        )

        // and
        commandHandler.handle(ActivateBankAccountCommand(aggregateId = UUID.fromString("0-0-0-0-1")))

        // when
        val findBankAccountRegistrationDateQueryHandler = FindBankAccountRegistrationDateQueryHandler(repository)

        // then
        val result1 = findBankAccountRegistrationDateQueryHandler
            .handle(FindBankAccountRegistrationDateQuery(UUID.fromString("0-0-0-0-01")))
        logger.debug { "result1: $result1" }

        // and when
        val findBankAccountActivatedStateQueryHandler = FindBankAccountActivatedStateQueryHandler(repository)

        // then
        val result2 = findBankAccountActivatedStateQueryHandler
            .handle(FindBankAccountActivatedStateQuery(UUID.fromString("0-0-0-0-1")))
        logger.debug { "result2: $result2" }
    }

    companion object : KLogging()
}
