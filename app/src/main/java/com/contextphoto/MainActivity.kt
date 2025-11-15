package com.contextphoto

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.navigation.compose.composable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.contextphoto.ListItem.AlbumItem
import com.contextphoto.ListItem.PictureItem
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.data.Album
import com.contextphoto.data.Destination
import com.contextphoto.data.Picture
import com.contextphoto.ui.theme.ContextPhotoTheme
import java.io.File

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        setContent {
//            requestPermissions()
//            RequestPermissionExample()
            ComposePermissions()
            val navController = rememberNavController()
            val startDestination = Destination.ALBUMS
            var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

            ContextPhotoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(LocalContext.current.resources.getString(R.string.albums)) },
                            navigationIcon = {
                                Icon(Icons.Default.ArrowBack,
                                    contentDescription = null)
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                            Destination.entries.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == index,
                                    onClick = {
                                        navController.navigate(route = destination.route)
                                        selectedDestination = index
                                    },
                                    icon = {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.contentDescription
                                        )
                                    },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {}) {
                            Icon(Icons.Default.Add,
                                contentDescription = null)
                        }
                    },
                    content = {
                        paddingValues ->
                        AppNavHost(navController, startDestination, modifier = Modifier.padding(paddingValues))
//                        AlbumsScreen(
//                            modifier = Modifier.padding(paddingValues)
//                        )
                    }
                )

            }
        }
    }
}

@Composable
fun AlbumsScreen(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier
    ) {
        items(8) {
            AlbumItem(
                Album(
                    "1",
                    "hru $it",
                    4,
                    BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.chchch),
                    File("a.jpg")
                ),
                Modifier.padding(0.dp, 1.dp)
            )
        }
    }
}

@Composable
fun PicturesScreen(modifier: Modifier = Modifier) {
    Text("Hello toothless")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(18) {
            PictureItem(
                Picture(
                    "1",
                    "hru".toUri(),
                    "a.jpg",
                    BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.recoon),
                    "2:66",
                    false
                ),
                Modifier.padding(1.dp)
                )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.ALBUMS -> AlbumsScreen(modifier)
                    Destination.PICTURES -> PicturesScreen(modifier)
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContextPhotoTheme {
        AlbumsScreen()
    }
}