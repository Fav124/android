package com.example.deisaapplication.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CoronaDarkColorScheme = darkColorScheme(
    primary           = Primary,
    onPrimary         = OnPrimary,
    secondary         = Secondary,
    onSecondary       = OnSecondary,
    background        = AppBackground,
    onBackground      = OnAppBackground,
    surface           = AppSurface,
    onSurface         = OnAppSurface,
    surfaceVariant    = AppSurfaceVariant,
    onSurfaceVariant  = MutedText,
    error             = AppError,
    onError           = OnPrimary,
)

@Composable
fun DeisaApplicationTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = CoronaDarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}
