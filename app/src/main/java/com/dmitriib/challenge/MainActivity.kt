package com.dmitriib.challenge

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dmitriib.challenge.ui.ViewModelProvider
import com.dmitriib.challenge.ui.screens.ChallengeMainScreen
import com.dmitriib.challenge.ui.screens.ChallengeMainScreenViewModel
import com.dmitriib.challenge.ui.screens.MainScreenState
import com.dmitriib.challenge.ui.services.LocationService
import com.dmitriib.challenge.ui.theme.DmitriiBelykhChallengeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DmitriiBelykhChallengeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ChallengeMainScreenViewModel = viewModel(
                        factory = ViewModelProvider.Factory
                    )
                    val state by viewModel.mainScreenStateFlow.collectAsState()

                    LocationServiceEffect(state)
                    PermissionsEffect(
                        state,
                        viewModel::checkPermissionsResult,
                        viewModel::requestPermissionsResult
                    )

                    ChallengeMainScreen(state, viewModel::onActionButtonClicked)
                }
            }
        }
    }

    @Composable
    private fun LocationServiceEffect(state: MainScreenState) {
        // NOTE: doesn't work if service was not started by system.
        var serviceStarted by rememberSaveable { mutableStateOf(false) }
        when (state) {
            MainScreenState.Created,
            is MainScreenState.WalkPaused,
            is MainScreenState.WalkInProgress -> if (!serviceStarted) {
                SideEffect {
                    serviceStarted = true
                    startService()
                }
            }

            is MainScreenState.CheckingPermissions,
            MainScreenState.Initial,
            is MainScreenState.RequestingPermissions,
            is MainScreenState.Stopped -> if (serviceStarted) {
                SideEffect {
                    stopService()
                    serviceStarted = false
                }
            }
        }
    }

    private fun startService() {
        startService(Intent(this, LocationService::class.java))
    }

    private fun stopService() {
        stopService(Intent(this, LocationService::class.java))
    }

    @Composable
    private fun PermissionsEffect(
        state: MainScreenState,
        checkPermissionsResult: (Map<String, Boolean>) -> Unit,
        requestPermissionResult: (Map<String, Boolean>) -> Unit
    ) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            requestPermissionResult
        )
        when (state) {
            is MainScreenState.CheckingPermissions -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    state.permissions.associateWith {
                        (checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED)
                    }
                        .let {
                            checkPermissionsResult(it)
                        }
                } else {
                    checkPermissionsResult(state.permissions.associateWith { true })
                }
            }

            is MainScreenState.RequestingPermissions -> {
                SideEffect { launcher.launch(state.permissions.toTypedArray()) }
            }

            else -> Unit
        }
    }
}
