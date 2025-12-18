package com.joist.echo.presentation.echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joist.echo.domain.usecase.ValidateTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Presentation logic for the Echo screen (MVVM).
 *
 * Exposes immutable [uiState] with:
 * - `input`: current text field value
 * - `isLoading`: in-flight submission flag
 * - `result`: last successful echo (trimmed)
 * - `error`: last user-visible error message
 *
 * Events:
 * - [EchoEvent.OnInputChange] (legacy name): updates input.
 * - [EchoEvent.OnSubmit]: triggers validation.
 *
 * Concurrency: prevents re-entrance while loading.
 */
@HiltViewModel
internal class EchoViewModel @Inject constructor(
    private val validateStringUseCase: ValidateTextUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EchoUiState())
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: EchoEvent) {
        when (event) {
            is EchoEvent.OnInputChange -> handleInputChange(newValue = event.input)
            is EchoEvent.OnSubmit -> handleSubmit()
        }
    }

    private fun handleInputChange(newValue: String) {
        _uiState.value = _uiState.value.copy(input = newValue, error = null)
    }

    private fun handleSubmit() = viewModelScope.launch {
        if (_uiState.value.isLoading) return@launch
        _uiState.update { it.copy(isLoading = true, result = null, error = null) }

        val result = runCatching {
            validateStringUseCase(_uiState.value.input)
        }

        result.fold(
            onSuccess = {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        result = it,
                        error = null
                    )
                }
            },
            onFailure = {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        result = null,
                        error = it.message ?: "Unknown error"
                    )
                }
            }
        )
    }
}