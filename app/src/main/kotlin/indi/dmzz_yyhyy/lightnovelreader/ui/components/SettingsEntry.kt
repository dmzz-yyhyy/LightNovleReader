package indi.dmzz_yyhyy.lightnovelreader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.BooleanUserData
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.FloatUserData
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.MenuOptions
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun SettingsSwitchEntry(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    checked: Boolean,
    booleanUserData: BooleanUserData,
    disabled: Boolean = false
) {
    SettingsSwitchEntry(
        modifier = modifier,
        title = title,
        description = description,
        checked = checked,
        onCheckedChange = booleanUserData::asynchronousSet,
        disabled = disabled
    )
}

@Composable
fun SettingsSwitchEntry(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    disabled: Boolean = false
) {
    FilledCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (disabled) Modifier.clickable {} else Modifier.clickable { onCheckedChange(!checked) })
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
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Switch(
                checked = checked,
                enabled = !disabled,
                onCheckedChange = if (disabled) null else onCheckedChange
            )
        }
    }
}

@Composable
fun SettingsSliderEntry(
    modifier: Modifier = Modifier,
    description: String,
    unit: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    floatUserData: FloatUserData
) {
    var tempValue by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        tempValue = value
    }
    SettingsSliderEntry(
        modifier = modifier,
        description = description,
        unit = unit,
        value = tempValue,
        valueRange = valueRange,
        onSlideChange = { tempValue = it },
        onSliderChangeFinished = { floatUserData.asynchronousSet(tempValue) }
    )
}

@Composable
fun SettingsSliderEntry(
    modifier: Modifier = Modifier,
    description: String,
    unit: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onSlideChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit
) {
    FilledCard(
        modifier = modifier,
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
                        AnimatedText(
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
    option: String? = null
) {
    SettingsClickableEntry(
        title = title,
        description = description,
        option = option,
        onClick = { }
    )
}

@Composable
fun SettingsClickableEntry(
    title: String,
    description: String,
    option: String? = null,
    openUrl: String
) {
    val context = LocalContext.current
    SettingsClickableEntry(
        title = title,
        description = description,
        option = option,
        onClick = {
            openUrl.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(context, intent, null)
            }
        }
    )
}

@Composable
fun SettingsClickableEntry(
    title: String,
    description: String,
    option: String? = null,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    FilledCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(
            modifier = Modifier.clickable {
                expanded = !expanded
                onClick.invoke()
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
