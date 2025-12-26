package com.joist.echo.presentation.echo

import com.joist.echo.domain.repository.EchoRepository
import com.joist.echo.domain.usecase.ValidateTextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class EchoViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun `onInputChange updates input and clears error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase =
            ValidateTextUseCase(echoRepository = SuccessEchoRepository(), ioDispatcher = dispatcher)
        val viewModel = EchoViewModel(validateStringUseCase = useCase)

        viewModel.onEvent(EchoEvent.OnInputChange("  hello  "))
        val state = viewModel.uiState.value
        assertEquals("  hello  ", state.input)
        assertNull(state.error)
    }

    @Test
    fun `onSubmit success sets result and clears error`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase =
            ValidateTextUseCase(echoRepository = SuccessEchoRepository(), ioDispatcher = dispatcher)
        val viewModel = EchoViewModel(validateStringUseCase = useCase)

        viewModel.onEvent(EchoEvent.OnInputChange("  hello  "))
        viewModel.onEvent(EchoEvent.OnSubmit)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("hello", state.result)
        assertNull(state.error)
    }

    @Test
    fun `onSubmit failure sets error and clears result`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val useCase =
            ValidateTextUseCase(echoRepository = FailingEchoRepository(), ioDispatcher = dispatcher)
        val viewModel = EchoViewModel(useCase)

        viewModel.onEvent(EchoEvent.OnInputChange("  hello  "))
        viewModel.onEvent(EchoEvent.OnSubmit)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.result)
        assertEquals(
            "The server rejected your input. Try a different value.",
            state.error
        )
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


private class SuccessEchoRepository : EchoRepository {
    override suspend fun validateText(text: String): String = text.trim()
}

private class FailingEchoRepository : EchoRepository {
    override suspend fun validateText(text: String): String {
        throw IOException("The server rejected your input. Try a different value.")
    }
}