package com.contextphoto

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalGraphicsContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.contextphoto.data.Album
import com.contextphoto.data.Picture
import com.contextphoto.ui.theme.ContextPhotoTheme

@Composable
    fun AlbumItem(album: Album, onItemClick: (String) -> Unit, modifier: Modifier = Modifier) {
        val albumName = remember { mutableStateOf(album.name) }
        val albumItemsCount = remember { mutableIntStateOf(album.itemsCount) }
        val albumMiniature = remember { mutableStateOf(album.miniature) }

        Box(
//            shape = RoundedCornerShape(0.dp),
            modifier = modifier.clickable {
                onItemClick(album.bID)
                Log.d("click", "album bID - ${album.bID}")
            }
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
    fun PictureItem(picture: Picture, modifier: Modifier = Modifier) {
        val durationMedia = remember { mutableStateOf(picture.duration) }
        val miniatureMedia = remember { mutableStateOf(picture.thumbnail) }
        var checked by remember { mutableStateOf(picture.checked) }
        val checkModifier by remember { mutableStateOf(Modifier.alpha(if (picture.checked) 0f else 1f)) }

        Box(contentAlignment = Alignment.BottomCenter)
        {
            Image(bitmap = miniatureMedia.value.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.Crop,)
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(text = durationMedia.value,
                    modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                    color = colorResource(R.color.white)
                )
                Checkbox(checked = checked,
                    onCheckedChange = { checked = it },
                    modifier=checkModifier,
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
        PictureItem(
            Picture(
                "1",
                "hru".toUri(),
                "a.jpg",
                BitmapFactory.decodeResource(LocalResources.current, R.drawable.recoon),
                "2:66",
                false
            )
        )
    }
}
