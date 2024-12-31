package indi.dmzz_yyhyy.lightnovelreader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
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
    iconRes: Int = -1,
    title: String,
    description: String,
    checked: Boolean,
    booleanUserData: BooleanUserData,
    disabled: Boolean = false
) {
    SettingsSwitchEntry(
        modifier = modifier,
        iconRes = iconRes,
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
    iconRes: Int,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    disabled: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .wrapContentHeight()
            .then(if (!disabled) Modifier.clickable { onCheckedChange(!checked) } else Modifier)
            .padding(start = 18.dp, end = 14.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (iconRes > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Icon"
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
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
    iconRes: Int = -1,
    modifier: Modifier = Modifier,
    title: String,
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
        iconRes = iconRes,
        modifier = modifier,
        title = title,
        unit = unit,
        value = tempValue,
        valueRange = valueRange,
        onSlideChange = { tempValue = it },
        onSliderChangeFinished = { floatUserData.asynchronousSet(tempValue) }
    )
}

@Composable
private fun SettingsSliderEntry(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    unit: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onSlideChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .wrapContentHeight()
            .padding(start = 18.dp, end = 14.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (iconRes > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Icon"
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${DecimalFormat("#.#").format(value)}$unit",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                lineHeight = 18.sp,
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsMenuEntry(
    iconRes: Int = -1,
    title: String,
    description: String? = null,
    options: MenuOptions,
    selectedOptionKey: String,
    onOptionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .wrapContentHeight()
            .pointerInteropFilter {
                offset = Offset(it.x, it.y); false
            }
            .clickable { expanded = !expanded }
            .padding(start = 18.dp, end = 14.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (iconRes > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Icon"
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            description?.let {
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            AnimatedText(
                text = options.get(selectedOptionKey).name,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier.offset {
                    IntOffset(offset.x.toInt(), 0)
                }
            ) {
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
                            text = { Text(option.name, fontSize = 14.sp) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsClickableEntry(
    iconRes: Int = -1,
    title: String,
    description: String,
    option: String? = null
) {
    SettingsClickableEntry(
        iconRes = iconRes,
        title = title,
        description = description,
        option = option,
        onClick = { }
    )
}

@Composable
fun SettingsClickableEntry(
    iconRes: Int = -1,
    title: String,
    description: String,
    option: String? = null,
    openUrl: String
) {
    val context = LocalContext.current
    SettingsClickableEntry(
        iconRes = iconRes,
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
    iconRes: Int = -1,
    title: String,
    description: String,
    option: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .wrapContentHeight()
            .clickable { onClick.invoke() }
            .padding(start = 18.dp, end = 14.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (iconRes > 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "Icon"
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            option?.let {
                AnimatedText(
                    text = it,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
