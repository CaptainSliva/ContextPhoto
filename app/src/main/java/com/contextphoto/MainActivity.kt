package com.contextphoto

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.contextphoto.RequestPermissions.RequestMultiplePermissions
import com.contextphoto.data.Destination
import com.contextphoto.data.Picture
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        setContent {
//            requestPermissions()

            val multiplePermissionsState = rememberMultiplePermissionsState(when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    listOf(
                        READ_MEDIA_IMAGES,
                        READ_MEDIA_VIDEO
                    )
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    listOf(
                        READ_EXTERNAL_STORAGE,
                        MANAGE_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                    )
                }
                else ->
                    listOf(
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                    )
            })
            val navController = rememberNavController()
            val startDestination = Destination.ALBUMS
            var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }


            ContextPhotoTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(LocalResources.current.getString(R.string.albums)) },
                            navigationIcon = {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                            Destination.entries.forEachIndexed { index, destination ->
                                NavigationBarItem(
                                    selected = selectedDestination == index,
                                    onClick = {
                                        try {
                                            navController.navigate(route = destination.route)
                                            selectedDestination = index
                                        }catch (e: Exception){ }
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
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null)
                        }
                    },
                    content = {
                        paddingValues ->
                        if (!multiplePermissionsState.allPermissionsGranted) {
                            RequestMultiplePermissions(multiplePermissionsState, modifier = Modifier.padding(paddingValues))
                        }
                        else {
                            AppNavHost(navController, startDestination, modifier = Modifier.padding(paddingValues))
                        }
                    }
                )

            }
        }
    }
}

private fun displayToast(context: Context) {
    Toast.makeText(context, "This is a Sample Toast", Toast.LENGTH_LONG).show()
}

@Composable
fun AlbumsScreen(modifier: Modifier = Modifier) {
    val albumList = getListAlbums(LocalContext.current)
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier
    )
    {
        items(albumList.size) {
            AlbumItem(albumList[it],
                Modifier.padding(0.dp, 2.dp)
            )
//            AlbumItem(
//                Album(
//                    "1",
//                    "hru $it",
//                    4,
//                    BitmapFactory.decodeResource(LocalResources.current, R.drawable.chchch),
//                    File("a.jpg")
//                ),
//                Modifier.padding(0.dp, 1.dp)
//            )
        }
    }
}

@Composable
fun PicturesScreen(modifier: Modifier = Modifier, bID: String="") {
    val content = remember { mutableStateListOf<Picture>() }
    val listPictures = getAllMedia(LocalContext.current, bID)
    LaunchedEffect(Unit) {
        listPictures.collect {
            println(it)

            content.add(it)
        }
    }

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
        ) {
        items(content.size) {
            println("\n\nPRIIIINT\n${content.size}\nIIITT\n$it\n")
            PictureItem(
                content[it],
                Modifier.padding(3.dp)
            )
        }
//            items(18) {
//                PictureItem(
//                    Picture(
//                        "1",
//                        "hru".toUri(),
//                        "a.jpg",
//                        BitmapFactory.decodeResource(LocalResources.current, R.drawable.recoon),
//                        "2:66",
//                        false
//                    ),
//                    Modifier.padding(1.dp)
//                )
//            }
        }
        Row(modifier = Modifier.background(Color.Black)) {
            Column(modifier = Modifier.padding(8.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(Icons.Outlined.Share, contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White))
                Text(text = "Поделиться",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.white))
            }
            Column(modifier = Modifier.padding(8.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(Icons.Outlined.Create, contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White))
                Text(text = "Комментировать",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.white))
            }
            Column(modifier = Modifier.padding(8.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(Icons.Outlined.Add, contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White))
                Text(text = "В альбом",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.white))
            }
            Column(modifier = Modifier.padding(8.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(Icons.Outlined.Delete, contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White))
                Text(text = "Удалить",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorResource(R.color.white))
            }
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
        PicturesScreen()
    }
}