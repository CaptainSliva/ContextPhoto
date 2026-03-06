package com.contextphoto.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.navigation.Destination
import com.contextphoto.item.CustomVideoUI
import com.contextphoto.item.ImageUI
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.utils.FunctionsUri.convertUri
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenViewPagerWithScaffold(
    navController: NavHostController,
    fullScreenViewModel: FullscreenViewModel = hiltViewModel(),
) {
    fullScreenViewModel.loadPictureList()

    val coroutineScope = rememberCoroutineScope()
    val listMedia by fullScreenViewModel.listMedia.collectAsStateWithLifecycle()
    val mediaPosition by fullScreenViewModel.mediaPosition.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = mediaPosition, pageCount = { listMedia.size })
    val commentText by fullScreenViewModel.imageComment.collectAsStateWithLifecycle()
    val window = LocalActivity.current!!.window
    val visibleMenu = fullScreenViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle()

    Log.d("ActionMediaPosotion", mediaPosition.toString())

    Scaffold(
        modifier = Modifier.background(Color.Black),
        topBar = {
            AnimatedVisibility(
                visible = visibleMenu.value,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                TopAppBar(
                    title = {
                        Column {
                            if (listMedia.size != 0) {
                                Text(
                                    listMedia[mediaPosition].date[0],
                                    fontSize = 20.sp,
                                )
                                Text(
                                    listMedia[mediaPosition].date[1],
                                    fontSize = 13.sp,
                                )
                            } else {
                                backActions(navController)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            backActions(navController)
                        }) {
                            Icon(
                                Icons.Default.ArrowBack, // Кнопка назад
                                contentDescription = null,
                            )
                        }
                    },
                    modifier = Modifier.background(Color.Black),
                    colors =
                        TopAppBarColors(
                            containerColor = Color.Black,
                            scrolledContainerColor = Color.White,
                            navigationIconContentColor = Color.White,
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White,
                        ),
                )
            }
            LaunchedEffect(visibleMenu.value) {
                val insetsController = WindowCompat.getInsetsController(window, window!!.decorView)

                if (!visibleMenu.value) {
                    insetsController.apply {
                        hide(WindowInsetsCompat.Type.statusBars())
                        hide(WindowInsetsCompat.Type.navigationBars())
                    }
                } else {
                    insetsController.apply {
                        show(WindowInsetsCompat.Type.statusBars())
                        show(WindowInsetsCompat.Type.navigationBars())
                    }
                }
            }
        },
        content = { paddingValues ->
            BackHandler {
                backActions(navController)
            }

            HorizontalPager(modifier = Modifier.padding(paddingValues), state = pagerState, key = { index -> listMedia[index].hashCode() }) { page ->
                val media = listMedia[page]
                val settedMedia = pagerState.settledPage
                Log.d("URI LOG", media.uri.toString())
                Log.d("page LOG", page.toString())
                Log.d("pagerState LOG", settedMedia.toString())

                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                ) {
                    Log.d("convertUri", "uri - ${media.uri}\npath - ${media.path}\nconvertUri- ${convertUri(media.path, media.uri)}")
                    if (settedMedia == listMedia.size) {
                        fullScreenViewModel.getImageComment(listMedia[settedMedia - 1].thumbnail)
                    } else {
                        fullScreenViewModel.getImageComment(listMedia[settedMedia].thumbnail)
                    }
                    if (convertUri(media.path, media.uri).toString().contains("video")) {
                        CustomVideoUI(
                            media.uri,
                            commentText,
                            fullScreenViewModel,
                            {
                                ShowBottomMenu(
                                    Destination.FullScreenImg().route,
                                    fullScreenViewModel = fullScreenViewModel,
                                    commentText = commentText,
                                    isVideo = true,
                                )
                            },
                            { fullScreenViewModel.changeStateBottomMenuFullScreen() },
                        )
                    } else {
                        ImageUI(
                            media.uri,
                            media.path,
                            { fullScreenViewModel.changeStateBottomMenuFullScreen() },
                        )
                        ShowBottomMenu(
                            Destination.FullScreenImg().route,
                            fullScreenViewModel = fullScreenViewModel,
                            commentText = commentText,
                        )
                    }
                }
            }
        },
    )

    LaunchedEffect(listMedia.size) {
        coroutineScope.launch {
            if (mediaPosition != -1) {
                pagerState.animateScrollToPage(mediaPosition)
                Log.d("Scroll", mediaPosition.toString())
            } else {
                backActions(navController)
            }
        }
    }




    LaunchedEffect(pagerState.settledPage) {
        fullScreenViewModel.updateMediaPosition(pagerState.settledPage) // setedMedia
    }
}

private fun backActions(navController: NavHostController) {
    navController.navigateUp()
}
