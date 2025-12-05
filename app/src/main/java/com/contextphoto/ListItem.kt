package com.contextphoto

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.contextphoto.data.Album
import com.contextphoto.data.MediaViewModel
import com.contextphoto.data.Picture
import com.contextphoto.data.albumBid
import com.contextphoto.data.bottomMenuVisible
import com.contextphoto.data.listpicture
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
    fun PictureItem(mediaPosition: Int, picture: Picture, modifier: Modifier = Modifier, onItemClick: (String) -> Unit, viewModel: MediaViewModel) {
        val durationMedia = remember { mutableStateOf(picture.duration) }
        val miniatureMedia = remember { mutableStateOf(picture.thumbnail) }
        var checked by remember { mutableStateOf(picture.checked) }
        val checkModifier by remember { mutableStateOf(Modifier.alpha(if (picture.checked) 0f else 1f)) }

        Box(contentAlignment = Alignment.BottomCenter,
            modifier = modifier.combinedClickable (
                onClick = {
                    onItemClick("")
                    Log.d("POSITION", mediaPosition.toString())
                    viewModel.updateMediaPosition(mediaPosition)
                },
                onLongClick = { // TODO fixme галочки не сбрасываются при долгом нажатии после их установки
                    bottomMenuVisible.value = !bottomMenuVisible.value
                    selectProcess.value = !selectProcess.value
                    checkboxVisible.value = !checkboxVisible.value

                    if (listpicture.isEmpty()) {
                        checked = true
                        listpicture.add(picture)
                    }
                    else {
                        listpicture.clear()
                        checked = false
                    }
                }
            )
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
                        onCheckedChange =
                            {
                                checked = it
                                if (checked) listpicture.add(picture)
                                else listpicture.remove(picture)
                            },
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
