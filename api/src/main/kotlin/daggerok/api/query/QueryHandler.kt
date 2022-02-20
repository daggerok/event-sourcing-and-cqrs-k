package daggerok.api.query

interface QueryHandler<Q : Query, R : Result> {
    fun handle(query: Q): R
}
