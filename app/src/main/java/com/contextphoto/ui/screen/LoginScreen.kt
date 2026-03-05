package com.contextphoto.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.contextphoto.R
import com.contextphoto.data.navigation.Destination
import com.contextphoto.ui.LoginViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsApp.espWrite
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var isShowPassword by rememberSaveable { mutableStateOf(false) }
    val errorMessage = loginViewModel.errorMessage.collectAsStateWithLifecycle()
    val currentUser = loginViewModel.currentUser.collectAsStateWithLifecycle()
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    Surface(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = context.getString(R.string.login_title),
                    style =
                        androidx.compose.ui.text
                            .TextStyle(fontSize = 40.sp),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = context.getString(R.string.email)) },
                    value = email.value,
                    onValueChange = { email.value = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    textStyle = TextStyle(fontSize = 20.sp),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = context.getString(R.string.password)) },
                    value = password.value,
                    visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = { password.value = it },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    trailingIcon = {
                        val description = if (isShowPassword) "Show Password" else "Hide Password"
                        val iconImage =
                            if (isShowPassword) R.drawable.baseline_remove_red_eye_24 else R.drawable.eye_closed
                        IconButton(onClick = {
                            (!isShowPassword).also { isShowPassword = it }
                        }) {
                            Icon(
                                painter = painterResource(id = iconImage),
                                contentDescription = description,
                            )
                        }
                    },
                    textStyle = TextStyle(fontSize = 20.sp),
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(
                    onClick = {
                        loginViewModel.clearError()
                        loginViewModel.login(email.value, password.value)
                        currentUser.value?.getIdToken(false)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result?.token
                                espWrite(context, currentUser.value!!.email.toString(), token.toString())
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_blue),
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .wrapContentHeight(),
                ) {
                    Text(
                        text = context.getString(R.string.login),
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
                Text(
                    text = errorMessage.value,
                    modifier =
                        Modifier.clickable(onClick = {
                            FirebaseAuth.getInstance()
                            loginViewModel.logout()
                        }),
                )

                Text(
                    context.getString(R.string.login_bottom_text),
                    color = Color.Cyan,
                    textDecoration = TextDecoration.Underline,
                    modifier =
                        Modifier
                            .clickable(onClick = {
                                navController.popBackStack()
                                navController.navigate(
                                    Destination.Registration().route,
                                )
                            }),
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val errorMessage = loginViewModel.errorMessage.collectAsStateWithLifecycle()
    val currentUser = loginViewModel.currentUser.collectAsStateWithLifecycle()
    var isShowPassword by rememberSaveable { mutableStateOf(false) }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val checkedPrivacy = rememberSaveable { mutableStateOf(false) }
    val showConfidence = rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = context.getString(R.string.register_title),
                    style = TextStyle(fontSize = 40.sp),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = context.getString(R.string.email)) },
                    value = email.value,
                    onValueChange = { email.value = it },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    textStyle = TextStyle(fontSize = 20.sp),
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = context.getString(R.string.password)) },
                    value = password.value,
                    visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = { password.value = it },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    trailingIcon = {
                        val description = if (isShowPassword) "Show Password" else "Hide Password"
                        val iconImage =
                            if (isShowPassword) R.drawable.baseline_remove_red_eye_24 else R.drawable.eye_closed
                        IconButton(onClick = {
                            (!isShowPassword).also { isShowPassword = it }
                        }) {
                            Icon(
                                painter = painterResource(id = iconImage),
                                contentDescription = description,
                            )
                        }
                    },
                    textStyle = TextStyle(fontSize = 20.sp),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = checkedPrivacy.value,
                        onCheckedChange = {
                            checkedPrivacy.value = !checkedPrivacy.value
                        },
                        colors =
                            CheckboxDefaults.colors(
                                checkedColor = colorResource(R.color.light_blue),
                                checkmarkColor = Color.White,
                                disabledCheckedColor = Color.White,
                            ),
                    )
                    Text(
                        context.getString(R.string.privacy_policy_text),
                        textDecoration = TextDecoration.Underline,
                        modifier =
                            Modifier.clickable(onClick = {
                                showConfidence.value = !showConfidence.value
                            }),
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Button(
                    onClick = {
                        if (checkedPrivacy.value) {
                        } else {
                            Toast.makeText(context, getString(context, R.string.get_policy), Toast.LENGTH_LONG).show()
                        }
                        loginViewModel.clearError()
                        loginViewModel.registration(email.value, password.value)
                        currentUser.value?.getIdToken(false)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                currentUser.value?.getIdToken(false)?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val token = task.result?.token
                                        espWrite(context, currentUser.value!!.email.toString(), token.toString())
                                    }
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_blue),
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                            .wrapContentHeight(),
                ) {
                    Text(
                        text = context.getString(R.string.register),
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
                Text(
                    text = errorMessage.value,
                    modifier =
                        Modifier.clickable(onClick = {
                            FirebaseAuth.getInstance()
                            loginViewModel.logout()
                        }),
                )

                Text(
                    context.getString(R.string.register_bottom_text),
                    color = Color.Cyan,
                    textDecoration = TextDecoration.Underline,
                    modifier =
                        Modifier
                            .clickable(onClick = {
                                navController.popBackStack()
                                navController.navigate(
                                    Destination.Login().route,
                                )
                            }),
                )
            }
        }
    }
    AnimatedVisibility(
        visible = showConfidence.value,
        enter = expandVertically(),
        exit =
            shrinkVertically(),
    ) {
        PrivacyPolicyScreenWithScaffold(showConfidence)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetngPreview() {
    ContextPhotoTheme {
//        LoginScreen()
    }
}
