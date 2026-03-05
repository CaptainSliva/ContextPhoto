package com.contextphoto.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    onPrimary = Color.White,
//    primaryContainer = Purple80.copy(alpha = 0.4f),
//    onPrimaryContainer = Color.White,
//
//    secondary = PurpleGrey80,
//    onSecondary = Color.Black,
//    secondaryContainer = PurpleGrey80.copy(alpha = 0.4f),
//    onSecondaryContainer = Color.Black,
//
//    tertiary = Pink80,
//    onTertiary = Color.Black,
//    tertiaryContainer = Pink80.copy(alpha = 0.4f),
//    onTertiaryContainer = Color.Black,
//
//    background = Color(0xFF121212), // Темный фон
//    onBackground = Color.White,
//
//    surface = Color(0xFF1E1E1E), // Темная поверхность
//    onSurface = Color.White,
//
//    surfaceVariant = Color(0xFF2C2C2C),
//    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
//
//    error = Color(0xFFCF6679),
//    onError = Color.Black,
//
//    outline = Color.White.copy(alpha = 0.12f)
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    onPrimary = Color.White,
//    primaryContainer = Purple40.copy(alpha = 0.2f),
//    onPrimaryContainer = Color.Black,
//
//    secondary = PurpleGrey40,
//    onSecondary = Color.White,
//    secondaryContainer = PurpleGrey40.copy(alpha = 0.2f),
//    onSecondaryContainer = Color.Black,
//
//    tertiary = Pink40,
//    onTertiary = Color.White,
//    tertiaryContainer = Pink40.copy(alpha = 0.2f),
//    onTertiaryContainer = Color.Black,
//
//    background = Color(0xFFF5F5F5), // Светлый фон
//    onBackground = Color.Black,
//
//    surface = Color.White,
//    onSurface = Color.Black,
//
//    surfaceVariant = Color(0xFFE0E0E0),
//    onSurfaceVariant = Color.Black.copy(alpha = 0.6f),
//
//    error = Color(0xFFB00020),
//    onError = Color.White,
//
//    outline = Color.Black.copy(alpha = 0.12f)
//)

@Composable
fun ContextPhotoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}