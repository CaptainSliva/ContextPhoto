package com.contextphoto.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.ShowBottomMenu
import com.contextphoto.data.Destination
import com.contextphoto.data.Picture
import com.contextphoto.db.CommentDatabase
import com.contextphoto.item.ImageUI
import com.contextphoto.item.VideoUI
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.utils.FunctionsBitmap.md5
import com.contextphoto.utils.FunctionsUri.convertUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.format.TextStyle

@Composable
fun FullScreenViewPager(
    modifier: Modifier = Modifier,
    navController: NavController,
    bID: String,
    mediaPosotion: Int,
    fullScreenViewModel: FullscreenViewModel = hiltViewModel(),
) {
    //fullScreenViewModel.resetPicturePosition()
    fullScreenViewModel.loadPictureList(bID)
    fullScreenViewModel.updateMediaPosition(mediaPosotion)
    val coroutineScope = rememberCoroutineScope()
    val db = CommentDatabase.getDatabse(LocalContext.current).commentDao()
    val previousPage = rememberSaveable { mutableStateOf(0) }
    val listMedia by fullScreenViewModel.listMedia.collectAsStateWithLifecycle()
    val mediaPosotion by fullScreenViewModel.mediaPosition.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = mediaPosotion, pageCount = { listMedia.size })
    val commentText = rememberSaveable {mutableStateOf("")}

    Log.d("POSITION page", mediaPosotion.toString())
    HorizontalPager(state = pagerState) { page ->
        val media = listMedia[page]
        previousPage.value = page
        fullScreenViewModel.updateMediaPosition(pagerState.settledPage)


        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            fullScreenViewModel.updateMediaPosition(pagerState.currentPage)
            if (convertUri(media.path, media.uri).toString().contains("video")) {
                VideoUI(media.uri, { fullScreenViewModel.changeStateBottomMenuFullScreen() })
            } else {
                ImageUI(media.uri, media.path, { fullScreenViewModel.changeStateBottomMenuFullScreen() })
            }
            LaunchedEffect(page) {
                coroutineScope.launch {
                    commentText.value = db.findImageByHash(md5(media.thumbnail))?.image_comment?:""
                }
            }




            //Box(modifier = Modifier.fillMaxSize().background(Color.Red))

        }
    }

    LaunchedEffect(mediaPosotion) {
        if (mediaPosotion == 0) {
            coroutineScope.launch {
                try {
                    pagerState.animateScrollToPage(previousPage.value-10)
                } catch (e: Exception) {
                    try {
                        pagerState.animateScrollToPage(previousPage.value - 10)
                    } catch (e: Exception) {
                        navController.navigateUp()
                    }
                }
            }
        }
    }

    InfinityScrollableText(commentText.value, { fullScreenViewModel.changeStateBottomMenuFullScreen() })
    ShowBottomMenu(Destination.FULLSCREENIMG.route, fullScreenViewModel = fullScreenViewModel)
}

@Composable
fun InfinityScrollableText(commentText: String, onClick: () -> Unit) {
    val freeSpace = 155
    val brush = Brush.verticalGradient(listOf(colorResource(R.color.medium_transparant_black), colorResource(R.color.dark_black_overlay), Color.Black))
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(Size.Zero) }
    Column(modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom) {

        println(offsetY.value)
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(unbounded=true, align = Bottom)
            .onSizeChanged { size = it.toSize() }
            .background(brush)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val original = Offset(offsetX.value, offsetY.value)
                    val summed = original + dragAmount
                    val newValue =
                        Offset(
                            x = summed.x.coerceIn(0f, size.width),
                            y = (original.y-dragAmount.y/4).coerceIn(0f, Constraints.Infinity.toFloat()),
                        )
                    offsetX.value = newValue.x
                    offsetY.value = newValue.y
                }
            }
            .clickable(onClick = {onClick()})
            .height(freeSpace.dp+offsetY.value.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(text = commentText, modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(freeSpace.dp+offsetY.value.dp),
                color = Color.White,
            )
        }
    }

}