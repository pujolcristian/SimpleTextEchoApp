package com.joist.echo.domain.usecase

import com.joist.echo.domain.repository.EchoRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

private class SuccessEchoRepository : EchoRepository {
    override suspend fun validateText(text: String): String = text.trim()
}

private class FailingEchoRepository : EchoRepository {
    override suspend fun validateText(text: String): String {
        throw IOException("exception")
    }
}

class ValidateStringUseCaseTest {

    @Test
    fun `invoke returns repository value on success`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase =
            ValidateTextUseCase(echoRepository = SuccessEchoRepository(), ioDispatcher = dispatcher)
        val result = useCase("  test value  ")
        assertEquals("test value", result)
    }

    @Test
    fun `invoke rethrows repository error`() {
        assertThrows(IOException::class.java) {
            runTest {
                val dispatcher = StandardTestDispatcher(testScheduler)
                val useCase =
                    ValidateTextUseCase(
                        echoRepository = FailingEchoRepository(),
                        ioDispatcher = dispatcher
                    )
                useCase("anything")
            }
        }
    }
}