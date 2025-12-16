package com.contextphoto

import android.R.attr.name
import android.R.attr.visible
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.data.AlbumViewModel
import com.contextphoto.data.Destination
import com.contextphoto.data.MediaViewModel
import com.contextphoto.dialog.CreateAlbumDialog
import com.contextphoto.menu.BottomMenuFullScreen
import com.contextphoto.menu.BottomMenuPictureScreen
import com.contextphoto.menu.PopupMenuAlbumScreen
import com.contextphoto.screen.AlbumsScreen
import com.contextphoto.screen.FullScreenViewPager
import com.contextphoto.screen.PicturesScreen
import com.contextphoto.ui.theme.ContextPhotoTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    // TODO ask как часто надо обновлять андроид студию? Как лучше присваивать значение переменной от вьюмодели? Через присвоение или через делегирование?
        setContent {
            ComposePermissions()
            val navController = rememberNavController()
            val startDestination = Destination.ALBUMS
            var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
            val createAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
            val albumViewModel = remember { AlbumViewModel() }
            val mediaViewModel = remember { MediaViewModel() }

            ContextPhotoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            // Если поменять route на label - всегда будет null title = { Text(navController.currentBackStackEntryAsState().value?.destination?.route.toString()) },
                            title = {
                                Text(
                                    when (
                                        navController
                                            .currentBackStackEntryAsState()
                                            .value
                                            ?.destination
                                            ?.route
                                    ) {
                                        "albums" -> Destination.ALBUMS.label
                                        "pictures" -> Destination.PICTURES.label
                                        "full_screen_img" -> Destination.PICTURES.label
                                        else -> "error in MainActivity TopAppBar"
                                    },
                                )
                            },
//                            title = {Text(startDestination.label)},
                            navigationIcon = {
                                val currentDestination =
                                    navController
                                        .currentBackStackEntryAsState()
                                        .value
                                        ?.destination
                                        ?.route
                                IconButton(onClick = {
                                    navController.navigateUp()
                                }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            },
                        )
                    },
                    bottomBar = {
                        val currentDestination =
                            navController
                                .currentBackStackEntryAsState()
                                .value
                                ?.destination
                                ?.route
                        if (currentDestination == Destination.ALBUMS.route) {
                            mediaViewModel.changeState(false)
                            mediaViewModel.changeStateCheckBox(false)
                            mediaViewModel.clearSelectedMedia()
                            mediaViewModel.changeAlbumBid("")
                            AnimatedVisibility(
                                currentDestination == Destination.ALBUMS.route,
                                enter =
                                    slideInVertically() +
                                            expandVertically(
                                                expandFrom = Alignment.Top,
                                            ) +
                                            fadeIn(
                                                initialAlpha = 0.3f,
                                            ),
                                exit =
                                    slideOutVertically() +
                                            shrinkVertically(
                                                shrinkTowards = Alignment.Top,
                                            ) + fadeOut(),
                            ) {
                                NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                                    Destination.entries.slice(0..1).forEachIndexed { index, destination ->
                                        NavigationBarItem(
                                            selected = selectedDestination == index,
                                            onClick = {
                                                navController.navigate(route = destination.route)
                                                selectedDestination = index
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
                            }
                        }
                        if (mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value) {
                            ShowBottomMenu(currentDestination?: Destination.ALBUMS.route, albumViewModel, mediaViewModel)
                        }
                    },
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = navController.currentBackStackEntryAsState().value?.destination?.route == Destination.ALBUMS.route,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            FloatingActionButton(onClick = {
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

                        AppNavHost(
                            navController,
                            startDestination,
                            modifier = Modifier.padding(paddingValues),
                            albumViewModel,
                            mediaViewModel,
                        )
                        if (createAlbumDialogVisible.value) { // TODO dodododo
                            CreateAlbumDialog({}, createAlbumDialogVisible, albumViewModel)
                        }
//                        AlbumsScreen(
//                            modifier = Modifier.padding(paddingValues)
//                        )
                    },
                )
                com.contextphoto.menu.DropdownMenu()
            }
        }
    }
}

@Composable
fun ShowBottomMenu(currentDestination: String, albumViewModel: AlbumViewModel, mediaViewModel: MediaViewModel) {

    when (currentDestination) {
        Destination.ALBUMS.route -> {
//            albumViewModel.changeState(false)
//            mediaViewModel.changeState(false)
//            FunBottomMenu(albumViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
//                { PopupMenuAlbumScreen(albumViewModel) })
        }

        Destination.PICTURES.route -> {
//            albumViewModel.changeState(false)
//            mediaViewModel.changeState(false)
            FunBottomMenu(mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuPictureScreen(mediaViewModel) }
            )
        }

        Destination.FULLSCREENIMG.route -> {
            FunBottomMenu(mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuFullScreen(mediaViewModel) }
            )
        }
    }
}

@Composable
fun FunBottomMenu(visible: Boolean,
      bootmFun: @Composable () -> Unit) {
    AnimatedVisibility(
        visible,
        enter =
            slideInVertically() +
                    expandVertically(
                        expandFrom = Alignment.Top,
                    ) +
                    fadeIn(
                        initialAlpha = 0.3f,
                    ),
        exit =
            slideOutVertically() +
                    shrinkVertically(
                        shrinkTowards = Alignment.Top,
                    ) + fadeOut(),
    ) {
        //Box(modifier = Modifier.fillMaxSize().background(Color.Red))
        bootmFun()
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    albumViewModel: AlbumViewModel,
    mediaViewModel: MediaViewModel,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
        composable(Destination.ALBUMS.route) {
            AlbumsScreen(modifier, navController, albumViewModel, mediaViewModel)
        }

        composable(Destination.PICTURES.route) {
            PicturesScreen(modifier, navController, mediaViewModel) // TODO fixme пропадают фото при перекомпозиции, но альбомы не пропадают
        }

        composable(Destination.FULLSCREENIMG.route) {
            FullScreenViewPager(modifier, navController, mediaViewModel)
        }
    }
}
