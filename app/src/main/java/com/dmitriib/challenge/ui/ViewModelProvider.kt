package com.dmitriib.challenge.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dmitriib.challenge.ChallengeApplication
import com.dmitriib.challenge.ui.screens.currentRecord.ChallengeMainScreenViewModel
import com.dmitriib.challenge.ui.screens.records.RecordsScreenViewModel

object ViewModelProvider {
    fun Factory(id: Int) = viewModelFactory {
        initializer {
            with(application().appContainer) {
                ChallengeMainScreenViewModel(
                    id,
                    permissionManager,
                    logger,
                    recordManager
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
                    recordManager
                )
            }
        }
    }
}

private fun CreationExtras.application(): ChallengeApplication {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChallengeApplication
}
