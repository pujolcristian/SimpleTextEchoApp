package com.joist.echo.data.di

import com.joist.echo.data.repository.EchoRepositoryImpl
import com.joist.echo.domain.repository.EchoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    internal abstract fun bindEchoRepository(
        impl: EchoRepositoryImpl,
    ): EchoRepository

}