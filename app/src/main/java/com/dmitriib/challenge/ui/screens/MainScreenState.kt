package com.dmitriib.challenge.ui.screens

import com.dmitriib.challenge.domain.ImageInfo

// NOTE: Add check of location state before start
sealed interface MainScreenState {

    data object Initial : MainScreenState

    data class CheckingPermissions(
        val permissions: List<String>,
    ) : MainScreenState

    data class RequestingPermissions(
        val permissions: List<String>,
    ) : MainScreenState

    data class WalkInProgress(val images: List<ImageInfo>) : MainScreenState

    data class WalkPaused(val images: List<ImageInfo>) : MainScreenState
}
