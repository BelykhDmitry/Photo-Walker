package com.dmitriib.challenge.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.dmitriib.dmitrii_belykh_challenge.R
import com.dmitriib.challenge.domain.ImageInfo
import com.dmitriib.challenge.ui.theme.DmitriiBelykhChallengeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeMainScreen(
    screenState: MainScreenState,
    onTopAppBarAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    ActionButton(screenState, onTopAppBarAction)
                }
            )
        }
    ) { contentPadding ->
        when (screenState) {
            is MainScreenState.Initial,
            is MainScreenState.CheckingPermissions,
            is MainScreenState.RequestingPermissions ->
                InitialState(Modifier.padding(contentPadding))
            is MainScreenState.WalkInProgress ->
                PhotoFeed(screenState.images, Modifier.padding(contentPadding))
            is MainScreenState.WalkPaused ->
                PhotoFeed(screenState.images, Modifier.padding(contentPadding))
        }
    }
}

@Composable
fun ActionButton(
    state: MainScreenState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    @StringRes val textResource = when (state) {
        is MainScreenState.CheckingPermissions,
        MainScreenState.Initial,
        is MainScreenState.RequestingPermissions -> R.string.start
        is MainScreenState.WalkInProgress -> R.string.stop
        is MainScreenState.WalkPaused -> R.string.resume
    }
    TextButton(
        onClick = onClick,
        modifier = modifier.wrapContentSize()
    ) {
        Text(text = stringResource(id = textResource))
    }
}

@Composable
fun InitialState(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize())
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
