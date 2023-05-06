package daggerok.domain.command

import daggerok.api.command.Command
import java.util.UUID

data class RegisterBankAccountCommand(val aggregateId: UUID, val username: String, val password: String) : Command<UUID>
data class ActivateBankAccountCommand(val aggregateId: UUID) : Command<UUID>
