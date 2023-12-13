package com.dmitriib.challenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dmitriib.challenge.ui.screens.currentRecord.CurrentRecordScreen
import com.dmitriib.challenge.ui.screens.records.RecordsScreen
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
                    val initialState = if (intent.hasExtra(KEY_FROM_SERVICE)) CurrentScreen.Record(0)
                    else CurrentScreen.Records
                    Log.d("t", " onCreate intent: $intent")
                    var currentScreen by rememberSaveable {
                        mutableStateOf(initialState)
                    }
                    when (currentScreen) {
                        CurrentScreen.Records -> RecordsScreen(
                            onNewRecord = { id ->
                                currentScreen = CurrentScreen.Record(id)
                            },
                            onRecordClicked = { id ->

                            }
                        )
                        is CurrentScreen.Record -> CurrentRecordScreen(
                            (currentScreen as CurrentScreen.Record).id,
                            onReturnBackClicked = {
                                currentScreen = CurrentScreen.Records
                            }
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_FROM_SERVICE = "from_service"
    }
}

sealed interface CurrentScreen : Serializable {
    data object Records : CurrentScreen
    data class Record(val id: Int) : CurrentScreen
}
