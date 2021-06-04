package com.example.hiltconditionalnavigation.data.repository


import com.example.hiltconditionalnavigation.data.result.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * Returns true or false if the user is authenticated.
     */
    fun getAuthState(): Boolean

    /**
     * Listens to the authentication's state of the user.
     */
    fun listenAuthState(): Flow<Boolean>

    /**
     * Signs in the user.
     */
    suspend fun signIn(): Flow<Result<Unit>>

    /**
     * Signs out the user.
     */
    suspend fun signOut()
}