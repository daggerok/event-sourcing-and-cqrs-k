package daggerok.domain.command

import daggerok.api.Repository
import daggerok.api.command.Command
import daggerok.api.command.CommandHandler
import daggerok.domain.BackAccountAggregate
import java.util.UUID
import mu.KLogging

class BankAccountCommandHandler(private val repository: Repository<UUID, BackAccountAggregate>) :
    CommandHandler<UUID, BackAccountAggregate> {

    override fun handle(command: Command<UUID>): BackAccountAggregate {
        logger.debug { "handle($command)" }
        return when (command) {
            is RegisterBankAccountCommand -> handleRegisterBankAccountCommand(command)
            is ActivateBankAccountCommand -> handleActivateBankAccountCommand(command)
            else -> handleUnknownCommand(command)
        }
    }

    private fun handleRegisterBankAccountCommand(command: RegisterBankAccountCommand): BackAccountAggregate =
        repository
            .also { logger.debug { "handleRegisterBankAccountCommand($command)" } }
            .findByAggregateId(command.aggregateId)
            .apply { eventStream.clear() }
            .registerBankAccount(command.aggregateId, command.username, command.password)
            .let { repository.save(it) }
            .apply { eventStream.clear() }

    private fun handleActivateBankAccountCommand(command: ActivateBankAccountCommand): BackAccountAggregate =
        repository
            .also { logger.debug { "handleActivateBankAccountCommand($command)" } }
            .findByAggregateId(command.aggregateId)
            .apply { eventStream.clear() }
            .activateBankAccount(command.aggregateId)
            .let { repository.save(it) }
            .apply { eventStream.clear() }

    private fun handleUnknownCommand(command: Command<UUID>): BackAccountAggregate {
        logger.debug { "handleUnknownCommand($command)" }
        throw RuntimeException("Unsupported command: $command")
    }

    private companion object : KLogging()
}
