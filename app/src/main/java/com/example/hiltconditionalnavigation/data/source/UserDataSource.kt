package com.example.hiltconditionalnavigation.data.source

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        const val AUTH = "AUTH"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _channelAuthState = ConflatedBroadcastChannel<Boolean>()
    private var isListeningUser = false

    private val _mainSharedPref = context
        .getSharedPreferences("com.example.hiltconditionalnavigation", Context.MODE_PRIVATE)

    fun getAuthState(): Boolean = _mainSharedPref.getBoolean(AUTH, false)

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun listenAuthState(): Flow<Boolean> {
        if (!isListeningUser) {
            isListeningUser = true
            _channelAuthState.offer(_mainSharedPref.getBoolean(AUTH, false))
        }
        return _channelAuthState.asFlow()
    }

    @ExperimentalCoroutinesApi
    suspend fun authenticate(auth: Boolean) {
        _mainSharedPref.edit { putBoolean(AUTH, auth) }
        _channelAuthState.offer(auth)
        delay(500) // simulating delay
    }
}