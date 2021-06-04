package com.example.hiltconditionalnavigation.data

import com.example.hiltconditionalnavigation.data.repository.UserRepository
import com.example.hiltconditionalnavigation.data.result.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeUserRepository @Inject constructor() : UserRepository {
    var auth = false

    override fun getAuthState(): Boolean {
        TODO("Not yet implemented")
    }

    override fun listenAuthState(): Flow<Boolean> {
        return flow {
            emit(auth)
        }
    }

    override suspend fun signIn(): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }
}