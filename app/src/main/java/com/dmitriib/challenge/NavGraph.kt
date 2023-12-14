package com.dmitriib.challenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dmitriib.challenge.ui.screens.currentRecord.CurrentRecordScreen
import com.dmitriib.challenge.ui.screens.records.RecordsScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.RECORDS_ROUTE,
    navActions: NavigationActions = remember(navController) {
        NavigationActions(navController)
    }
) {
//    val currentNavBacStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentNavBacStackEntry?.destination?.route ?: startDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            Destinations.RECORDS_ROUTE
        ) {
            RecordsScreen(
                onNewRecord = { id ->
                    navActions.navigateToCurrentRecord(id)
                },
                onRecordClicked = { id ->
                    navActions.navigateToCurrentRecord(id)
                }
            )
        }
        composable(
            Destinations.CURRENT_RECORD_ROUTE
        ) {
            CurrentRecordScreen(onReturnBackClicked = { navController.popBackStack() })
        }
    }
}