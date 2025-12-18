package com.joist.echo.presentation.echo

internal sealed class EchoEvent {
    data class OnInputChange(val input: String) : EchoEvent()
    data object OnSubmit : EchoEvent()
}