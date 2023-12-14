package com.dmitriib.challenge

import androidx.navigation.NavHostController
import com.dmitriib.challenge.DestinationArgs.RECORD_ID_ARG
import com.dmitriib.challenge.Screens.CURRENT_RECORD_SCREEN
import com.dmitriib.challenge.Screens.RECORDS_SCREEN

private object Screens {
    const val RECORDS_SCREEN = "records"
    const val CURRENT_RECORD_SCREEN = "current_record"
}

object DestinationArgs {
    const val RECORD_ID_ARG = "record_id_arg"
}

object Destinations {
    const val RECORDS_ROUTE = RECORDS_SCREEN
    const val CURRENT_RECORD_ROUTE = "$CURRENT_RECORD_SCREEN/{$RECORD_ID_ARG}"
}

class NavigationActions(private val navHostController: NavHostController) {
    fun navigateToRecords() {
        navHostController.navigate(Destinations.RECORDS_ROUTE) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToCurrentRecord(id: Int) {
        navHostController.navigate("$CURRENT_RECORD_SCREEN/$id") {
            launchSingleTop = true
            restoreState = true
        }
    }
}
