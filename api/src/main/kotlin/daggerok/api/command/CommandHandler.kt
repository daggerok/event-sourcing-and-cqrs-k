package daggerok.api.command

import daggerok.api.Aggregate

interface CommandHandler<ID, A : Aggregate<ID>> {
    fun handle(command: Command<ID>): A
}
