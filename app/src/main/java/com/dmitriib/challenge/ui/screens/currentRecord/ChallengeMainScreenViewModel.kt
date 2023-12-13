package com.dmitriib.challenge.ui.screens.currentRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriib.challenge.domain.RecordManager
import com.dmitriib.challenge.domain.RecordState
import com.dmitriib.challenge.ui.permissions.PermissionManager
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChallengeMainScreenViewModel(
    private val id: Int,
    private val permissionManager: PermissionManager,
    private val logger: Logger,
    private val recordManager: RecordManager
) : ViewModel() {

    private var job: Job? = null

    init {
        job = observeState()
    }

    private val _currentRecordScreenStateFlow: MutableStateFlow<CurrentRecordScreenState> = MutableStateFlow(
        CurrentRecordScreenState.Initial
    )
    val currentRecordScreenStateFlow: StateFlow<CurrentRecordScreenState> = _currentRecordScreenStateFlow

    fun checkPermissionsResult(permissions: Map<String, Boolean>) {
        if (permissionManager.onPermissionResult(permissions)) {
            recordManager.startRecord()
//            createRecord()
        } else {
            _currentRecordScreenStateFlow.update {
                CurrentRecordScreenState.RequestingPermissions(
                    permissionManager.getRequiredPermissions()
                )
            }
        }
    }

    fun requestPermissionsResult(permissions: Map<String, Boolean>) {
        if (permissionManager.onPermissionResult(permissions)) {
            recordManager.startRecord()
//            createRecord()
        } else {
            _currentRecordScreenStateFlow.update {
                CurrentRecordScreenState.Initial
            }
        }
    }

    fun onActionButtonClicked(userAction: RecordUserAction) {
        reduceUserAction(userAction)
    }

    private fun reduceUserAction(userAction: RecordUserAction) {
        val currentState = _currentRecordScreenStateFlow.value
        when (userAction) {
            RecordUserAction.Create -> when (currentState) {
                CurrentRecordScreenState.Initial,
                is CurrentRecordScreenState.Completed -> checkPermissionsState()
                else -> Unit
            }
            RecordUserAction.Pause -> recordManager.pauseRecord()
            RecordUserAction.Resume -> recordManager.resumeRecord()
            RecordUserAction.Start -> recordManager.startRecord()
            RecordUserAction.Complete -> recordManager.completeRecord()
        }
    }

    private fun checkPermissionsState() {
        _currentRecordScreenStateFlow.update {
            CurrentRecordScreenState.CheckingPermissions(
                permissionManager.getRequiredPermissions()
            )
        }
    }

    private fun observeState(): Job {
        return viewModelScope.launch {
            recordManager.getRecordStatusFlow()
                .onStart {
                    logger.d("VM: on start")
                }
                .onCompletion {
                    logger.d("VM: on complete")
                }
                .onEach {
                    logger.d("new state before all $it")
                }
                .catch { t ->
                    logger.d("Error occurred while observing record status", t)
                }.collect { state ->
                    logger.d("On new state: $state")
                    val newState = when (state) {
                        is RecordState.Completed -> CurrentRecordScreenState.Completed(state.images)
                        is RecordState.Created -> CurrentRecordScreenState.Initial
                        // TODO: remove after refactoring
                        is RecordState.NoCurrent -> CurrentRecordScreenState.Initial
                        is RecordState.Paused -> CurrentRecordScreenState.Paused(state.images)
                        is RecordState.Started -> CurrentRecordScreenState.Started(state.images)
                    }
                    _currentRecordScreenStateFlow.update {
                        newState
                    }
                }
        }
    }
}
