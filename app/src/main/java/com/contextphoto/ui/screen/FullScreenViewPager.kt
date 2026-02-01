package com.contextphoto.ui.screen

import android.app.Activity
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.Destination
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.CustomVideoUI
import com.contextphoto.item.ImageUI
import com.contextphoto.item.VideoUI
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.utils.FunctionsBitmap.md5
import com.contextphoto.utils.FunctionsUri.convertUri
import com.davemorrissey.labs.subscaleview.ImageSource.uri
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenViewPagerWithScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    mediaPosotionStart: Int,
    fullScreenViewModel: FullscreenViewModel = hiltViewModel(),
) {
    fullScreenViewModel.loadPictureList()
    fullScreenViewModel.updateMediaPosition(mediaPosotionStart)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = CommentDatabase.getDatabse(LocalContext.current).commentDao()
    val listMedia by fullScreenViewModel.listMedia.collectAsStateWithLifecycle()
    val mediaPosotion by fullScreenViewModel.mediaPosition.collectAsStateWithLifecycle()
    val dateInfoList by fullScreenViewModel.dateInfoList.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = mediaPosotion, pageCount = { listMedia.size })
    val commentText = rememberSaveable {mutableStateOf<String?>(null)}
    val deleteAction = fullScreenViewModel.deleteAction.collectAsStateWithLifecycle()
    val window = LocalActivity.current!!.window
    val visibleMenu = fullScreenViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle()

//    LaunchedEffect(Unit) {
//        val activity = context as Activity
//        activity?.window?.let { window ->
//            WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        }
//    }

    Log.d("POSITION page", mediaPosotion.toString())
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = visibleMenu.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                dateInfoList[0],
                                fontSize = 20.sp
                            )
                            Text(
                                dateInfoList[1],
                                fontSize = 13.sp
                            )
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                Icons.Default.ArrowBack, // Кнопка назад
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
            LaunchedEffect (visibleMenu.value) {
                val insetsController = WindowCompat.getInsetsController(window, window!!.decorView)

                if (!visibleMenu.value) {
                    insetsController.apply {
                        hide(WindowInsetsCompat.Type.statusBars())
                        hide(WindowInsetsCompat.Type.navigationBars())
                        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }

                else {
                    insetsController.apply {
                        show(WindowInsetsCompat.Type.statusBars())
                        show(WindowInsetsCompat.Type.navigationBars())
                        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    }
                }

            }

        },
        content = { paddingValues ->
            HorizontalPager(modifier = Modifier.padding(paddingValues), state = pagerState) { page ->
                val media = listMedia[page]
                Log.d("URI LOG", media.uri.toString())
                Log.d("page LOG", page.toString())
                Log.d("pagerState LOG", pagerState.settledPage.toString())
                fullScreenViewModel.updateMediaPosition(pagerState.settledPage) // pagerState.settledPage
                fullScreenViewModel.updateDateInfo(LocalContext.current, page)

                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                ) {
                    LaunchedEffect(pagerState.settledPage) {
                        if (convertUri(listMedia[pagerState.settledPage].path, listMedia[pagerState.settledPage].uri).toString().contains("video")) {
                            fullScreenViewModel.changeMediaType(true)
                        }
                        else fullScreenViewModel.changeMediaType(false)
                    }
                    if (convertUri(media.path, media.uri).toString().contains("video")) {
                        //fullScreenViewModel.changeStateBottomMenuFullScreen(true)
                        //VideoUI(media.uri, { fullScreenViewModel.changeStateBottomMenuFullScreen() }, // Для комментария видео стоит null тк иначе я вижу просто чёрный экран
                        //    { ShowBottomMenu(Destination.FULLSCREENIMG().route, fullScreenViewModel = fullScreenViewModel, commentText = null) })
                        CustomVideoUI(media.uri)
                    } else {
                        ImageUI(media.uri, media.path, { fullScreenViewModel.changeStateBottomMenuFullScreen() })
                        ShowBottomMenu(Destination.FULLSCREENIMG().route, fullScreenViewModel = fullScreenViewModel, commentText = commentText.value)
                    }
                    LaunchedEffect(pagerState.settledPage) {
                        coroutineScope.launch {
                            commentText.value = db.findImageByHash(md5(listMedia[pagerState.settledPage].thumbnail))?.image_comment
                        }
                    }
                }
            }


            LaunchedEffect(mediaPosotion) {
                if (deleteAction.value) {
                    coroutineScope.launch {
                        try {
                            pagerState.animateScrollToPage(mediaPosotion)
                        } catch (e: Exception) {
                            navController.navigateUp()
                        }
                        //fullScreenViewModel.deleteActionChange()
                    }
                }

            }
        }
    )


}