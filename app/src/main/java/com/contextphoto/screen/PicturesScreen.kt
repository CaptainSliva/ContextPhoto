package com.contextphoto.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.data.Destination
import com.contextphoto.data.MediaViewModel
import com.contextphoto.item.PictureItem
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable // TODO fixme При закрытии экрана и быстром нажатии на место где была картинка - открывается картинка, хотя на экране её уже нет
fun PicturesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MediaViewModel,
) {
    viewModel.resetMediaPosition()

    val context = LocalContext.current
    val albumBid by viewModel.albumBid.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            getAllMedia(context, albumBid, viewModel)
        }
    }
    val listMedia by viewModel.listMedia.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(items = listMedia) { media ->
            println("\n\nPRIIIINT\n${listMedia.size}\nIIITT\n$media\n")
            PictureItem(
                listMedia.indexOf(media),
                media,
                Modifier.padding(1.dp),
                onItemClick = { navController.navigate(Destination.FULLSCREENIMG.route) },
                viewModel,
            )
        }
    }

}

