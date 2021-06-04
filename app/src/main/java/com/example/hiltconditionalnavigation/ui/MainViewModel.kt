package com.example.hiltconditionalnavigation.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltconditionalnavigation.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _user = MutableLiveData<Boolean>()
    val user: LiveData<Boolean>; get() = _user

    init {
        viewModelScope.launch {
            userRepository.listenAuthState().collect {
                _user.value = it
            }
        }
    }
}