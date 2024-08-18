package indi.dmzz_yyhyy.lightnovelreader.theme

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import indi.dmzz_yyhyy.lightnovelreader.utils.LocaleUtil

@Composable
fun LightNovelReaderTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    darkMode: String,
    isDynamicColor: Boolean = false,
    appLocale: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme =
        if (isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if ((darkMode == "FollowSystem" && isDarkTheme) || (darkMode == "Enabled"))
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        else
            if ((darkMode == "FollowSystem" && isDarkTheme) || (darkMode == "Enabled"))
                darkColorScheme()
            else
                lightColorScheme()

    if (!LocalInspectionMode.current) {
        val view = LocalView.current
        val (language, variant) = appLocale.split("-")
        LocaleUtil.set(context, language = language, variant = variant)
        LaunchedEffect(context, view) {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setBackgroundDrawable(
                ColorDrawable(colorScheme.background.toArgb())
            )

            val controller = WindowCompat.getInsetsController(window, view)
            window.statusBarColor = Color.Transparent.toArgb()
            controller.isAppearanceLightStatusBars = !isDarkTheme

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                window.navigationBarColor = Color.Transparent.toArgb()
                controller.isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
