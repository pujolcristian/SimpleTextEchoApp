package com.joist.echo.domain.repository

interface EchoRepository {
    suspend fun validateText(text: String): String
}