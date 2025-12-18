package com.joist.echo.domain.di

import com.joist.echo.domain.repository.EchoRepository
import com.joist.echo.domain.usecase.ValidateTextUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    internal fun provideValidateTextUseCase(
        echoRepository: EchoRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ValidateTextUseCase {
        return ValidateTextUseCase(
            echoRepository = echoRepository,
            ioDispatcher = ioDispatcher
        )
    }

}