package com.contextphoto.ui.screen

import android.R.attr.orientation
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
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
import com.contextphoto.dialog.CreateAlbumDialog
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import com.google.common.math.Quantiles.scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreenWithScaffold(
    modifier: Modifier = Modifier,
    navController: NavController,
    albumViewModel: AlbumViewModel = hiltViewModel()
) {
    LaunchedEffect(albumViewModel.loadAlbums.collectAsStateWithLifecycle()) {
        CoroutineScope(Dispatchers.IO).launch {
            albumViewModel.loadAlbumList()
        }
    }

    val albumList by albumViewModel.albumList.collectAsStateWithLifecycle()

    val createAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
    Log.d("Albums", albumList.toString())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        Destination.ALBUMS().label
                    )
                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        navController.navigateUp()
//                    }) {
//                        Icon(
//                            Icons.Default.ArrowBack, // Кнопка назад
//                            contentDescription = null,
//                        )
//                    }
//                },
            )
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                listOf(Destination.ALBUMS(), Destination.PICTURES()).forEach { destination ->
                    NavigationBarItem(
                        selected = destination is Destination.ALBUMS,
                        onClick = {
                            when (destination) {
                                is Destination.ALBUMS -> navController.navigate(route = destination.route)
                                is Destination.PICTURES -> navController.navigate(Destination.PICTURES().route + "/")
                                else -> {}
                            }
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                    createAlbumDialogVisible.value = true
                }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                    )
                }
            }
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
                        onItemClick = { navController.navigate(Destination.PICTURES().route + "/${album.bID}") },
                        albumViewModel
                    )
                }
            }

            if (createAlbumDialogVisible.value) {
                CreateAlbumDialog({}, createAlbumDialogVisible, albumViewModel)
            }
        }
    )

}

