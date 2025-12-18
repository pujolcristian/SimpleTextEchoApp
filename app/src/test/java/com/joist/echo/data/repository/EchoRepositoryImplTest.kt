package com.joist.echo.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class EchoRepositoryImplTest {

    @Test
    fun `validateString returns trimmed text on success`() = runTest {
        val repo = EchoRepositoryImpl()
        val result = repo.validateText("   hello world   ")
        assertEquals("hello world", result)
    }

    @Test
    fun `validateString throws IllegalArgumentException for empty input`() {
        val repo = EchoRepositoryImpl()
        val ex = assertThrows(IllegalArgumentException::class.java) {
            runTest { repo.validateText("    ") }
        }
        assertEquals("Input cannot be empty.", ex.message)
    }

    @Test
    fun `validateString throws IOException when input contains fail`() {
        val repo = EchoRepositoryImpl()
        val ex = assertThrows(IOException::class.java) {
            runTest { repo.validateText("please FAIL this") }
        }
        // Keep the assertion aligned with your message; adjust if you change the copy
        assertEquals("The server rejected your input. Try a different value.", ex.message)
    }
}