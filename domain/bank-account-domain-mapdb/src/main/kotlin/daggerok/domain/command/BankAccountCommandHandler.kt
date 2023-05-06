package daggerok.domain.command

import daggerok.api.Repository
import daggerok.api.command.Command
import daggerok.api.command.CommandHandler
import daggerok.domain.BackAccountAggregate
import java.util.UUID
import mu.KLogging

class BankAccountCommandHandler(private val repository: Repository<UUID, BackAccountAggregate>) :
    CommandHandler<UUID, BackAccountAggregate> {

    override fun handle(command: Command<UUID>): BackAccountAggregate =
        logger.debug { "handle(command=$command)" }
            .let {
                when (command) {
                    is RegisterBankAccountCommand -> handleRegisterBankAccountCommand(command)
                    is ActivateBankAccountCommand -> handleActivateBankAccountCommand(command)
                    else -> handleUnknownCommand(command)
                }
            }

    private fun handleRegisterBankAccountCommand(command: RegisterBankAccountCommand): BackAccountAggregate =
        logger.debug { "handleRegisterBankAccountCommand(command=$command)" }
            .let { repository.findByAggregateId(command.aggregateId) }
            .apply { registerBankAccount(command.aggregateId, command.username, command.password) }
            .let(repository::save)

    private fun handleActivateBankAccountCommand(command: ActivateBankAccountCommand): BackAccountAggregate =
        logger.debug { "handleActivateBankAccountCommand(command=$command)" }
            .let { repository.findByAggregateId(command.aggregateId) }
            .apply { activateBankAccount(command.aggregateId) }
            .let(repository::save)

    private fun handleUnknownCommand(command: Command<UUID>): BackAccountAggregate =
        logger.debug { "handleUnknownCommand(command=$command)" }
            .let { throw RuntimeException("Unsupported command: $command") }

    private companion object : KLogging()
}
