package com.example.newspulse.utils

sealed class ResultState<out T> {
    class Success<T>(val data: T) : ResultState<T>()
    class Error(val error: String) : ResultState<Nothing>()
    data object Loading : ResultState<Nothing>()
    data object Idle : ResultState<Nothing>()
}