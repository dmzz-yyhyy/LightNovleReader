package indi.dmzz_yyhyy.lightnovelreader.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.colintheshots.twain.MarkdownText
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf


@Composable
fun BaseDialog(
    icon: Painter,
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dismissText: String,
    confirmationText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .width(312.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(top = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = icon,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.W400,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.W400,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                content.invoke(this)
            }
            Box(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(8.dp, 24.dp, 24.dp, 24.dp)
                        .align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier
                            .padding(12.dp, 10.dp)
                            .clickable(onClick = onDismissRequest),
                    ) {
                        Text(
                            text = dismissText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Box(
                        Modifier
                            .padding(12.dp, 10.dp)
                            .clickable(onClick = onConfirmation),
                    ) {
                        Text(
                            text = confirmationText,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UpdatesAvailableDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    contentMarkdown: String? = null,
    newVersionName: String? = null,
    newVersionCode: Int = 0,
    downloadSize: String? = null,
    downloadUrl: String? = null
) {
    val context = LocalContext.current
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
                        text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) → $newVersionName($newVersionCode), ${formatted}MB")
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
                                text = stringResource(R.string.changelog),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        item {
                            MarkdownText(
                                markdown = it,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = stringResource(R.string.decline))
                }
                TextButton(
                    onClick = {
                        downloadUrl?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(context, intent, null)
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.manual_download))
                }
            }
        }
    )
}

@Composable
fun AddBookToBookshelfDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onSelectBookshelf: (Int) -> Unit,
    onDeselectBookshelf: (Int) -> Unit,
    allBookshelf: List<Bookshelf>,
    selectedBookshelfIds: List<Int>
) {
    BaseDialog(
        icon = painterResource(R.drawable.filled_bookmark_24px),
        title = "添加至书架",
        description = "将这本小说添加到以下分组",
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        dismissText = "取消",
        confirmationText = "添加至选定分组",
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .fillMaxWidth()
        ) {
            allBookshelf.forEachIndexed { index, bookshelf ->
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    headlineContent = { Text(text = bookshelf.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.W400) },
                    supportingContent = { Text(text = "共 ${bookshelf.allBookIds.size} 本", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.W400, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingContent = {
                        Checkbox(
                            checked = selectedBookshelfIds.contains(bookshelf.id),
                            onCheckedChange = {
                                if (it) onSelectBookshelf(bookshelf.id) else onDeselectBookshelf(bookshelf.id)
                            }
                        )
                    }
                )
                if (index != allBookshelf.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}