package com.dmitriib.lazyfeed.ui.screens.currentRecord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmitriib.lazyfeed.domain.RecordManager
import com.dmitriib.lazyfeed.domain.RecordState
import com.dmitriib.lazyfeed.ui.permissions.PermissionManager
import com.dmitriib.lazyfeed.utils.Logger
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
    private val permissionManager: PermissionManager,
    private val logger: Logger,
    private val recordManager: RecordManager,
) : ViewModel() {

    private var job: Job? = null

    init {
        logger.d("View Model created $this")
        job = observeState()
    }

    private val _currentRecordScreenStateFlow: MutableStateFlow<CurrentRecordScreenState> = MutableStateFlow(
        CurrentRecordScreenState.Initial(emptyList(), -1)
    )
    val currentRecordScreenStateFlow: StateFlow<CurrentRecordScreenState> = _currentRecordScreenStateFlow

//    fun checkPermissionsResult(permissions: Map<String, Boolean>) {
//        if (permissionManager.onPermissionResult(permissions)) {
//            recordManager.startRecord()
//        } else {
//            _currentRecordScreenStateFlow.update {
//                CurrentRecordScreenState.RequestingPermissions(
//                    permissionManager.getRequiredPermissions()
//                )
//            }
//        }
//    }

//    fun requestPermissionsResult(permissions: Map<String, Boolean>) {
//        if (permissionManager.onPermissionResult(permissions)) {
//            recordManager.startRecord()
//        } else {
//            _currentRecordScreenStateFlow.update {
//                CurrentRecordScreenState.Initial
//            }
//        }
//    }

    fun onActionButtonClicked(userAction: RecordUserAction) {
        reduceUserAction(userAction)
    }

    private fun reduceUserAction(userAction: RecordUserAction) {
        when (userAction) {
            RecordUserAction.Pause -> recordManager.pauseRecord()
            RecordUserAction.Resume -> recordManager.resumeRecord()
            RecordUserAction.Start -> recordManager.startRecord()
            RecordUserAction.Complete -> recordManager.completeRecord()
        }
    }

//    private fun checkPermissionsState() {
//        _currentRecordScreenStateFlow.update {
//            CurrentRecordScreenState.CheckingPermissions(
//                permissionManager.getRequiredPermissions()
//            )
//        }
//    }

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
                        is RecordState.Completed -> CurrentRecordScreenState.Completed(state.images, state.recordId)
                        is RecordState.Created -> CurrentRecordScreenState.Initial(state.images, state.recordId)
                        is RecordState.Paused -> CurrentRecordScreenState.Paused(state.images, state.recordId)
                        is RecordState.Started -> CurrentRecordScreenState.Started(state.images, state.recordId)
                    }
                    _currentRecordScreenStateFlow.update {
                        newState
                    }
                }
        }
    }
}
