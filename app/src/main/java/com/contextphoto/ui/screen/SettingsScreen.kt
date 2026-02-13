package com.contextphoto.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.contextphoto.R
import com.contextphoto.data.Destination
import com.contextphoto.data.debugSpeedrun
import com.contextphoto.ui.SettingsViewModel
import com.contextphoto.utils.FunctionsApp.espRead
import com.contextphoto.utils.FunctionsApp.espWrite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWithScaffold(modifier: Modifier = Modifier,
                               navController: NavController,
                               settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val buttonWidth = Modifier.width(300.dp)
    val corutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val completedOperation = settingsViewModel.operationCompleted.collectAsStateWithLifecycle()
    val stateInfo = settingsViewModel.stateInfo.collectAsStateWithLifecycle()
    val currentUser = settingsViewModel.currentUser.collectAsStateWithLifecycle()
    currentUser.value?.getIdToken(false)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            currentUser.value?.getIdToken(false)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result?.token
                    settingsViewModel.setToken(token?:"")
                    espWrite(context, espRead(context).first, settingsViewModel.getToken())
                    Log.d(debugSpeedrun, token.toString())
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        Destination.Settings().label,
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
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
                horizontalAlignment = Alignment.Start) {
                Button(
                    onClick = {
                        corutineScope.launch {
                            settingsViewModel.importCommentsFromStorage()
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.light_blue)
                    ),
                    modifier = buttonWidth
                        .padding(14.dp)
                        .height(50.dp)
                ) {
                    Text(context.getString(R.string.import_from_file))
                }

                Button(
                    onClick = {
                        corutineScope.launch {
                            settingsViewModel.exportCommentsToStorage()
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.light_blue)
                    ),
                    modifier = buttonWidth
                        .padding(14.dp)
                        .height(50.dp)
                ) {
                    Text(context.getString(R.string.export_to_file))
                }
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp))

                Button(
                    onClick = {
                        navController.navigate(
                            Destination.Login().route
                        )
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.light_blue)
                    ),
                    modifier = buttonWidth
                        .padding(14.dp)
                        .height(50.dp)
                ) {
                    Text(context.getString(R.string.enter_in_account))
                }

                Button(
                    onClick = {
                        val idToken = settingsViewModel.getToken()
                        val espData = espRead(context)
//                Log.d(debugSpeedrun, espData.second)
//                Log.d(debugSpeedrun, idToken)
//                println(espData.second == idToken)
                        if (espData.second == idToken && listOf(espData.second, idToken).all { it != "" }) {
                            corutineScope.launch {
                                settingsViewModel.importCommentsFromFirestore()
                            }
                        }
                        else {
                            Toast.makeText(context, context.getString(R.string.no_login), Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.light_blue)
                    ),
                    modifier = buttonWidth
                        .padding(14.dp)
                        .height(50.dp)
                ) {
                    Text(context.getString(R.string.import_from_firebase))
                }

                Button(
                    onClick = {
                        val idToken = settingsViewModel.getToken()
                        val espData = espRead(context)
//                Log.d(debugSpeedrun, espData.second)
//                Log.d(debugSpeedrun, idToken)
//                println(espData.second == idToken)
                        if (espData.second == idToken && espData.second != "") {
                            corutineScope.launch {
                                settingsViewModel.exportCommentsToFirestore()
                            }
                        }
                        else {
                            Toast.makeText(context, context.getString(R.string.no_login), Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.light_blue)
                    ),
                    modifier = buttonWidth
                        .padding(14.dp)
                        .height(50.dp)
                ) {
                    Text(context.getString(R.string.export_to_firebase))
                }

                AnimatedVisibility(visible = completedOperation.value,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally()
                ) {
                    Text(stateInfo.value,
                        fontSize = 18.sp,
                        color = Color.Green
                    )
                    corutineScope.launch {
                        delay(5000)
                        settingsViewModel.changeOperationStatus(false)
                    }

                }
            }
        }
    )

}



//@Preview(showBackground = true)
//@Composable
//fun GreetngPreview() {
//    ContextPhotoTheme {
//        SettingsScreen(modifier = Modifier.fillMaxSize())
//    }
//}


