package com.contextphoto.item

import android.R.attr.checked
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.contextphoto.FunBottomMenu
import com.contextphoto.R
import com.contextphoto.data.Album
import com.contextphoto.data.AlbumCache
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.data.Picture
import com.contextphoto.menu.PopupMenuAlbumScreen
import com.contextphoto.ui.theme.ContextPhotoTheme


@Composable
    fun AlbumItem(album: Album, modifier: Modifier = Modifier, onItemClick: () -> Unit, albumViewModel: AlbumViewModel) {
        val popupVisible = rememberSaveable { mutableStateOf(false) }

        Box(
//            shape = RoundedCornerShape(0.dp),
            modifier = modifier.combinedClickable (
                onClick = {
                    albumViewModel.updateAlbumID(album.bID)
                    onItemClick()
                    Log.d("click", "album bID - ${album.bID}")
                },
                onLongClick = {
                    albumViewModel.selectAlbum(album)
                    popupVisible.value = !popupVisible.value
                }
            )
        ) {
            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Image(bitmap = album.thumbnail.asImageBitmap(), contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp)
                )
                Column(
                    modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text(text = album.name, // maxLength = 63
                        maxLines = 3,
                        style = MaterialTheme.typography.titleLarge)
                    Text(text = album.itemsCount.toString(),
                        style = MaterialTheme.typography.titleMedium)
                }
                if (popupVisible.value)
                    FunBottomMenu(popupVisible.value,
                        { PopupMenuAlbumScreen({}, popupVisible, albumViewModel) })
            }

        }
    }

    @Composable
    fun PictureItem(mediaPosition: Int, pic: Picture, modifier: Modifier = Modifier, onItemClick: () -> Unit, viewModel: MediaViewModel) {
        // TODO fixme возможно состояние сбрасывается из-за того, что я не саму picture сохраняю
        val picture by remember { mutableStateOf(pic) }
        val checkboxVisible = viewModel.checkboxVisible.collectAsStateWithLifecycle()
        val checkModifier by remember { mutableStateOf(Modifier.alpha(if (checkboxVisible.value) 0f else 1f)) }
        val listSelectedMedia by viewModel.listSelectedMedia.collectAsState()
        // TODO есть баг при заходе в fullscreen когда меню вызвано и выходе - галочки пропали и не появляются, пока не переоткрою экран
        Box(contentAlignment = Alignment.BottomCenter,
            modifier = modifier.combinedClickable (
                onClick = {
                    onItemClick()
                    Log.d("POSITION", mediaPosition.toString())
                    viewModel.updateMediaPosition(mediaPosition)
                },
                onLongClick = {
                    viewModel.changeStateBottomMenu()
                    viewModel.changeStateCheckBox()
                    if (picture !in listSelectedMedia) viewModel.selectMedia(picture)
                    if (checkboxVisible.value) {
                        viewModel.clearSelectedMedia()
                    }
                }
            )
            )
        {
            Image(bitmap = picture.thumbnail.asImageBitmap(), contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(1f/1f))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(text = picture.duration,
                    modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                    color = colorResource(R.color.white)
                )
                Checkbox(
                    checked = picture in listSelectedMedia,
                    onCheckedChange =
                        {
                            if (picture !in listSelectedMedia) viewModel.selectMedia(picture)
                            else viewModel.removeSelectMedia(picture)
                        },
                    modifier = checkModifier.alpha(if (checkboxVisible.value) 1f else 0f),
                )

                if (checkboxVisible.value) {
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
