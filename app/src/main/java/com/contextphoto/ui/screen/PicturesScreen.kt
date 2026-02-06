package com.contextphoto.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.Destination
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.PictureItem
import com.contextphoto.menu.MainDropdownMenu
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsBitmap.md5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicturesScreenWithScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    bID: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            mediaViewModel.loadPictureList(bID)
        }
    }

    val context = LocalContext.current
    val db = CommentDatabase.getDatabse(context).commentDao()
    val listMedia by mediaViewModel.listMedia.collectAsStateWithLifecycle()
    val albumName by mediaViewModel.albumName.collectAsStateWithLifecycle()
    val groupedMedia =
        remember(listMedia) {
            listMedia.groupBy { media ->
                media.date[0]
            }
        }

    val otboinik = 10
    val defaultTextSize = 14f
    val dateVisible = rememberSaveable { mutableStateOf(true) }
    val counterFlag = rememberSaveable { mutableStateOf(0) }
    val countOfPhotoLine = rememberSaveable { mutableStateOf(3) }
    val fontSize = rememberSaveable { mutableStateOf(defaultTextSize) }
    val mediaPosition = mediaViewModel.mediaPosition.collectAsStateWithLifecycle()
    val newPosition = rememberLazyGridState(0)
    val coroutineScope = rememberCoroutineScope()

    Log.d("Pictures", listMedia.toString())
    when (countOfPhotoLine.value) {
        1 -> {
            fontSize.value = 28f
        }

        2 -> {
            fontSize.value = 18f
        }

        3 -> {
            fontSize.value = defaultTextSize
        }

        4 -> {
            fontSize.value = 11f
            dateVisible.value = true
        }

        // visible date and divider
        5 -> {
            dateVisible.value = false
        }

        // invisible date and divider
        6 -> {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        albumName,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        mediaViewModel.loadPicturesStateChange(true)
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack, // Кнопка назад
                            contentDescription = null,
                        )
                    }
                },
            )
            MainDropdownMenu(navController)
        },
        bottomBar = {
            val stateBottomMenu by mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle()
            if (bID == "") {
                AnimatedVisibility(
                    visible = !stateBottomMenu,
                    enter = slideInVertically(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                ) {
                    NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                        listOf(Destination.Albums(), Destination.Pictures()).forEach { destination ->
                            NavigationBarItem(
                                selected = destination is Destination.Pictures,
                                onClick = {
                                    when (destination) {
                                        is Destination.Albums -> {
                                            mediaViewModel.loadAlbumsStateChange(true)
                                            navController.navigate(route = destination.route)
                                        }

                                        is Destination.Pictures -> {
                                            navController.navigate(Destination.Pictures().route + "/")
                                        }

                                        else -> {}
                                    }
                                },
                                icon = {
                                    Icon(
                                        painterResource(destination.icon),
                                        contentDescription = destination.contentDescription,
                                    )
                                },
                                label = { Text(destination.label) },
                            )
                        }
                    }
                }
            }
        },
        content = { paddingValues ->

            Log.d("fontSize", fontSize.value.toString())
            Log.d("countOfPhotoLine", countOfPhotoLine.value.toString())
            Box(
                modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, f1, _ ->
                            if (f1 < 1) { // Увеличение масштаба
                                counterFlag.value += 1
                                if (counterFlag.value == otboinik) {
                                    counterFlag.value = 0
                                    countOfPhotoLine.value += if (countOfPhotoLine.value < 6) 1 else 0
                                }
                            } else { // Уменьшение масштаба
                                counterFlag.value += 1
                                if (counterFlag.value == otboinik) {
                                    counterFlag.value = 0
                                    countOfPhotoLine.value -= if (countOfPhotoLine.value > 1) 1 else 0
                                }
                            }
                        }
                    },
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(countOfPhotoLine.value),
                    modifier =
                        modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                    state = newPosition,
                    contentPadding = PaddingValues(bottom = 80.dp),
                ) {
                    groupedMedia.forEach { (date, mediaList) ->
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
                            AnimatedVisibility(
                                visible = dateVisible.value,
                                enter = expandVertically(expandFrom = Alignment.Top),
                                exit = shrinkVertically(shrinkTowards = Alignment.Top),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().animateItem(),
                                ) {
                                    Text(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(start = 4.dp, top = 4.dp),
                                        text = date,
                                        fontSize = fontSize.value.sp,
                                    )
                                    HorizontalDivider()
                                    Spacer(Modifier.padding(2.dp))
                                }
                            }
                        }

                        items(items = mediaList, key = { media -> media.path }) { media ->
                            val mediaIndex = listMedia.indexOf(media)
                            val haveComment = remember { mutableStateOf(false) }

                            LaunchedEffect(Unit) {
                                CoroutineScope(Dispatchers.Default).launch {
                                    haveComment.value = (db.findImageByHash(md5(media.thumbnail))?.image_comment ?: "") != ""
                                    listMedia[mediaIndex].haveComment = haveComment.value
                                }
                            }

                            PictureItem(
                                mediaIndex,
                                media,
                                Modifier.padding(1.dp).animateItem(),
                                onItemClick = {
                                    navController.navigate(Destination.FullScreenImg().route)
                                },
                                mediaViewModel,
                            )
                        }
                    }
                }

                ShowBottomMenu(Destination.Pictures().route, mediaViewModel = mediaViewModel)
            }

            LaunchedEffect(mediaPosition.value) {
                coroutineScope.launch {
                    println("New index photo ${mediaPosition.value}")
                    newPosition.scrollToItem(
                        mediaPosition.value,
//                        index = mediaPosition.value,
//                        scrollOffset = 0
                    )
                }
            }
        },
    )
}
