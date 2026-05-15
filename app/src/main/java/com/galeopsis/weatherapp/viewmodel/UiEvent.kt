package com.galeopsis.weatherapp.viewmodel

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
}
