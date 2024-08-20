package indi.dmzz_yyhyy.lightnovelreader

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import dagger.hilt.android.AndroidEntryPoint
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import indi.dmzz_yyhyy.lightnovelreader.theme.LightNovelReaderTheme
import indi.dmzz_yyhyy.lightnovelreader.ui.LightNovelReaderApp
import indi.dmzz_yyhyy.lightnovelreader.utils.update
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userDataRepository: UserDataRepository
    @Inject
    lateinit var updateCheckRepository: UpdateCheckRepository
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var isUsingVolumeKeyFlip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var appLocale by mutableStateOf(Locale.current.language)
        var darkMode by mutableStateOf("FollowSystem")
        var statisticsEnabled by mutableStateOf(true)
        coroutineScope.launch(Dispatchers.IO) {
            userDataRepository.stringUserData(UserDataPath.Settings.Display.AppLocale.path).getFlow().collect {
                appLocale = it ?: "${Locale.current.platformLocale.language}-${Locale.current.platformLocale.variant}"
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            userDataRepository.stringUserData(UserDataPath.Settings.Display.DarkMode.path).getFlow().collect {
                darkMode = it ?: "FollowSystem"
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            statisticsEnabled = userDataRepository.booleanUserData(UserDataPath.Settings.App.Statistics.path).getOrDefault(true)
            if (!BuildConfig.DEBUG && statisticsEnabled) {
                AppCenter.start(
                    application,
                    update("eNpb85aBtYRBJc3c3MTYwshAN808JVnXxNIiTTfJ2DBFNzXZ1MDYKMkgxcwsBQAG3Aux").toString(),
                    Analytics::class.java,
                    Crashes::class.java
                )
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            userDataRepository.booleanUserData(UserDataPath.Reader.IsUsingVolumeKeyFlip.path).getFlow().collect {
                it?.let { isUsingVolumeKeyFlip = it }
            }
        }
        installSplashScreen()
        if (Build.VERSION.SDK_INT >= 33) { /* Android 13 + */
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(POST_NOTIFICATIONS), 0
                )
            }
        }
        setContent {
            LightNovelReaderTheme(
                darkMode = darkMode,
                appLocale = appLocale
            ) {
                LightNovelReaderApp()
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(AppEvent.KEYCODE_VOLUME_UP))
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(AppEvent.KEYCODE_VOLUME_DOWN))
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                return isUsingVolumeKeyFlip
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                return isUsingVolumeKeyFlip
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}