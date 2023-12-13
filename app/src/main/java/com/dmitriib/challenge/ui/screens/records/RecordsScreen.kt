package com.dmitriib.challenge.ui.screens.records

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dmitriib.challenge.data.local.RecordItem
import com.dmitriib.challenge.ui.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    onNewRecord: (Int) -> Unit,
    onRecordClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordsScreenViewModel = viewModel(
        factory = ViewModelProvider.RecordsListFactory
    ),
) {
    val state = viewModel.screenStateFlow.collectAsState().value

    PermissionsEffect(
        state,
        { viewModel.onUserAction(RecordsUserAction.CheckPermissionsResult(it)) },
        { viewModel.onUserAction(RecordsUserAction.RequestPermissionsResult(it)) }
    )

    if (state is RecordsScreenState.RecordCreated) {
        onNewRecord(state.recordId)
        viewModel.onUserAction(RecordsUserAction.NewRecordOpened)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
             TopAppBar(title = { Text(text = "Walking Man") })
        },
        floatingActionButton = {
            Fab(isInProgress = state is RecordsScreenState.CreatingNewRecord) {
                viewModel.onUserAction(RecordsUserAction.FabClick)
            }
        }
    ) { contentPadding ->
        Box(modifier = modifier
            .padding(contentPadding)
            .fillMaxSize()) {
            RecordsList(items = state.items, onClick = viewModel::onUserAction)
        }
    }
}

@Composable
private fun PermissionsEffect(
    state: RecordsScreenState,
    checkPermissionsResult: (Map<String, Boolean>) -> Unit,
    requestPermissionResult: (Map<String, Boolean>) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        requestPermissionResult
    )
    val context = LocalContext.current
    when (state) {
        is RecordsScreenState.CheckingPermissions -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                state.permissions.associateWith {
                    (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED)
                }
                    .let {
                        checkPermissionsResult(it)
                    }
            } else {
                checkPermissionsResult(state.permissions.associateWith { true })
            }
        }

        is RecordsScreenState.RequestingPermissions -> {
            SideEffect { launcher.launch(state.permissions.toTypedArray()) }
        }

        else -> Unit
    }
}

@Composable
fun RecordsList(
    items: List<RecordItem>,
    onClick: (RecordsUserAction.ItemClick) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        items(items) {
            RecordItem(it, { id -> onClick(RecordsUserAction.ItemClick(id)) })
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordItem(
    item: RecordItem,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(item.id) },
        modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(
            text = "Record Id: ${item.id}",
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun Fab(
    isInProgress: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
    ) {
        if (isInProgress) {
            CircularProgressIndicator()
        } else {
            Icon(Icons.Filled.Add, "Floating action button.")
        }
    }
}
