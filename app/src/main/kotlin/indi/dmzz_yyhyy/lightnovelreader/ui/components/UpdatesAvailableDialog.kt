package indi.dmzz_yyhyy.lightnovelreader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R

@Composable
fun UpdatesAvailableDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    contentMarkdown: String? = null,
    newVersionName: String? = null,
    newVersionCode: Int = 0,
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
                newVersionName?.let {
                    val sizeInMB = ((downloadSize?.toDoubleOrNull() ?: 0.0) / 1024) / 1024
                    val formatted = "%.2f".format(sizeInMB)
                    Text(
                        text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) → $newVersionName($newVersionCode)  ($formatted)MB")
                }
                contentMarkdown?.let {
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .wrapContentHeight()
                            .heightIn(max = 350.dp)
                    ) {
                        item {
                            Text(
                                text = "更新描述",
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
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
                Text(text = stringResource(R.string.install_update))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(R.string.decline))
            }
        }
    )
}