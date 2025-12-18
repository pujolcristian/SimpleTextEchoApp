package com.joist.echo.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppGraphRoutes {
    @Serializable
    data object HomeRoute : AppGraphRoutes()

}