package com.contextphoto.item

import android.graphics.PointF
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File


// https://kotlincodes.com/kotlin/jetpack-compose-kotlin/jetpack-compose-media-player-integration/
@Composable // https://gorkemkara.net/responsive-video-playback-jetpack-compose-exoplayer/
fun VideoUI(
    // TODO fixme проверить как сделать воспроизведение как можно большего числа расширений (mp4 который я загрузил не воспроизводит)
    uri: Uri,
    onClick: () -> Unit = {},
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
                setControllerAutoShow(false)

                // Обработка касаний
                setControllerHideOnTouch(true)

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
        //modifier = Modifier.fillMaxSize(),
        update = { view ->
            // Обновление при необходимости
        },
    )

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun ImageScreenUI(
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
//        modifier = Modifier.fillMaxSize()
    )
}
