package com.contextphoto.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.contextphoto.R
import com.contextphoto.data.navigation.Destination
import com.contextphoto.ui.SettingsViewModel
import com.contextphoto.utils.FunctionsApp.espRead
import com.contextphoto.utils.FunctionsApp.espWrite


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenWithScaffold(navController: NavHostController,
                               settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val buttonWidth = Modifier.width(300.dp)
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
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically)
                    {
                        Text(
                            Destination.Settings().label,
                        )
                        if (currentUser.value?.email != null) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp).weight(1f),
                                text = "${currentUser.value!!.email}",
                                fontSize = 13.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        backActions(navController)
                    }) {
                        Icon(
                            Icons.Default.ArrowBack, // Кнопка назад
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            BackHandler {
                backActions(navController)
            }

            Row(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)) {

            Column(
                modifier = Modifier
                    .weight(0.7f),
                horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Button(
                            onClick = {
                                settingsViewModel.changeOperationStatus(false)
                                settingsViewModel.exportCommentsToStorage()
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.light_blue)
                            ),
                            modifier = buttonWidth
                                .weight(1f)
                        ) {
                            Text(
                                context.getString(R.string.export_to_file),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.25f))
                        Button(
                            onClick = {
                                settingsViewModel.changeOperationStatus(false)
                                settingsViewModel.importCommentsFromStorage()
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.light_blue)
                            ),
                            modifier = buttonWidth
                                .weight(1f)
                        ) {
                            Text(
                                context.getString(R.string.import_from_file),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(vertical = 16.dp))
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
                                .fillMaxWidth()
                        ) {
                            Text(
                                context.getString(R.string.enter_in_account),
                                textAlign = TextAlign.Center
                            )
                        }

                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Button(
                            onClick = {
                                settingsViewModel.changeOperationStatus(false)
                                val idToken = settingsViewModel.getToken()
                                val espData = espRead(context)
                                if (espData.second == idToken && idToken != "") {
                                    settingsViewModel.exportCommentsToFirestore()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.no_login),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.light_blue)
                            ),
                            modifier = buttonWidth
                                .weight(1f)
                        ) {
                            Text(
                                context.getString(R.string.export_to_firebase),
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.25f))
                        Button(
                            onClick = {
                                settingsViewModel.changeOperationStatus(false)
                                val idToken = settingsViewModel.getToken()
                                val espData = espRead(context)
                                if (espData.second == idToken && idToken != "") {
                                    settingsViewModel.importCommentsFromFirestore()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.no_login),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.light_blue)
                            ),
                            modifier = buttonWidth
                                .weight(1f)
                        ) {
                            Text(
                                context.getString(R.string.import_from_firebase),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = completedOperation.value,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        Text(
                            stateInfo.value,
                            fontSize = 18.sp,
                            color = Color.Green
                        )
//                        settingsViewModel.changeOperationStatus(false)

                    }
                }
                Spacer(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f))
            }
        }
    )

}


private fun backActions(navController: NavHostController) {
    navController.navigateUp()
}


//@Preview(showBackground = true)
//@Composable
//fun GreetngPreview() {
//    ContextPhotoTheme {
//        SettingsScreen(modifier = Modifier.fillMaxSize())
//    }
//}


