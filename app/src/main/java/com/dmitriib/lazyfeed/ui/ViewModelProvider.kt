package com.dmitriib.lazyfeed.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dmitriib.lazyfeed.ChallengeApplication
import com.dmitriib.lazyfeed.DestinationArgs
import com.dmitriib.lazyfeed.ui.screens.currentRecord.ChallengeMainScreenViewModel
import com.dmitriib.lazyfeed.ui.screens.records.RecordsScreenViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val id = createSavedStateHandle().get<String>(DestinationArgs.RECORD_ID_ARG)!!.toInt()
            with(application().appContainer) {
                ChallengeMainScreenViewModel(
                    permissionManager,
                    logger,
                    recordManagerFactory.create(id)
                )
            }
        }
    }
    val RecordsListFactory = viewModelFactory {
        initializer {
            with(application().appContainer) {
                RecordsScreenViewModel(
                    permissionManager,
                    logger,
                    recordsRepository
                )
            }
        }
    }
}

private fun CreationExtras.application(): ChallengeApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChallengeApplication
}
