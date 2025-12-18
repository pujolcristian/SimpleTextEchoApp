package com.joist.echo.presentation.echo

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.joist.echo.R
import com.joist.echo.domain.repository.EchoRepository
import com.joist.echo.domain.usecase.ValidateTextUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class EchoScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun echoViewModel(repo: EchoRepository): EchoViewModel {
        val testDispatcher = UnconfinedTestDispatcher()
        val useCase = ValidateTextUseCase(echoRepository = repo, ioDispatcher = testDispatcher)
        return EchoViewModel(validateStringUseCase = useCase)
    }

    @Test
    fun button_is_disabled_when_input_empty_and_enabled_when_not() {
        val viewModel = echoViewModel(object : EchoRepository {
            override suspend fun validateText(text: String) = text
        })

        composeRule.setContent {
            MaterialTheme { EchoScreen(viewModel = viewModel) }
        }

        val submit = composeRule.activity.getString(R.string.action_submit)
        composeRule.onNodeWithText(submit).assertIsDisplayed().assertIsNotEnabled()

        // Type some text into the only text field on the screen
        composeRule.onNode(hasSetTextAction()).performTextInput("hello")

        composeRule.onNodeWithText(submit).assertIsEnabled()
    }

    @Test
    fun successful_submit_shows_success_and_result_text() {
        val viewModel = echoViewModel(object : EchoRepository {
            override suspend fun validateText(text: String) = text.trim()
        })

        composeRule.setContent {
            MaterialTheme { EchoScreen(viewModel = viewModel) }
        }

        val submit = composeRule.activity.getString(R.string.action_submit)
        val success = composeRule.activity.getString(R.string.status_success)

        // Enter text and submit
        composeRule.onNode(hasSetTextAction()).performTextInput("  Hello Echo  ")
        composeRule.onNodeWithText(submit).performClick()

        // Wait until "Success" appears
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText(success).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText(success).assertIsDisplayed()
        composeRule.onNodeWithText("Hello Echo").assertIsDisplayed() // trimmed result text
    }

    @Test
    fun failing_submit_shows_error_message() {
        val errorMessage = "The server rejected your input. Try a different value."
        val viewModel = echoViewModel(object : EchoRepository {
            override suspend fun validateText(text: String): String {
                throw IOException(errorMessage)
            }
        })

        composeRule.setContent {
            MaterialTheme { EchoScreen(viewModel = viewModel) }
        }

        val submit = composeRule.activity.getString(R.string.action_submit)

        composeRule.onNode(hasSetTextAction()).performTextInput("trigger")
        composeRule.onNodeWithText(submit).performClick()

        // Wait until error text is visible (CustomTextField should render it)
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText(errorMessage).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }
}