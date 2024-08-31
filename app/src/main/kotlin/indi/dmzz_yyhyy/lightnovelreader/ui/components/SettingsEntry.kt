package indi.dmzz_yyhyy.lightnovelreader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.content.ContextCompat.startActivity
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.roundToInt

/* NOTE
* SettingsSwitchEntry and SettingsSliderEntry renamed and moved from ContentScreen.kt
*/
@Composable
fun SettingsSwitchEntry(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    FilledCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(18.dp, 10.dp, 20.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .weight(2f)
                    .padding(end = 4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W500,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun SettingsSliderEntry(
    description: String,
    unit: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onSlideChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit
) {
    FilledCard(
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(Modifier.padding(18.dp, 10.dp, 20.dp, 12.dp)) {
            Text(
                text = description,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W500,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "${DecimalFormat("#.#").format(value)}$unit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W500,
                fontSize = 13.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                valueRange = valueRange,
                onValueChange = { onSlideChange((it * 2).roundToInt().toFloat() / 2) },
                onValueChangeFinished = onSliderChangeFinished,
                colors = SliderDefaults.colors(
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

@Composable
fun SettingsMenuEntry(
    title: String,
    description: String,
    options: MenuOptions,
    selectedOptionKey: String,
    onOptionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    FilledCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(modifier = Modifier.clickable { expanded = !expanded }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp, 10.dp, 20.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(2f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W500,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Column {
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = options.get(selectedOptionKey).name,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.optionsList.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                onOptionChange(option.key)
                                expanded = false
                            },
                            text = { Text(option.name) },
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun SettingsClickableEntry(
    title: String,
    description: String,
    option: String? = null,
    openUrl: String? = null,
    onClick: suspend CoroutineScope.() -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    FilledCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(
            modifier = Modifier.clickable {
                expanded = !expanded
                if (onClick != {}) {
                    coroutineScope.launch(block = onClick)
                }
                openUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(context, intent, null)
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp, 10.dp, 20.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.weight(2f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W500,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Column {
                        Text(
                            text = description,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        option?.let {
                            Text(
                                text = it,
                                fontSize = 13.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
