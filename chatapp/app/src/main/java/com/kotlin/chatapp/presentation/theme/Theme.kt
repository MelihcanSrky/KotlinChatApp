package com.kotlin.chatapp.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xff96adfb),
    secondary = Color(0xff818181),
    tertiary = Pink40,
    background = Color(0xFF232323),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xff373737),
    onSecondary = Color(0xff555555),
    onTertiary = Color.White,
    onBackground = Color(0xFF131416),
    onSurface = Color(0xFF1C1B1F),
    error = Color(0xffCD5037),
    onPrimaryContainer = Color(0xff373737)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff5d7ff2),
    secondary = Color(0xffa2a2a2),
    tertiary = Pink40,
    background = Color(0xFFfafafa),
    surface = Color(0xFF000000),
    onPrimary = Color(0xffcfd7f2),
    onSecondary = Color(0xffebebeb),
    onTertiary = Color.Black,
    onBackground = Color(0xFFF4F6F6),
    onSurface = Color(0xFF1C1B1F),
    error = Color(0xffF44725),
    onPrimaryContainer = Color(0xfff5f5f5)
)

@Composable
fun ChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = ChatAppTypo,
        content = content
    )
}