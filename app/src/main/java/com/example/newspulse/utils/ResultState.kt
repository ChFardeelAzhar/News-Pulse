package com.example.newspulse.utils

sealed class ResultState<out T> {
    class Success<T>(val data: T) : ResultState<T>()
    class Error(val error: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}