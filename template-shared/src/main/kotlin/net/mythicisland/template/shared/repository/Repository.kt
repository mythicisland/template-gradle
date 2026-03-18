package net.mythicisland.template.shared.repository

interface Repository<I, E> {

    suspend fun delete(element: E): Boolean

    suspend fun save(element: E)

    suspend fun find(identifier: I): E?

    suspend fun getAll(): List<E>

}