package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.ui.Screen
import indi.dmzz_yyhyy.lightnovelreader.ui.components.NavItem
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.AboutSettingsList
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.AppSettingsList
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.DataSettingsList
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.list.DisplaySettingsList

val SettingsScreenInfo = NavItem (
    route = Screen.Home.Settings.route,
    drawable = R.drawable.animated_settings,
    label = R.string.nav_settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    topBar: (@Composable () -> Unit) -> Unit,
    dialog: (@Composable () -> Unit) -> Unit,
    checkUpdate: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val pinnedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    topBar {
        TopBar(pinnedScrollBehavior,)
    }
    AnimatedVisibility(
        viewModel.settingState != null
    ) {
        val settingState = viewModel.settingState!!
        Column(
            Modifier.verticalScroll(rememberScrollState())
                .nestedScroll(pinnedScrollBehavior.nestedScrollConnection)
        ) {
            SettingsCard(
                title = stringResource(R.string.app_settings),
                icon = ImageVector.vectorResource(R.drawable.outline_settings_24px)
            ) {
                AppSettingsList(
                    settingState = settingState,
                    checkUpdate = checkUpdate
                )
            }
            SettingsCard(
                title = stringResource(R.string.display_settings),
                icon = ImageVector.vectorResource(R.drawable.light_mode_24px)
            ) {
                DisplaySettingsList(settingState = settingState)
            }
            /*SettingsCard(
                indi.dmzz_yyhyy.lightnovelreader.data.statistics.getTitle = "阅读",
                icon = ImageVector.vectorResource(R.drawable.outline_bookmark_24px),
                content = { ReaderSettingsList(
                    state = state,
                ) }
            )*/
            SettingsCard(
                title = "数据",
                icon = ImageVector.vectorResource(R.drawable.hard_disk_24px)
            ) {
                DataSettingsList(
                    settingState = settingState,
                    dialog = dialog,
                    exportDataToFile = viewModel::exportToFile,
                    exportAndSendToFile = viewModel::exportAndSendToFile,
                    importData = viewModel::importFromFile
                )
            }
            SettingsCard(
                title = stringResource(R.string.about_settings),
                icon = ImageVector.vectorResource(R.drawable.info_24px)
            ) {
                AboutSettingsList(settingState = settingState)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.nav_settings),
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
    content: @Composable ColumnScope.() -> Unit
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
                Box(
                    modifier = Modifier.padding(top = 0.dp, end = 14.dp, start = 14.dp, bottom = 14.dp)
                ) {
                    Column(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        content = content
                    )
                }
            }
        }
    }
}
