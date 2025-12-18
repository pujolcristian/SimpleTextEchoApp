package com.joist.echo.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.joist.echo.presentation.echo.EchoScreen

@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = AppGraphRoutes.HomeRoute,
    ) {
        composable<AppGraphRoutes.HomeRoute> {
            EchoScreen()
        }
    }
}