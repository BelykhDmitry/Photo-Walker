package com.dmitriib.lazyfeed.ui.screens.currentRecord

import com.dmitriib.lazyfeed.domain.ImageInfo

// NOTE: Add check of location state before start
// NOTE: Add state for completed walk
sealed interface CurrentRecordScreenState {

    val recordId: Int
    val images: List<ImageInfo>

    data class Initial(
        override val images: List<ImageInfo>,
        override val recordId: Int
    ) : CurrentRecordScreenState

    // Remove permissions from here?
    data class CheckingPermissions(
        override val images: List<ImageInfo>,
        override val recordId: Int,
        val permissions: List<String>,
    ) : CurrentRecordScreenState

    data class RequestingPermissions(
        override val images: List<ImageInfo>,
        override val recordId: Int,
        val permissions: List<String>,
    ) : CurrentRecordScreenState

    data class Started(
        override val images: List<ImageInfo>,
        override val recordId: Int
    ) : CurrentRecordScreenState

    data class Paused(
        override val images: List<ImageInfo>,
        override val recordId: Int
    ) : CurrentRecordScreenState

    data class Completed(
        override val images: List<ImageInfo>,
        override val recordId: Int
    ) : CurrentRecordScreenState
}
