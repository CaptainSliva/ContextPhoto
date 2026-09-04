package com.contextphoto.item

import android.R.attr.text
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contextphoto.FunBottomMenu
import com.contextphoto.InfinityScrollableText
import com.contextphoto.R
import com.contextphoto.menu.PopupMenuAlbumScreen
import com.contextphoto.ui.vm.AlbumViewModel
import com.contextphoto.ui.vm.MediaViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.ui.vm.FullscreenViewModel
import com.contextphoto.utils.FunctionsApp.sizeChange

@OptIn(ExperimentalFoundationApi::class)
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
                    Log.d("click", "album bID - ${album.bID}, ${album.name}")
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PictureItem(
    mediaPosition: Int,
    picture: Picture,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
    mediaViewModel: MediaViewModel,
    fullScreenViewModel: FullscreenViewModel = hiltViewModel()
) {
    val checkboxVisible = mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle()
    val listSelectedMedia by mediaViewModel.listSelectedMedia.collectAsState()
    val countOfPhotoLine by mediaViewModel.countOfPhotoLine.collectAsStateWithLifecycle()
    val stateCommentPicture by mediaViewModel.stateCommentPicture.collectAsStateWithLifecycle()

    Box(
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

        AnimatedVisibility (picture.haveComment.value && !stateCommentPicture) {
            Icon(
                painter = painterResource(R.drawable.text_icon),
                contentDescription = null,
                modifier = Modifier
                    .size((sizeChange(countOfPhotoLine) * 2.7).dp)
                    .alpha(if (picture.haveComment.value) 1f else 0f)
                    .padding(start = 4.dp)
                    .clickable(onClick = { mediaViewModel.changeStateCommentPicture() }),
                tint = Color.Unspecified
            )
        }

        AnimatedVisibility (picture.haveComment.value && countOfPhotoLine < 4 && stateCommentPicture) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f / 3f)
                    .align(Alignment.BottomCenter)
                    .background(colorResource(R.color.medium_transparant_black))
                    .padding(4.dp)
                    .clickable(onClick = { mediaViewModel.changeStateCommentPicture() }),
                contentAlignment = Alignment.BottomCenter
            ) {
                val commentText by fullScreenViewModel.imageComment.collectAsStateWithLifecycle()
                fullScreenViewModel.getImageComment(picture.thumbnail)
                Text(
                    text = commentText,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 6/countOfPhotoLine
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = picture.duration,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                    color = colorResource(R.color.white),
                    fontSize = sizeChange(countOfPhotoLine).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box(
                modifier =
                    Modifier
                        .size((sizeChange(countOfPhotoLine)*2).dp)
                        .padding(end = 8.dp, bottom = 8.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = checkboxVisible.value,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut(),
                ) {
                    Checkbox(
                        modifier = Modifier.size((sizeChange(countOfPhotoLine)*2).dp).scale(sizeChange(countOfPhotoLine)/15),
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
                        colors =
                            CheckboxDefaults.colors(
                                checkedColor = colorResource(R.color.light_blue),
                                checkmarkColor = Color.White,
                                uncheckedColor = Color.White,
                                disabledCheckedColor = Color.White,
                            ),
                    )
                }
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
