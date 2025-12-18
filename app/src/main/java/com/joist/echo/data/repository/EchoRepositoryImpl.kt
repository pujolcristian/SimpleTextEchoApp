package com.joist.echo.data.repository

import com.joist.echo.domain.repository.EchoRepository
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject


/**
 * Repository that **simulates** a remote validation Service.
 *
 * Behavior:
 * - Adds ~400 ms artificial latency.
 * - Trims the incoming [text].
 * - Throws [IllegalArgumentException] if the trimmed text is empty.
 * - Throws [IOException] if the trimmed text contains `"fail"` (case-insensitive).
 * - Returns the trimmed text on success.
 *
 * Rationale: keeps the project small, deterministic, and unit-test friendly
 * without real networking.
 */
internal class EchoRepositoryImpl @Inject constructor() : EchoRepository {
    override suspend fun validateText(text: String): String {
        delay(400)

        val cleaned = text.trim()
        if (cleaned.isEmpty()) {
            throw IllegalArgumentException("Input cannot be empty.")
        }
        if (cleaned.contains("fail", ignoreCase = true)) {
            throw IOException("The server rejected your input. Try a different value.")
        }
        return cleaned
    }
}