package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.AboutSettingsList
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.AppSettingsList
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.DisplaySettingsList

val SettingsScreenInfo = NavItem (
    route = Screen.Home.Settings.route,
    drawable = R.drawable.animated_settings,
    label = R.string.nav_settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    topBar: (@Composable (TopAppBarScrollBehavior, TopAppBarScrollBehavior) -> Unit) -> Unit,
    checkUpdate: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.settingsState
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadSettings()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        topBar { enterAlwaysScrollBehavior, _ ->
            TopBar(
                scrollBehavior = enterAlwaysScrollBehavior,
            )
        }
    }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        SettingsCard(
            title = "应用",
            icon = ImageVector.vectorResource(R.drawable.outline_settings_24px),
            content = { AppSettingsList(
                state = state,
                onUpdateChannelChanged = viewModel::onUpdateChannelChanged,
                onAutoUpdateChanged = viewModel::onAutoUpdateChanged,
                checkUpdate = checkUpdate
            ) }
        )
        SettingsCard(
            title = "显示",
            icon = ImageVector.vectorResource(R.drawable.light_mode_24px),
            content = { DisplaySettingsList(
                state = state,
                onLocaleChanged = viewModel::onAppLocaleChanged,
                onDarkModeChanged = viewModel::onDarkModeChanged
            ) }
        )
        /*SettingsCard(
            title = "阅读",
            icon = ImageVector.vectorResource(R.drawable.outline_bookmark_24px),
            content = { ReaderSettingsList(
                state = state,
            ) }
        )*/
        SettingsCard(
            title = "关于",
            icon = ImageVector.vectorResource(R.drawable.info_24px),
            content = { AboutSettingsList(
                state = state,
                onStatisticsChanged = viewModel::onStatisticsChanged
            ) }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    MediumTopAppBar(
        title = {
            Text(
                text = "设置",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            Box(Modifier.size(48.dp)) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(id = R.drawable.outline_settings_24px),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}


@Composable
fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 14.dp, end = 14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Hide content" else "Show content"
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    content()
                }
            }
        }
    }
}
