package com.joist.echo.presentation.echo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joist.echo.R
import com.joist.echo.presentation.components.CustomTextField
import com.joist.echo.presentation.components.ResultCard

/**
 * Top-level Echo screen.
 *
 * Collects [EchoViewModel.uiState] and renders:
 * - Title header
 * - [InputCard] (text field + submit button)
 * - [ResultCard] when a successful echo is available
 *
 * Layout is keyboard-safe via `verticalScroll + imePadding`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EchoScreen(
    viewModel: EchoViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(all = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.title_text_echo),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        InputCard(
            value = state.input,
            isLoading = state.isLoading,
            errorMessage = state.error,
            onChanged = { newValue ->
                viewModel.onEvent(event = EchoEvent.OnInputChange(input = newValue))
            },
            onSubmit = { viewModel.onEvent(event = EchoEvent.OnSubmit) }
        )

        ResultCard(visible = state.result != null, text = state.result.orEmpty())

    }
}


@Composable
private fun InputCard(
    value: String,
    isLoading: Boolean,
    errorMessage: String?,
    onChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                placeholder = stringResource(R.string.placeholder_input_text),
                label = stringResource(R.string.label_validate_text),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                enabled = !isLoading,
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                onValueChange = onChanged,
                errorMessage = errorMessage
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSubmit,
                enabled = !isLoading && value.isNotBlank(),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(size = 18.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                }
                Text(
                    text = if (isLoading) stringResource(R.string.action_submitting) else stringResource(
                        R.string.action_submit
                    )
                )
            }
        }
    }
}
