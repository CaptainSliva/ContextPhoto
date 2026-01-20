package com.contextphoto.ui.screen

import android.R.attr.orientation
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.contextphoto.data.AlbumCache
import com.contextphoto.item.AlbumItem
import com.contextphoto.ui.AlbumViewModel
import com.contextphoto.data.Destination
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Composable
fun AlbumsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    albumViewModel: AlbumViewModel = hiltViewModel()
) {

    albumViewModel.loadAlbumList()
    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()
    Log.d("Albums", albumList.toString())

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier,
    ) {
        items(
            items = albumList,
        ) { album ->
            AlbumItem(
                album,
                Modifier.padding(0.dp, 2.dp),
                onItemClick = { navController.navigate(Destination.PICTURES.route + "/${album.bID}") },
                albumViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreenWithScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    albumViewModel: AlbumViewModel = hiltViewModel()
) {
    albumViewModel.loadAlbumList()
    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()
    Log.d("Albums", albumList.toString())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        Destination.ALBUMS.label
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack, // Кнопка назад
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = modifier.padding(paddingValues),
            ) {
                items(
                    items = albumList,
                ) { album ->
                    AlbumItem(
                        album,
                        Modifier.padding(0.dp, 2.dp),
                        onItemClick = { navController.navigate(Destination.PICTURES.route + "/${album.bID}") },
                        albumViewModel
                    )
                }
            }
        }
    )

}

