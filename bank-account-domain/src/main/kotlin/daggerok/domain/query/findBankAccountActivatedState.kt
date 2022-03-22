package daggerok.domain.query

import daggerok.api.Repository
import daggerok.api.query.Query
import daggerok.api.query.QueryHandler
import daggerok.api.query.Result
import daggerok.domain.BackAccountAggregate
import java.util.UUID
import mu.KLogging

data class FindBankAccountActivatedStateQuery(val aggregateId: UUID) : Query
data class FindBankAccountActivatedStateResult(val activated: Boolean) : Result

class FindBankAccountActivatedStateQueryHandler(private val repository: Repository<UUID, BackAccountAggregate>) :
    QueryHandler<FindBankAccountActivatedStateQuery, FindBankAccountActivatedStateResult> {

    override fun handle(query: FindBankAccountActivatedStateQuery): FindBankAccountActivatedStateResult =
        repository
            .also { logger.debug { "handle($query)" } }
            .findByAggregateId(query.aggregateId)
            .let { FindBankAccountActivatedStateResult(it.state.activated) }

    private companion object : KLogging()
}
