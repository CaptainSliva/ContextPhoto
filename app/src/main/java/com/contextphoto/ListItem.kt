package com.contextphoto

import android.R.attr.checked
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.contextphoto.data.Album
import com.contextphoto.data.MainViewModel
import com.contextphoto.data.Picture
import com.contextphoto.data.albumBid
import com.contextphoto.data.bottomMenuVisible
import com.contextphoto.data.imageUri
import com.contextphoto.data.openAlbum
import com.contextphoto.data.selectProcess
import com.contextphoto.ui.theme.ContextPhotoTheme


var checkboxVisible = mutableStateOf(false)

@Composable
    fun AlbumItem(album: Album, modifier: Modifier = Modifier, onItemClick: (String) -> Unit) {
        val albumName = remember { mutableStateOf(album.name) }
        val albumItemsCount = remember { mutableIntStateOf(album.itemsCount) }
        val albumMiniature = remember { mutableStateOf(album.miniature) }

        Box(
//            shape = RoundedCornerShape(0.dp),
            modifier = modifier.combinedClickable (
                onClick = {
                    onItemClick(album.bID)
                    albumBid = album.bID
                    openAlbum = album
                    checkboxVisible.value = false
                    Log.d("click", "album bID - ${album.bID}")
                },
                onLongClick = {
                    bottomMenuVisible.value = !bottomMenuVisible.value
                    selectProcess.value = !selectProcess.value
                    checkboxVisible.value = !checkboxVisible.value
                }
            )
        ) {
            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Image(bitmap = albumMiniature.value.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp)
                )
                Column(
                    modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text(text = albumName.value, // maxLength = 63
                        maxLines = 3,
                        style = MaterialTheme.typography.titleLarge)
                    Text(text = albumItemsCount.intValue.toString(),
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }

    @Composable
    fun PictureItem(picture: Picture, modifier: Modifier = Modifier, onItemClick: (String) -> Unit) {
        val durationMedia = remember { mutableStateOf(picture.duration) }
        val miniatureMedia = remember { mutableStateOf(picture.thumbnail) }
        var checked by remember { mutableStateOf(picture.checked) }
        val checkModifier by remember { mutableStateOf(Modifier.alpha(if (picture.checked) 0f else 1f)) }

        Box(contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.combinedClickable (
                onClick = {
                    onItemClick("")
                    imageUri = picture.uri
                },
                onLongClick = {
                    bottomMenuVisible.value = !bottomMenuVisible.value
                    selectProcess.value = !selectProcess.value
                    checkboxVisible.value = !checkboxVisible.value
                }
            ).padding(0.2.dp)
            )
        {
            Image(bitmap = miniatureMedia.value.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(1f/1f))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(text = durationMedia.value,
                    modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                    color = colorResource(R.color.white)
                )
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                        modifier = checkModifier.alpha(if (checkboxVisible.value) 1f else 0f),
                    )

                if (checked) {
                    println("check")
                } else {
                    println("UNcheck")
                }
            }

        }
    }


@Preview(showBackground = true)
@Composable
fun GreetngPreview() {
    ContextPhotoTheme {

    }
}
