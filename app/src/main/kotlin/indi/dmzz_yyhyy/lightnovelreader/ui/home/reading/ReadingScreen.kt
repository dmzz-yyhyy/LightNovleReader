package indi.dmzz_yyhyy.lightnovelreader.ui.home.reading

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.home.ReadingHomeScreen
import indi.dmzz_yyhyy.lightnovelreader.ui.home.reading.statistics.ReadingStatisticsScreen

val ReadingScreenInfo = NavItem(
    route = Screen.Home.Reading.route,
    drawable = R.drawable.animated_book,
    label = R.string.nav_reading
)

@Composable
fun ReadingScreen(
    onClickBook: (Int) -> Unit,
    onClickJumpToExploration: () -> Unit,
    onClickContinueReading: (Int, Int) -> Unit,
    topBar: (@Composable () -> Unit) -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Home.Reading.Home.route) {
        composable(
            route = Screen.Home.Reading.Home.route
        ) {
            ReadingHomeScreen(
                topBar = topBar,
                onClickBook = onClickBook,
                onClickContinueReading = onClickContinueReading,
                onClickJumpToExploration = onClickJumpToExploration,
                onClickStatistics = { navController.navigate(Screen.Home.Reading.Statistics.route) }
            )
        }
        composable(
            route = Screen.Home.Reading.Statistics.route
        ) {
            ReadingStatisticsScreen(
                topBar = topBar,
                onClickBack = { navController.popBackStack() },
                )
        }
    }

}