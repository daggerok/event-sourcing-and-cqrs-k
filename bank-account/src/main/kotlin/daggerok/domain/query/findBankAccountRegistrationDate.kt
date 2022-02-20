package daggerok.domain.query

import daggerok.api.Repository
import daggerok.api.query.Query
import daggerok.api.query.QueryHandler
import daggerok.api.query.Result
import daggerok.domain.BackAccountAggregate
import java.time.Instant
import java.util.UUID
import mu.KLogging

data class FindBankAccountRegistrationDateQuery(val aggregateId: UUID) : Query
data class FindBankAccountRegistrationDateResult(val registeredAt: Instant) : Result

class FindBankAccountRegistrationDateQueryHandler(private val repository: Repository<UUID, BackAccountAggregate>) :
    QueryHandler<FindBankAccountRegistrationDateQuery, FindBankAccountRegistrationDateResult> {

    override fun handle(query: FindBankAccountRegistrationDateQuery): FindBankAccountRegistrationDateResult =
        repository
            .also { logger.debug { "handle($query)" } }
            .findByAggregateId(query.aggregateId)
            .let { FindBankAccountRegistrationDateResult(it.state.registeredAt) }

    private companion object : KLogging()
}
