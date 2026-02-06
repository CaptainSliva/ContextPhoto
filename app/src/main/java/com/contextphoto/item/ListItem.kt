package com.contextphoto.item

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contextphoto.FunBottomMenu
import com.contextphoto.R
import com.contextphoto.data.Album
import com.contextphoto.data.Picture
import com.contextphoto.menu.PopupMenuAlbumScreen
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme

@Composable
fun AlbumItem(
    album: Album,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    albumViewModel: AlbumViewModel,
) {
    val popupVisible = rememberSaveable { mutableStateOf(false) }

    Box(
//            shape = RoundedCornerShape(0.dp),
        modifier =
            modifier.combinedClickable(
                onClick = {
                    albumViewModel.updateAlbumID(album.bID)
                    onItemClick()
                    Log.d("click", "album bID - ${album.bID}")
                },
                onLongClick = {
                    albumViewModel.selectAlbum(album)
                    popupVisible.value = !popupVisible.value
                },
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                bitmap = album.thumbnail.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp),
            )
            Column(
                modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp),
            ) {
                Text(
                    text = album.name, // maxLength = 63
                    maxLines = 3,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = album.itemsCount.toString(),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (popupVisible.value) {
                FunBottomMenu(
                    popupVisible.value,
                    { PopupMenuAlbumScreen({}, popupVisible, albumViewModel) },
                )
            }
        }
    }
}

@Composable
fun PictureItem(
    mediaPosition: Int,
    picture: Picture,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    mediaViewModel: MediaViewModel,
) {
    val checkboxVisible = mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle()
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsState()

    Box( // contentAlignment = Alignment.BottomCenter,
        modifier =
            modifier.fillMaxSize().combinedClickable(
                onClick = {
                    mediaViewModel.updateMediaPosition(mediaPosition)
                    onItemClick()
                    Log.d("POSITION", mediaPosition.toString())
                    Log.d("Click pic URI", picture.uri.toString())
                },
                onLongClick = {
                    mediaViewModel.changeStateBottomMenu()
                    if (picture !in listSelectedMedia) mediaViewModel.selectMedia(picture)
                    if (checkboxVisible.value) {
                        mediaViewModel.clearSelectedMedia()
                    }
                },
            ),
    ) {
        Image(
            bitmap = picture.thumbnail.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.aspectRatio(1f / 1f),
        )

        Icon(
            painter = painterResource(R.drawable.text_icon),
            contentDescription = null,
            modifier = Modifier.size(26.dp).alpha(if (picture.haveComment) 1f else 0f).padding(start = 4.dp),
            tint = Color.White,
        )

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = picture.duration,
                modifier = Modifier.padding(start = 8.dp),
                color = colorResource(R.color.white),
            )
            Checkbox(
                checked = picture in listSelectedMedia,
                onCheckedChange =
                    {
                        if (picture !in listSelectedMedia) {
                            mediaViewModel.selectMedia(
                                picture,
                            )
                        } else {
                            mediaViewModel.removeSelectMedia(picture)
                        }
                    },
                modifier = Modifier.alpha(if (checkboxVisible.value) 1f else 0f),
                colors =
                    CheckboxDefaults.colors(
                        checkedColor = colorResource(R.color.light_blue),
                        uncheckedColor = Color.White,
                        disabledCheckedColor = Color.White,
                    ),
                // .border(1.dp, color = Color.White, shape = RoundedCornerShape(20)),
            )

//                if (checkboxVisible.value) {
//                    println("check")
//                } else {
//                    println("UNcheck")
//                }
            if (picture.haveComment) {
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
