package com.example.hiltconditionalnavigation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltconditionalnavigation.data.repository.UserRepository
import com.example.hiltconditionalnavigation.data.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _login = MutableLiveData<Result<Unit>>()
    val login: LiveData<Result<Unit>>; get() = _login

    fun authenticate() {
        viewModelScope.launch {
            userRepository.signIn().collect { _login.value = it }
        }
    }
}