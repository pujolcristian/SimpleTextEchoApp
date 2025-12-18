package com.joist.echo.presentation.echo

internal data class EchoUiState(
    val input: String = "",
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null,
)