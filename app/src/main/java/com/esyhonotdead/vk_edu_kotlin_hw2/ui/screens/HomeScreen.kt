package com.esyhonotdead.vk_edu_kotlin_hw2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.esyhonotdead.vk_edu_kotlin_hw2.R
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.DEFAULT_RATING
import com.esyhonotdead.vk_edu_kotlin_hw2.data.models.GifModel
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.components.GifCard
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.components.GifSearchDialog
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.icons.home
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.icons.search
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.theme.Dimens
import com.esyhonotdead.vk_edu_kotlin_hw2.ui.veiwmodels.GifViewModel
import kotlinx.coroutines.launch

private const val GIF_GRID_COLUMNS = 2

@Composable
fun HomeScreen(
    viewModel: GifViewModel = viewModel(factory = GifViewModel.Factory)
) {
    val gifs = viewModel.gifs.collectAsLazyPagingItems()
    val activeSearch by viewModel.activeSearch.collectAsStateWithLifecycle()
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var showSearchDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            HomeScreenTopAppBar(
                onHomeClick = if (activeSearch != null) viewModel::clearSearch else null,
                onButtonSearchClick = { showSearchDialog = true }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    shape = MaterialTheme.shapes.large,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    dismissActionContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            val refresh = gifs.loadState.refresh
            val showGrid = gifs.itemCount > 0 && (activeSearch == null || refresh is LoadState.NotLoading)
            when {
                showGrid -> {
                    MediaPaginationGrid(gifs, onGifClick = { index ->
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = resources.getString(R.string.gif_index, index + 1),
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            )
                        }
                    })
                }
                refresh is LoadState.Loading -> {
                    CircularProgressIndicator()
                }
                refresh is LoadState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = refresh.error.localizedMessage
                                ?: stringResource(R.string.gif_loading_error),
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { gifs.retry() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                else -> {
                    Text(stringResource(R.string.no_gifs))
                }
            }
            if (showSearchDialog) {
                GifSearchDialog(
                    initialQuery = activeSearch?.query.orEmpty(),
                    initialRating = activeSearch?.rating ?: DEFAULT_RATING,
                    onDismiss = { showSearchDialog = false },
                    onSearch = { query, rating ->
                        viewModel.search(query, rating)
                        showSearchDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun MediaPaginationGrid(gifs: LazyPagingItems<GifModel>, modifier: Modifier = Modifier, onGifClick: (Int) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(GIF_GRID_COLUMNS),
        verticalItemSpacing = Dimens.sizeS,
        horizontalArrangement = Arrangement.spacedBy(Dimens.sizeS),
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.sizeS)
    ) {
        items(
            count = gifs.itemCount, key = gifs.itemKey { it.id }) { index ->
            val gif = gifs[index]
            if (gif != null) {
                val image = gif.images.fixedHeight
                val width = image.width.toInt().takeIf { it > 0 }
                val height = image.height.toInt().takeIf { it > 0 }
                val aspectRatio = if (width != null && height != null) {
                    width.toFloat() / height
                } else {
                    1f
                }
                GifCard(gif, image, aspectRatio, onClick = { onGifClick(index) })
            }
        }
        if (gifs.loadState.append is LoadState.Loading) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.sizeM),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        if (
            gifs.loadState.append is LoadState.Error ||
            gifs.loadState.mediator?.refresh is LoadState.Error
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                ) {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(vertical = Dimens.sizeM),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = { gifs.retry() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopAppBar(onHomeClick: (() -> Unit)? = null, onButtonSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.home_screen_app_bar_title)) },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        actions = {
            if (onHomeClick != null) {
                IconButton(onClick = onHomeClick) {
                    Icon(imageVector = home, contentDescription = stringResource(R.string.home))
                }
            }
            IconButton(onClick = onButtonSearchClick) {
                Icon(
                    imageVector = search, contentDescription = stringResource(R.string.search)
                )
            }
        }
    )
}
