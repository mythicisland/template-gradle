package net.mythicisland.template.shared.repository

interface LoadableRepository<I, E> : Repository<I, E> {

    suspend fun load(): List<E>

}