package com.contextphoto

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.compose.AndroidFragment
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.contextphoto.RequestPermissions.ComposePermissions
import com.contextphoto.data.Destination
import com.contextphoto.data.FABVisible
import com.contextphoto.data.MainViewModel
import com.contextphoto.data.Picture
import com.contextphoto.data.albumBid
import com.contextphoto.data.allAlbums
import com.contextphoto.data.bottomMenuVisible
import com.contextphoto.data.dialogVisible
import com.contextphoto.data.imageUri
import com.contextphoto.data.listPictures
import com.contextphoto.data.listpicture
import com.contextphoto.data.openAlbum
import com.contextphoto.data.selectProcess
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsMediaStore.getAllMedia
import com.contextphoto.utils.FunctionsMediaStore.getListAlbums
import kotlinx.coroutines.flow.collectIndexed
import java.lang.System.exit
import kotlin.math.absoluteValue

class MainActivity() : ComponentActivity() {
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
                            title = { Text(navController.currentBackStackEntryAsState().value?.destination?.label.toString()) },
//                            title = {Text(startDestination.label)},
                            navigationIcon = {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {

                        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentDestination == Destination.ALBUMS.route) {
                            bottomMenuVisible.value = false
                            selectProcess.value = false
                        }
                        if (currentDestination == Destination.PICTURES.route && selectProcess.value) {
                            bottomMenuVisible.value = true
                        }

                        if (currentDestination == Destination.FULLSCREENIMG.route) {
                            FABVisible.value = false
                            bottomMenuVisible.value = false
                        }
                        else FABVisible.value = true
                        AnimatedVisibility(bottomMenuVisible.value,
                            enter = slideInVertically()
                                    + expandVertically(
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                initialAlpha = 0.3f
                            ),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut())
                        {
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
                        }
                        AnimatedVisibility(bottomMenuVisible.value,
                            enter = slideInVertically()
                                    + expandVertically(
                                expandFrom = Alignment.Top
                            ) + fadeIn(
                                initialAlpha = 0.3f
                            ),
                            exit = slideOutVertically() + shrinkVertically(
                                    shrinkTowards = Alignment.Top
                                    ) + fadeOut())
                        {
                            BottomMenu()
                        }
                    },
                    floatingActionButton = {
                        AnimatedVisibility(FABVisible.value,
                            enter = fadeIn(),
                            exit = fadeOut()
                        )
                        {
                            FloatingActionButton(onClick = {
                                FABVisible.value = false
                                dialogVisible.value = true
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    content = { paddingValues ->
                        AppNavHost(navController, startDestination, modifier = Modifier.padding(paddingValues))
                        if (dialogVisible.value) CreateAlbumDialog({ dialogVisible.value = false })
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
fun AlbumsScreen(modifier: Modifier = Modifier, navController: NavController) {
    val albumList = getListAlbums(LocalContext.current)

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier
    )
    {
        items(
            items = albumList
        ) {
            AlbumItem(it,
                Modifier.padding(0.dp, 2.dp),
                onItemClick = { it -> navController.navigate(Destination.PICTURES.route)}
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
fun PicturesScreen(modifier: Modifier = Modifier, navController: NavController, bID: String="") {
    val content = remember { mutableStateListOf<Picture>() }
    listPictures = getAllMedia(LocalContext.current, albumBid)
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
                    Modifier.padding(3.dp),
                    onItemClick = { it -> navController.navigate(Destination.FULLSCREENIMG.route)}
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
    }
}

@Composable
fun FullScreenImg(modifier: Modifier = Modifier, navController: NavController) {


//    Box(modifier = Modifier.fillMaxSize()) {
//
//            AsyncImage(
//                model = imageUri,
//                contentScale = ContentScale.Fit,
//                contentDescription = "Example Image",
//                modifier = Modifier.background(Color.Black).fillMaxSize(),
////                placeholder = painterResource(id = R.drawable.placeholder), // Replace with your placeholder drawable
////                error = painterResource(id = R.drawable.error)  // Replace with your error drawable
//            )
//
//    }

    val pagerState = rememberPagerState(pageCount = {
        openAlbum.itemsCount
    })
    HorizontalPager(state = pagerState) { page -> // TODO add ViewPager и subsampling-scale-image-view
        var pictureUri = imageUri
        LaunchedEffect(Unit) {
            listPictures.collect {
                println(it)
                pictureUri = it.uri
            }
        }
        // Our page content
        Box(modifier = Modifier.fillMaxSize()
            .graphicsLayer {
                // Calculate the absolute offset for the current page from the
                // scroll position. We use the absolute value which allows us to mirror
                // any effects for both directions
                val pageOffset = (
                        (pagerState.currentPage - page) + pagerState
                            .currentPageOffsetFraction
                        ).absoluteValue

                // We animate the alpha, between 50% and 100%
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }) {

            AsyncImage(
                model = pictureUri,
                contentScale = ContentScale.Fit,
                contentDescription = "Example Image",
                modifier = Modifier.background(Color.Black).fillMaxSize(),
//                placeholder = painterResource(id = R.drawable.placeholder), // Replace with your placeholder drawable
//                error = painterResource(id = R.drawable.error)  // Replace with your error drawable
            )

        }
        Text(
            text = "Page: $page",
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@Composable
fun BottomMenu() {
    val context = LocalContext.current
    Row(modifier = Modifier
        .background(Color.Black)
        .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
//                if (listpicture.isNotEmpty()) {
//                    // val sendCommentText = db.findImageByHash(md5(it.thumbnail))
//                    val sendIntent = Intent()
//                    sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE)
//                    sendIntent.setType("*/*")
//                    sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(listpicture.map { it.uri }))
////                        sendIntent.putExtra(Intent.EXTRA_TEXT, sendCommentText)
//                    startActivity(sendIntent) // TODO fixme intent
//                }
            },
            onLongClick = {}
        ),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Share, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.share),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
        Column(modifier = Modifier.padding(8.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Create, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.commentate),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
        Column(modifier = Modifier.padding(8.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Add, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.to_album),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
        }
        Column(modifier = Modifier.padding(8.dp, 16.dp).combinedClickable(
            onClick = {
                // TODO fixme опять беда с composable штукой
                //deleteDialog({}, allAlbums[0], false) // allAlbums[0] просто заглушка, что бы много функций не плодить
            },
            onLongClick = {}
        ),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(Icons.Outlined.Delete, contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White))
            Text(text = context.getString(R.string.delete),
                style = MaterialTheme.typography.labelSmall,
                color = colorResource(R.color.white))
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

        composable(Destination.ALBUMS.route) {
            AlbumsScreen(modifier, navController)
        }

        composable(Destination.PICTURES.route) {
            PicturesScreen(modifier, navController)
        }

        composable(Destination.FULLSCREENIMG.route) {
            FullScreenImg(modifier, navController)
        }

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ContextPhotoTheme {
        //PicturesScreen()
    }
}