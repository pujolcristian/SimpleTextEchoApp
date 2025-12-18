package com.joist.echo.domain.usecase

import com.joist.echo.domain.repository.EchoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case that validates user input by delegating to [EchoRepository].
 *
 * Keeps business intent explicit so UI and data layers remain simple and testable.
 *
 * @param echoRepository abstraction over the validation source (fake "server").
 */
class ValidateTextUseCase @Inject constructor(
    private val echoRepository: EchoRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(text: String) = withContext(context = ioDispatcher) {
        echoRepository.validateText(text = text)
    }
}