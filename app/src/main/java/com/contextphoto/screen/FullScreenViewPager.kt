package com.contextphoto.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.item.ImageScreenUI
import com.contextphoto.item.VideoUI
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsUri.convertUri

@Composable
fun FullScreenViewPager(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MediaViewModel,
) {
    val listMedia by viewModel.listMedia.collectAsStateWithLifecycle()
    val mediaPosotion by viewModel.mediaPosition.collectAsStateWithLifecycle()
    val bottomMenuVisible = viewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle().value
    val pagerState = rememberPagerState(initialPage = mediaPosotion, pageCount = { listMedia.size })

    Log.d("POSITION", mediaPosotion.toString())
    Log.d("CLICK", bottomMenuVisible.toString())


    HorizontalPager(state = pagerState) { page ->
        // Our page content
        val media = listMedia[page]
        viewModel.updateMediaPosition(pagerState.settledPage)
        Log.d("POSITION page", page.toString())

        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Log.d("POSITION page", listMedia[page].toString())
            viewModel.updateMediaPosition(page)
            if (convertUri(media.path, media.uri).toString().contains("video")) {
                VideoUI(media.uri, { viewModel.changeStateBottomMenuFullScreen() })
            } else {
                ImageScreenUI(media.uri, media.path, { viewModel.changeStateBottomMenuFullScreen() }) //viewModel.changeStateBottomMenuFullScreen()
            }
            //Box(modifier = Modifier.fillMaxSize().background(Color.Red))

        }

    }
}

