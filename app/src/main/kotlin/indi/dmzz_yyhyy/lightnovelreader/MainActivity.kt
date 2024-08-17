package indi.dmzz_yyhyy.lightnovelreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userDataRepository: UserDataRepository
    @Inject
    lateinit var updateCheckRepository: UpdateCheckRepository
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

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
            if (!BuildConfig.DEBUG) {
                AppCenter.start(
                    application,
                    update("eNpb85aBtYRBJc3c3MTYwshAN808JVnXxNIiTTfJ2DBFNzXZ1MDYKMkgxcwsBQAG3Aux").toString(),
                    Analytics::class.java,
                    Crashes::class.java
                )
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            updateCheckRepository.checkUpdate()
            updateCheckRepository.isNeedUpdateFlow.collect {
                if (it) {
                    println("NEED UPDATE")
                } else {
                    println("NO NEED UPDATE")
                }
            }
        }
        installSplashScreen()
        setContent {
            LightNovelReaderTheme(
                darkMode = darkMode,
                appLocale = appLocale
            ) {
                LightNovelReaderApp()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}