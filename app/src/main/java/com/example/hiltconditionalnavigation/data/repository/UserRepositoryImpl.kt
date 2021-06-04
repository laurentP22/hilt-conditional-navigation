package com.example.hiltconditionalnavigation.data.repository

import com.example.hiltconditionalnavigation.data.result.Result
import com.example.hiltconditionalnavigation.data.source.UserDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val ioDispatcher: CoroutineDispatcher
) : UserRepository {

    override fun getAuthState(): Boolean = userDataSource.getAuthState()

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun listenAuthState(): Flow<Boolean> =
        userDataSource.listenAuthState().flowOn(ioDispatcher)

    @ExperimentalCoroutinesApi
    override suspend fun signIn() = flow {
        emit(Result.loading())
        userDataSource.authenticate(true)
        emit(Result.success(Unit))
    }.catch {
        emit(Result.error(it.message))
    }.flowOn(ioDispatcher)

    @ExperimentalCoroutinesApi
    override suspend fun signOut() = withContext(ioDispatcher) {
        userDataSource.authenticate(false)
    }
}