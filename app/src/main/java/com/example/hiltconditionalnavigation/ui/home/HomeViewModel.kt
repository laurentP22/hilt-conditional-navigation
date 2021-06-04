package com.example.hiltconditionalnavigation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltconditionalnavigation.data.repository.UserRepository
import com.example.hiltconditionalnavigation.data.result.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _openProfile = MutableLiveData<Event<Unit>>()
    val openProfile: LiveData<Event<Unit>>; get() = _openProfile

    fun openProfile() {
        _openProfile.value = Event(Unit)
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}