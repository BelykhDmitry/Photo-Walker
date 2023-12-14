package com.dmitriib.challenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dmitriib.challenge.ui.theme.DmitriiBelykhChallengeTheme
import java.io.Serializable

class MainActivity : ComponentActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("TEEEST", "new intent: $intent")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DmitriiBelykhChallengeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    val navActions = remember(navController) {
                        NavigationActions(navController)
                    }
                    NavGraph(navController = navController, navActions = navActions)
                    if (savedInstanceState == null &&
                        intent.hasExtra(KEY_FROM_SERVICE)) {
                        navActions.navigateToCurrentRecord(intent.getIntExtra(KEY_FROM_SERVICE, -1))
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_FROM_SERVICE = "from_service"
    }
}
