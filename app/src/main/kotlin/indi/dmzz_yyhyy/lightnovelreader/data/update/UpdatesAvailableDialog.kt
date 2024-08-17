package indi.dmzz_yyhyy.lightnovelreader.data.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig

@Composable
fun UpdatesAvailableDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onIgnore: () -> Unit,
    contentMarkdown: String? = null,
    newVersion: String? = null,
    downloadSize: String? = null
) {
    AlertDialog(
        title = {
            Text(
                text = "更新可用",
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                newVersion?.let {
                    Text(
                        text = buildString {
                            appendLine("有新的更新可用:")
                            append(BuildConfig.VERSION_NAME).append(" → ").append(it)
                            append("  (").append(downloadSize).append("MB)")
                        }
                    )
                }
                contentMarkdown?.let {
                    LazyColumn(
                        modifier = Modifier.height(350.dp).padding(top = 20.dp)
                    ) {
                        item {
                            MarkdownText(it)
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(text = "安装更新")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = "下次提醒")
            }
        }
    )
}