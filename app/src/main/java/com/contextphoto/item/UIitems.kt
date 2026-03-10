package com.contextphoto.item

import android.net.Uri
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.contextphoto.InfinityScrollableText
import com.contextphoto.R
import com.contextphoto.ui.vm.FullscreenViewModel
import com.contextphoto.utils.FunctionsApp.durationTranslate
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay
import java.io.File

// https://kotlincodes.com/kotlin/jetpack-compose-kotlin/jetpack-compose-media-player-integration/
// https://gorkemkara.net/responsive-video-playback-jetpack-compose-exoplayer/
@Composable
fun CustomVideoUI(
    uri: Uri,
    commentText: String? = null,
    fullscreenViewModel: FullscreenViewModel,
    bottomMfenu: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val exoPlayer =
        remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = false
            }
        }
    var currentPosition by remember { mutableLongStateOf(0L) }
    val totalDuration = remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }
    val isVisible = fullscreenViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle()

    val playerListener =
        object : Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int,
            ) {
                currentPosition = newPosition.positionMs
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> "Idle"
                    Player.STATE_BUFFERING -> "Buffering"
                    Player.STATE_READY -> totalDuration.value = exoPlayer.duration
                    Player.STATE_ENDED -> currentPosition = totalDuration.value
                    else -> "Unknown"
                }
            }
        }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (exoPlayer.isPlaying) {
                currentPosition = exoPlayer.currentPosition
            }
        }
    }

    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(playerListener)
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.removeListener(playerListener)
            exoPlayer.release()
        }
    }

    DisposableEffect(
        AndroidView(
            modifier =
                Modifier.clickable(onClick = {
                    onClick()
                }),
            factory = {
                PlayerView(context).apply {
                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    player = exoPlayer
                    useController = false // Show playback controls
                    hideController()
                }
            },
            update = { view ->
                view.player = exoPlayer
            },
        ),
    ) {
        onDispose {
            exoPlayer.release()
        }
    }

    AnimatedVisibility(
        visible = isVisible.value,
        enter =
            slideInVertically(initialOffsetY = { 500 }) +
                fadeIn(initialAlpha = 0.3f),
        exit =
            slideOutVertically(targetOffsetY = { 600 }) +
                fadeOut(),
    ) {
        InfinityScrollableText(isVisible.value, commentText, { onClick() }, offset = 47)
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Время
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = durationTranslate(currentPosition),
                    color = Color.White,
                )

                // Кнопка СтартСтоп
                IconButton(
                    onClick = {
                        if (isPlaying) {
                            exoPlayer.pause()
                            isPlaying = false
                        } else {
                            exoPlayer.play()
                            isPlaying = true
                        }
                    },
                ) {
                    Icon(
                        if (isPlaying) {
                            painterResource(
                                R.drawable.baseline_pause_32,
                            )
                        } else {
                            painterResource(R.drawable.baseline_play_arrow_32)
                        },
                        if (isPlaying) "Пауза" else "Воспроизвести",
                        tint = Color.White,
                    )
                }

                // Время
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = durationTranslate(totalDuration.value),
                    color = Color.White,
                )
            }

            // Ползунок прогресса
            Box(modifier = Modifier.background(Color.Black)) {
                Slider(
                    value = if (totalDuration.value > 0) currentPosition.toFloat() / totalDuration.value else 0f,
                    onValueChange = { newValue ->
                        currentPosition = (newValue * totalDuration.value).toLong()
                        exoPlayer.seekTo(currentPosition)
                    },
                    onValueChangeFinished = {
                        exoPlayer.seekTo(currentPosition)
                        if (currentPosition + 1 >= totalDuration.value) currentPosition = totalDuration.value
                        println("Перемотано на: $currentPosition")
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    colors =
                        SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red,
                            inactiveTrackColor = Color.Gray.copy(alpha = 0.3f),
                        ),
                )
            }
            bottomMfenu()
        }
    }
}

@Composable
fun ImageUI(
    uri: Uri,
    path: String,
    onClick: () -> Unit = {},
) {
    if (File(path).extension.lowercase().contains("gif")) {
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(uri.toString())
                    .build(),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxSize()
                    .clickable { onClick() },
        )
    } else {
        AndroidView(
            factory = { ctx ->
                SubsamplingScaleImageView(ctx).apply {
                    layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    setImage(ImageSource.uri(uri))

                    val gestureDetector =
                        GestureDetector(
                            object : SimpleOnGestureListener() {
                                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                    onClick()
                                    return true
                                }
                            },
                        )

                    setOnTouchListener { v, event ->
                        gestureDetector.onTouchEvent(event)
                    }
                }
            },
        )
    }
}
