package com.contextphoto.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contextphoto.R
import com.contextphoto.ui.theme.ContextPhotoTheme

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var isShowPassword by rememberSaveable {mutableStateOf(false)}
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = rememberSaveable { mutableStateOf(TextFieldValue()) }
        val password = rememberSaveable { mutableStateOf(TextFieldValue()) }

        Text(text = context.getString(R.string.login_title), style = androidx.compose.ui.text.TextStyle(fontSize = 40.sp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = context.getString(R.string.email)) },
            value = email.value,
            onValueChange = { email.value = it },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            textStyle = TextStyle(fontSize = 20.sp))
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

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom) {
        Button(
            onClick = { },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
        ) {
            Text(text = context.getString(R.string.login),
                color = Color.White,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
            Text(text = context.getString(R.string.alternative_login),
                fontSize = 20.sp,
                modifier = Modifier.background(Color.White)
            )
        }

        Image(painter = painterResource(R.drawable.google),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .border(2.dp, color = Color.LightGray, shape = RoundedCornerShape(20))
                .padding(8.dp)
        )
        Text(context.getString(R.string.login_bottom_text),
            color = Color.Cyan,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable(onClick = {
                })
        )


    }
}

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    var isShowPassword by rememberSaveable {mutableStateOf(false)}
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = rememberSaveable { mutableStateOf(TextFieldValue()) }
        val password = rememberSaveable { mutableStateOf(TextFieldValue()) }
        val checkedPrivacy = rememberSaveable { mutableStateOf(false) }

        Text(text = context.getString(R.string.register_title), style = TextStyle(fontSize = 40.sp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = context.getString(R.string.email)) },
            value = email.value,
            onValueChange = { email.value = it },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            textStyle = TextStyle(fontSize = 20.sp))
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
            Checkbox(checked = checkedPrivacy.value,
                onCheckedChange = {
                    checkedPrivacy.value = !checkedPrivacy.value
                })
            Text(context.getString(R.string.privacy_policy))
        }

    }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom) {
        Button(
            onClick = { },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
        ) {
            Text(text = context.getString(R.string.register),
                color = Color.White,
                fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
            Text(text = context.getString(R.string.alternative_login),
                fontSize = 20.sp,
                modifier = Modifier.background(Color.White)
            )
        }
        
        Image(painter = painterResource(R.drawable.google),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .border(2.dp, color = Color.LightGray, shape = RoundedCornerShape(20))
                .padding(8.dp)
        )
        Text(context.getString(R.string.register_bottom_text),
            color = Color.Cyan,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable(onClick = {

                })
        )


    }
}

@Preview(showBackground = true)
@Composable
fun GreetngPreview() {
    ContextPhotoTheme {
        LoginScreen()
    }
}