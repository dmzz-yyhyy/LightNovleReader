package indi.dmzz_yyhyy.lightnovelreader.ui.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportingText: String,
    trailingContent: @Composable () -> Unit,
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        supportingContent = {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        trailingContent = trailingContent
    )
}

@Composable
fun CheckBoxListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportingText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        modifier = modifier,
        title = title,
        supportingText = supportingText,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun RadioButtonListItem(
    modifier: Modifier = Modifier,
    title: String,
    supportingText: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier,
        title = title,
        supportingText = supportingText,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}