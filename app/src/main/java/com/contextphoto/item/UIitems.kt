package com.contextphoto.item

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.contextphoto.data.Destination
import com.contextphoto.data.Picture
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import java.io.File


// https://kotlincodes.com/kotlin/jetpack-compose-kotlin/jetpack-compose-media-player-integration/
@Composable // https://gorkemkara.net/responsive-video-playback-jetpack-compose-exoplayer/
fun VideoUI( // TODO fixme проверить как сделать воспроизведение как можно большего числа расширений (mp4 который я загрузил не воспроизводит)
    uri: Uri,
    onComplete: () -> Unit = {},
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
            }
        },
        modifier = Modifier.fillMaxSize(),
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
) {
    AndroidView(
        factory = { ctx ->
            SubsamplingScaleImageView(ctx).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                // Загрузка изображения через Coil
//                val imageLoader = ImageLoader(ctx)
//                val request = ImageRequest.Builder(ctx)
//                    .data(uri)
//                    .allowHardware(false)
//                    .build()
//
//                imageLoader.enqueue(request)
                // Заработает?
                setImage(ImageSource.uri(Uri.fromFile(File(path)))) // Костыль кажется
                // Загрузка изображения через Glide
//                            Glide.with(context)
//                                .asFile()
//                                .load(media.uri)
//                                .into(object : CustomTarget<File>() {
//                                    override fun onResourceReady(
//                                        resource: File,
//                                        transition: Transition<in File>?
//                                    ) {
//                                        setImage(ImageSource.uri(Uri.fromFile(resource)))
//                                    }
//
//                                    override fun onLoadCleared(placeholder: Drawable?) {
//                                        recycle()
//                                    }
//                                })
            }
        },
        modifier = Modifier.fillMaxSize(),
    )
}
