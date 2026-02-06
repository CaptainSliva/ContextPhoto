package com.contextphoto.ui.screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.contextphoto.R
import com.contextphoto.data.LoginViewModel
import com.contextphoto.ui.theme.ContextPhotoTheme
import com.contextphoto.utils.FunctionsApp.googleLogin
import com.contextphoto.utils.FunctionsApp.googleLogout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var isShowPassword by rememberSaveable {mutableStateOf(false)}
    val errorMessage = remember { mutableStateOf("") }
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult() // Контракт для запуска активити
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                    if (account != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, account.email, Toast.LENGTH_SHORT).show()
                                Toast.makeText(context, account.displayName, Toast.LENGTH_SHORT).show()
                                errorMessage.value = "${account.email} ${account.displayName}"
                            }
                        }
                    }

                } catch (e: ApiException) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                }


            }
            else {
                // Пользователь отменил вход
                errorMessage.value = "Вход отменен"
            }
        }


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val email = rememberSaveable { mutableStateOf("") }
            val password = rememberSaveable { mutableStateOf("") }

            Text(
                text = context.getString(R.string.login_title),
                style = androidx.compose.ui.text.TextStyle(fontSize = 40.sp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.email)) },
                value = email.value,
                onValueChange = { email.value = it },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                textStyle = TextStyle(fontSize = 20.sp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.password)) },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password.value = it },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    val description = if (isShowPassword) "Show Password" else "Hide Password"
                    val iconImage =
                        if (isShowPassword) R.drawable.eyeclosed else R.drawable.eye_closed
                    IconButton(onClick = {
                        (!isShowPassword).also { isShowPassword = it }
                    }) {
                        Icon(
                            painter = painterResource(id = iconImage),
                            contentDescription = description,
                        )
                    }
                },
                textStyle = TextStyle(fontSize = 20.sp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {
                    if (GoogleSignIn.getLastSignedInAccount(context) != null) {
                        FirebaseAuth.getInstance()
                        googleLogout(context,
                            { Toast.makeText(context, "Logging Out", Toast.LENGTH_SHORT).show() })
                    }
                    else {
                        firebaseAuth = FirebaseAuth.getInstance()
                        Toast.makeText(context, "Logging In", Toast.LENGTH_SHORT).show()
                        googleSignInLauncher.launch(googleLogin(context))
                    }

                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .height(50.dp),
            ) {
                Text(
                    text = context.getString(R.string.login),
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
            Text(text = errorMessage.value,
                modifier = Modifier.clickable(onClick = {
                    FirebaseAuth.getInstance()
                    googleLogout(context,
                        { Toast.makeText(context, "Logging Out", Toast.LENGTH_SHORT).show() })
                })
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = context.getString(R.string.alternative_login),
                    fontSize = 20.sp,
                    modifier = Modifier.background(Color.Unspecified).padding(10.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, color = Color.LightGray, shape = RoundedCornerShape(20))
                    .padding(8.dp)
            )
            Text(
                context.getString(R.string.login_bottom_text),
                color = Color.Cyan,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable(onClick = {

                    })
            )


        }
    }
}

@Composable
fun RegisterScreen(loginViewModel: LoginViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var isShowPassword by rememberSaveable {mutableStateOf(false)}
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val email = rememberSaveable { mutableStateOf("") }
            val password = rememberSaveable { mutableStateOf("") }
            val checkedPrivacy = rememberSaveable { mutableStateOf(false) }

            Text(
                text = context.getString(R.string.register_title),
                style = TextStyle(fontSize = 40.sp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.email)) },
                value = email.value,
                onValueChange = { email.value = it },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                textStyle = TextStyle(fontSize = 20.sp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = context.getString(R.string.password)) },
                value = password.value,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password.value = it },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    val description = if (isShowPassword) "Show Password" else "Hide Password"
                    val iconImage =
                        if (isShowPassword) R.drawable.eyeclosed else R.drawable.eye_closed
                    IconButton(onClick = {
                        (!isShowPassword).also { isShowPassword = it }
                    }) {
                        Icon(
                            painter = painterResource(id = iconImage),
                            contentDescription = description,
                        )
                    }
                },
                textStyle = TextStyle(fontSize = 20.sp)
            )

            Row {
                Checkbox(
                    checked = checkedPrivacy.value,
                    onCheckedChange = {
                        checkedPrivacy.value = !checkedPrivacy.value
                    })
                Text(context.getString(R.string.privacy_policy))
            }

        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = { },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .height(50.dp),
            ) {
                Text(
                    text = context.getString(R.string.register),
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = context.getString(R.string.alternative_login),
                    fontSize = 20.sp,
                    modifier = Modifier.background(Color.Unspecified).padding(10.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Image(
                painter = painterResource(R.drawable.google),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .border(2.dp, color = Color.LightGray, shape = RoundedCornerShape(20))
                    .padding(8.dp)
            )
            Text(
                context.getString(R.string.register_bottom_text),
                color = Color.Cyan,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable(onClick = {

                    })
            )


        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetngPreview() {
    ContextPhotoTheme {
        LoginScreen()
    }
}