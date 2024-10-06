package indi.dmzz_yyhyy.lightnovelreader.ui.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

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
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W400
            )
        },
        supportingContent = {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}