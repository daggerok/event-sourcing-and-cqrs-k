package daggerok.api

import daggerok.api.event.DomainEvent

interface State<ID, E : DomainEvent<ID>> {
    fun mutate(event: E): State<ID, E>
}
