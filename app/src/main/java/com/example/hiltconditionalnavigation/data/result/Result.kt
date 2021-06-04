package com.example.hiltconditionalnavigation.data.result

import androidx.lifecycle.Observer


data class Result<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Result<T> = Result(Status.SUCCESS, data, null)
        fun <T> error(msg: String?): Result<T> = Result(Status.ERROR, null, msg)
        fun <T> loading(): Result<T> = Result(Status.LOADING, null, null)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): Result<T>? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            Result(status, data, message)
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T? = data
}

class ResultObserver<T>(private val onEventUnhandledContent: (Result<T>) -> Unit) :
    Observer<Result<T>> {
    override fun onChanged(event: Result<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}
