package com.contextphoto.item

import android.R.attr.onClick
import android.graphics.PointF
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.contextphoto.R
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.utils.FunctionsApp.durationTranslate
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageSource.uri
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max
import kotlin.math.min


// https://kotlincodes.com/kotlin/jetpack-compose-kotlin/jetpack-compose-media-player-integration/
// https://gorkemkara.net/responsive-video-playback-jetpack-compose-exoplayer/
@Composable
fun CustomVideoUI(
    uri: Uri,
    bottomMfenu: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val exoPlayer = remember {
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

    val playerListener = object : Player.Listener {
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            currentPosition = newPosition.positionMs
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                currentPosition = totalDuration.value
            }
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
            modifier = Modifier.clickable(onClick = {onClick()}),
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

            }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        totalDuration.value = exoPlayer.duration

        // Кнопки управления
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    val newPosition = max(0L, exoPlayer.currentPosition - 5000)
                    exoPlayer.seekTo(newPosition)
                    currentPosition = newPosition
                }
            ) {
                Icon(Icons.Default.ArrowBack, "Назад 5 сек",
                    tint = Color.White)
            }

            IconButton(
                onClick = {
                    if (isPlaying) {
                        exoPlayer.pause()
                        isPlaying = false
                    } else {
                        exoPlayer.play()
                        isPlaying = true
                    }
                }
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                    if (isPlaying) "Пауза" else "Воспроизвести",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    val newPosition = min(
                        exoPlayer.duration,
                        exoPlayer.currentPosition + 5000
                    )
                    exoPlayer.seekTo(newPosition)
                    currentPosition = newPosition
                }
            ) {
                Icon(Icons.Default.Refresh, "Вперед 5 сек",
                    tint = Color.White)
            }
        }

        // Ползунок прогресса
        Column(verticalArrangement = Arrangement.Bottom) {
            Slider(
                value = if (totalDuration.value > 0) currentPosition.toFloat() / totalDuration.value else 0f,
                onValueChange = { newValue ->
                    currentPosition = (newValue * totalDuration.value).toLong()
                    exoPlayer.seekTo(currentPosition)
                },
                onValueChangeFinished = {
                    // Действие после отпускания ползунка
                    exoPlayer.seekTo(currentPosition)
                    println("Перемотано на: ${durationTranslate(currentPosition)}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Red,
                    activeTrackColor = Color.Red,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
        bottomMfenu()
    }
}


@Composable
fun VideoUI(
    uri: Uri,
    onClick: () -> Unit = {},
    BottomMenuFullscreen: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val exoPlayer =
        remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.Builder().setUri(uri).build()
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = false
            }
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(10f)
        ) {
            AndroidView(
                factory = {
                    StyledPlayerView(context).apply {
                        layoutParams =
                            ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )

                        this.player = exoPlayer

                        // Автоматическое скрытие контролов
//                        controllerAutoShow = false

                        // Обработка касаний
                        controllerHideOnTouch = true

                        val gestureDetector =
                            GestureDetector(object : SimpleOnGestureListener() {
                                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                                    onClick()
                                    return true
                                }
                            })
                        setOnTouchListener { _, event ->
                            gestureDetector.onTouchEvent(event)
                        }
                    }
                },
                update = { view ->
                    // Обновление при необходимости
                },
            )

        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuFullscreen()
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun ImageUI(
    uri: Uri,
    path: String,
    onClick: () -> Unit = {}
) {
    AndroidView(
        factory = { ctx ->
            SubsamplingScaleImageView(ctx).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                setImage(ImageSource.uri(Uri.fromFile(File(path))))

                val gestureDetector =
                    GestureDetector(object : SimpleOnGestureListener() {
                        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                            onClick()
                            return true
                        }
                    })

                setOnTouchListener { v, event ->
                    gestureDetector.onTouchEvent(event)
                }
            }
        },
    )
}
