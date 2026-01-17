package com.contextphoto

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.data.Destination
import com.contextphoto.dialog.CreateAlbumDialog
import com.contextphoto.menu.BottomMenuFullScreen
import com.contextphoto.menu.BottomMenuPictureScreen
import com.contextphoto.menu.MainDropdownMenu
import com.contextphoto.ui.FullscreenViewModel
import com.contextphoto.ui.MediaViewModel
import com.contextphoto.ui.screen.AlbumsScreen
import com.contextphoto.ui.screen.FullScreenViewPager
import com.contextphoto.ui.screen.PicturesScreen
import com.contextphoto.ui.screen.SearchPhotoScreen
import com.contextphoto.ui.screen.SettingsScreen
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsApp.firebaseFirestoreDatabaseTest
import com.contextphoto.utils.FunctionsApp.firebasePasswordAuth
import com.contextphoto.utils.FunctionsApp.firebaseRealTimeDatabaseTest
import dagger.hilt.android.AndroidEntryPoint

// Виды todo
// TODO add
// TODO fixme
//TODO ask
@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePermissions()
            val navController = rememberNavController()
            val startDestination = Destination.ALBUMS
            val context = LocalContext.current
            val activity = context as Activity
            var orientation = rememberSaveable { mutableStateOf(activity.requestedOrientation) }
            //var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
            val createAlbumDialogVisible = rememberSaveable { mutableStateOf(false) }
            //val albumViewModel = hiltViewModel<AlbumViewModel>()
            //val mediaViewModel = hiltViewModel<MediaViewModel>()

//            firebaseRealTimeDatabaseTest()
//            firebaseFirestoreDatabaseTest()
//            firebasePasswordAuth()


            ContextPhotoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    when{
                                        navController.currentBackStackEntryAsState().value?.destination?.route.toString().contains(Destination.ALBUMS.route) -> Destination.ALBUMS.label
                                        navController.currentBackStackEntryAsState().value?.destination?.route.toString().contains(Destination.PICTURES.route) -> Destination.PICTURES.label
                                        navController.currentBackStackEntryAsState().value?.destination?.route.toString().contains(Destination.FULLSCREENIMG.route) -> Destination.FULLSCREENIMG.label
                                        navController.currentBackStackEntryAsState().value?.destination?.route.toString().contains(Destination.SEARCH_PHOTO.route) -> Destination.SEARCH_PHOTO.label
                                        navController.currentBackStackEntryAsState().value?.destination?.route.toString().contains(Destination.SETTINGS.route) -> Destination.SETTINGS.label
                                        else -> {
                                            println("NAV - ${navController.currentBackStackEntryAsState().value?.destination?.route.toString()}")
                                            "error in MainActivity TopAppBar"
                                        }
                                    },
                                )
                            },
//                            title = {Text(startDestination.label)},
                            navigationIcon = {
                                IconButton(onClick = {
                                    navController.navigateUp()
                                    if (activity.requestedOrientation != orientation.value) { // TODO fixme не работает
                                        if (orientation.value == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        }
                                        if (orientation.value == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                        }
                                    }
                                }) {
                                    Icon(
                                        Icons.Default.ArrowBack, // Кнопка назад
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
                                            selected = destination == Destination.ALBUMS,
                                            onClick = {
                                                if (destination == Destination.PICTURES) navController.navigate(Destination.PICTURES.route + "/")
                                                else navController.navigate(route = destination.route)
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
                            modifier = Modifier.padding(paddingValues)
                        )
                        if (createAlbumDialogVisible.value) {
                            CreateAlbumDialog({}, createAlbumDialogVisible)
                        }
                    },
                )
                MainDropdownMenu(navController)
            }
        }
    }
}

@Composable
fun ShowBottomMenu(
    currentDestination: String,
    mediaViewModel: MediaViewModel = hiltViewModel(),
    fullScreenViewModel: FullscreenViewModel = hiltViewModel()
) {

    when (currentDestination) {
        Destination.PICTURES.route -> {
            FunBottomMenu(mediaViewModel.bottomMenuVisible.collectAsStateWithLifecycle().value,
                { BottomMenuPictureScreen(mediaViewModel) }
            )
        }

        Destination.FULLSCREENIMG.route -> {
            FunBottomMenu(fullScreenViewModel.bottomMenuFullScreenVisible.collectAsStateWithLifecycle().value,
                { BottomMenuFullScreen(fullScreenViewModel) }
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
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
        composable(Destination.ALBUMS.route) {
            AlbumsScreen(modifier, navController)
        }

        composable(Destination.PICTURES.route + "/{bID}") { stackEntry ->
            val bID = stackEntry.arguments?.getString("bID").toString()
            PicturesScreen(modifier, navController, bID)
        } // TODO fixme нижнее меню перекрывает часть картинок, как-то надо на его высоту картинки приподнять

        composable(Destination.FULLSCREENIMG.route + "/{bID}/{mediaPosition}",
            arguments = listOf(
                navArgument("bID") {type = NavType.StringType},
                navArgument("mediaPosition") {type = NavType.IntType}
            )) { stackEntry ->
            val bID = stackEntry.arguments?.getString("bID").toString()
            val mediaPosition = stackEntry.arguments?.getInt("mediaPosition")
            FullScreenViewPager(modifier, navController, bID, mediaPosition!!)
        }

        composable(Destination.SEARCH_PHOTO.route) {
            SearchPhotoScreen(modifier, navController)
        }

        composable(Destination.SETTINGS.route) {
            SettingsScreen(modifier, navController)
        }
    }
}
