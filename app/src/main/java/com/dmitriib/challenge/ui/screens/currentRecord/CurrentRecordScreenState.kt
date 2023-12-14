package com.dmitriib.challenge.ui.screens.currentRecord

import com.dmitriib.challenge.domain.ImageInfo

// NOTE: Add check of location state before start
// NOTE: Add state for completed walk
sealed interface CurrentRecordScreenState {

    data object Initial : CurrentRecordScreenState

    // Remove permissions from here?
    data class CheckingPermissions(
        val permissions: List<String>,
    ) : CurrentRecordScreenState

    data class RequestingPermissions(
        val permissions: List<String>,
    ) : CurrentRecordScreenState

    data class Started(val images: List<ImageInfo>) : CurrentRecordScreenState

    data class Paused(val images: List<ImageInfo>) : CurrentRecordScreenState

    data class Completed(val images: List<ImageInfo>) : CurrentRecordScreenState
}
