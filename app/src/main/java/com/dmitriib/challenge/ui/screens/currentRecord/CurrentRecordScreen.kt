package com.dmitriib.challenge.ui.screens.currentRecord

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dmitriib.dmitrii_belykh_challenge.R
import com.dmitriib.challenge.domain.ImageInfo
import com.dmitriib.challenge.ui.ViewModelProvider
import com.dmitriib.challenge.ui.services.LocationService
import com.dmitriib.challenge.ui.theme.DmitriiBelykhChallengeTheme

@Composable
fun CurrentRecordScreen(
    id: Int,
    onReturnBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChallengeMainScreenViewModel = viewModel(
        key = "$id",
        factory = ViewModelProvider.Factory(id)
    )
    val screenState by viewModel.currentRecordScreenStateFlow.collectAsState()

    BackHandler {
        if (screenState is CurrentRecordScreenState.Completed ||
            screenState is CurrentRecordScreenState.Initial) onReturnBackClicked()
    }

    LocationServiceEffect(screenState, id)

    RecordScreenContent(viewModel, screenState, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreenContent(
    viewModel: ChallengeMainScreenViewModel,
    screenState: CurrentRecordScreenState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                        Text(text = "Record #${screenState.recordId}")
                },
                actions = {
                    Actions(state = screenState, onClick = viewModel::onActionButtonClicked)
                }
            )
        }
    ) { contentPadding ->
        PhotoFeed(screenState.images, Modifier.padding(contentPadding))
    }
}

@Composable
private fun LocationServiceEffect(state: CurrentRecordScreenState, id: Int) {
    // NOTE: doesn't work if service was not started by system.
    var serviceStarted by rememberSaveable { mutableStateOf(false) }
    val context: Context = LocalContext.current
    when (state) {
        is CurrentRecordScreenState.Paused,
        is CurrentRecordScreenState.Started -> if (!serviceStarted) {
            SideEffect {
                serviceStarted = true
                startService(context, id)
            }
        }

        is CurrentRecordScreenState.CheckingPermissions,
        is CurrentRecordScreenState.Initial,
        is CurrentRecordScreenState.RequestingPermissions,
        is CurrentRecordScreenState.Completed -> if (serviceStarted) {
            SideEffect {
                stopService(context)
                serviceStarted = false
            }
        }
    }
}

private fun startService(context: Context, id: Int) {
    context.startService(Intent(context, LocationService::class.java).apply {
        putExtra(LocationService.KEY_RECORD_ID, id)
    })
}

private fun stopService(context: Context) {
    context.stopService(Intent(context, LocationService::class.java))
}

@Composable
fun Actions(
    state: CurrentRecordScreenState,
    onClick: (RecordUserAction) -> Unit,
) {
    when (state) {
        is CurrentRecordScreenState.CheckingPermissions,
        is CurrentRecordScreenState.Initial,
        is CurrentRecordScreenState.RequestingPermissions -> {
            ActionButton(R.string.start, { onClick(RecordUserAction.Start) })
        }
        is CurrentRecordScreenState.Completed -> { Text(text = "Press back") }
        is CurrentRecordScreenState.Started -> {
            ActionButton(R.string.pause, { onClick(RecordUserAction.Pause) })
        }
        is CurrentRecordScreenState.Paused -> {
            ActionButton(R.string.complete, { onClick(RecordUserAction.Complete) })
            ActionButton(R.string.resume, { onClick(RecordUserAction.Resume) })
        }
    }
}

@Composable
fun ActionButton(
    @StringRes textResource: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.wrapContentSize()
    ) {
        Text(text = stringResource(id = textResource))
    }
}

@Composable
fun PhotoFeed(images: List<ImageInfo>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = images) {
            PhotoItem(it, Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun PhotoItem(imageInfo: ImageInfo, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageInfo.imageUrl)
            .crossfade(true)
            .build(),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .wrapContentSize()
                )
            }
        },
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(DEFAULT_ASPECT_RATIO)
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PhotoItemPreview() {
    DmitriiBelykhChallengeTheme {
        PhotoItem(imageInfo = ImageInfo("https//someUrl.com"), Modifier.fillMaxWidth())
    }
}

private const val DEFAULT_ASPECT_RATIO = 16f/9
