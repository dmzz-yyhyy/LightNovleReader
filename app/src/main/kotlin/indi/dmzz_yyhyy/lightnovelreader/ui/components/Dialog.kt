package indi.dmzz_yyhyy.lightnovelreader.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import dev.jeziellago.compose.markdowntext.MarkdownText
import indi.dmzz_yyhyy.lightnovelreader.BuildConfig
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf
import indi.dmzz_yyhyy.lightnovelreader.data.update.UpdateCheckRepository.Companion.proxyUrlRegex
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.StringUserData
import indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data.ObjectOptions

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
    BaseDialog(
        icon = icon,
        title = title,
        description = description,
        onDismissRequest = onDismissRequest,
    ) {
        content.invoke(this)
        Row(
            modifier = Modifier
                .padding(8.dp, 24.dp, 24.dp, 24.dp)
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(
                onClick = onConfirmation
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

@Composable
fun BaseDialog(
    icon: Painter,
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .sizeIn(minWidth = 280.dp, maxWidth = 560.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(Modifier.height(24.dp))
            Icon(
                modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                painter = icon,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
            Box(Modifier.height(16.dp))
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.W400,
            )
            Box(Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .sizeIn(minWidth = 280.dp, maxWidth = 560.dp)
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Start,
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(Modifier.height(16.dp))
            content.invoke(this)
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
    downloadSize: Double? = null,
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
                    val sizeInMB = ((downloadSize ?: 0.0) / 1024) / 1024
                    val formatted = "%.2f".format(sizeInMB)
                    Text(
                        text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE}) → $newVersionName($newVersionCode), ${formatted}MB"
                    )
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
    val scrollState = rememberScrollState()
    BaseDialog(
        icon = painterResource(R.drawable.filled_bookmark_24px),
        title = "添加至书架",
        description = "将这本小说添加到以下分组",
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        dismissText = "取消",
        confirmationText = "添加至选定分组",
    ) {
        Column(Modifier.width(IntrinsicSize.Max).sizeIn(maxHeight = 350.dp).verticalScroll(scrollState)) {
            allBookshelf.forEachIndexed { index, bookshelf ->
                CheckBoxListItem(
                    modifier = Modifier
                        .sizeIn(minWidth = 280.dp, maxWidth = 500.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    title = bookshelf.name,
                    supportingText = "共 ${bookshelf.allBookIds.size} 本",
                    checked = selectedBookshelfIds.contains(bookshelf.id),
                    onCheckedChange = {
                        if (it) onSelectBookshelf(bookshelf.id) else onDeselectBookshelf(
                            bookshelf.id
                        )
                    }
                )
                if (index != allBookshelf.size - 1) {
                    HorizontalDivider(Modifier.padding(horizontal = 14.dp))
                }
            }
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SliderDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onSlideChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit,
    title: String,
    description: String
) {
    BaseDialog(
        icon = painterResource(R.drawable.filled_settings_24px),
        title = title,
        description = description,
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        dismissText = stringResource(R.string.cancel),
        confirmationText = stringResource(R.string.apply),
    ) {
        val sliderPercentage = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val current = LocalDensity.current
        var indicatorWidthDp by remember { mutableStateOf(0F) }

        Box(modifier = Modifier.width(350.dp))  {
            Box(
                modifier = Modifier
                    .offset(x = ((sliderPercentage * 300 + 25) - (indicatorWidthDp / 2)).dp)
                    .clip(RoundedCornerShape(64.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .padding(12.dp)
            ) {
                Text(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            with(current) {
                                indicatorWidthDp = layoutCoordinates.size.width.toDp().value
                            }
                        }
                        .padding(horizontal = 12.dp),
                    text = value.toInt().toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        }
        Slider(
            modifier = Modifier.width(300.dp).align(Alignment.CenterHorizontally),
            value = value,
            valueRange = valueRange,
            steps = steps,
            onValueChange = onSlideChange,
            onValueChangeFinished = onSliderChangeFinished,
            colors = SliderDefaults.colors(
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        )
    }
}


interface ExportContext {
    val bookshelf: Boolean
    val readingData: Boolean
    val settings: Boolean
    val bookmark: Boolean
}

class MutableExportContext: ExportContext {
    override var bookshelf by mutableStateOf(true)
    override var readingData by mutableStateOf(true)
    override var settings by mutableStateOf(true)
    override var bookmark by mutableStateOf(true)
}

@Composable
fun ExportDialog(
    onDismissRequest: () -> Unit,
    onClickSaveAndSend: (ExportContext) -> Unit,
    onClickSaveToFile: (ExportContext) -> Unit
) {
    val mutableExportContext = remember { MutableExportContext() }
    val listItemModifier = Modifier
        .sizeIn(minWidth = 280.dp, maxWidth = 500.dp)
        .fillMaxWidth()
        .padding(horizontal = 14.dp)
    BaseDialog(
        icon = painterResource(R.drawable.output_24px),
        title = "导出数据",
        description = "选择需要导出的数据。",
        onDismissRequest = onDismissRequest,
    ) {
        Column(Modifier.width(IntrinsicSize.Max).sizeIn(maxHeight = 350.dp)) {
            CheckBoxListItem(
                modifier = listItemModifier,
                title = "书架",
                supportingText = "包括书架及书本信息",
                checked = mutableExportContext.bookshelf,
                onCheckedChange = { mutableExportContext.bookshelf = it }
            )
            HorizontalDivider(Modifier.padding(horizontal = 14.dp))
            CheckBoxListItem(
                modifier = listItemModifier,
                title = "阅读信息",
                supportingText = "包括阅读历史、进度和时长等信息",
                checked = mutableExportContext.readingData,
                onCheckedChange = { mutableExportContext.readingData = it }
            )
            HorizontalDivider(Modifier.padding(horizontal = 14.dp))
            CheckBoxListItem(
                modifier = listItemModifier,
                title = "设置项",
                supportingText = "包括应用设置和阅读器设置",
                checked = mutableExportContext.settings,
                onCheckedChange = { mutableExportContext.settings = it }
            )
            HorizontalDivider(Modifier.padding(horizontal = 14.dp))
            CheckBoxListItem(
                modifier = listItemModifier,
                title = "书签",
                supportingText = "包括全部书本的书签信息",
                checked = mutableExportContext.bookmark,
                onCheckedChange = { mutableExportContext.bookmark = it }
            )
            HorizontalDivider(Modifier.padding(horizontal = 14.dp))
        }
        Row(
            modifier = Modifier
                .padding(8.dp, 24.dp, 24.dp, 24.dp)
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "取消",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(
                onClick = { onClickSaveAndSend(mutableExportContext) }
            ) {
                Text(
                    text = "导出并分享",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(
                onClick = { onClickSaveToFile(mutableExportContext) }
            ) {
                Text(
                    text = "导出至文件",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

data class WebDataSourceItem(
    val id: Int,
    val name: String,
    val provider: String,
)

val wenku8ApiWebDataSourceItem = WebDataSourceItem(
    "wenku8".hashCode(),
    "Wenku8",
    "LightNovelReader from wenku8.net"
)

val zaiComicWebDataSourceItem = WebDataSourceItem(
    "ZaiComic".hashCode(),
    "ZaiComic",
    "LightNovelReader from zaimanhua.com"
)

@Composable
fun SourceChangeDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    webDataSourceItems: List<WebDataSourceItem>,
    selectedWebDataSourceId: Int,
    onClickItem: (Int) -> Unit
) {
    BaseDialog(
        icon = painterResource(R.drawable.public_24px),
        title = "切换数据源",
        description = "选择使用的数据源，切换软件的网络数据提供源，但这会导致你的用户数据被暂存，将在下次切换到此数据源后恢复。但是你的缓存数据会被永久删除，并且需要重启应用。",
        onDismissRequest = onDismissRequest,
        onConfirmation = onConfirmation,
        dismissText = "取消",
        confirmationText = "切换并重启"
    ) {
        webDataSourceItems.forEachIndexed { index, webDataSourceItem ->
            RadioButtonListItem(
                modifier = Modifier
                    .sizeIn(minWidth = 280.dp, maxWidth = 500.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                title = webDataSourceItem.name,
                supportingText = "提供者: ${webDataSourceItem.provider}",
                selected = selectedWebDataSourceId == webDataSourceItem.id,
                onClick = { onClickItem(webDataSourceItem.id) }
            )
            if (index != webDataSourceItems.size - 1) {
                HorizontalDivider(Modifier.padding(horizontal = 14.dp))
            }
        }
    }
}

@Composable
fun SettingsGitHubProxyDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
    proxyUrlUserData: StringUserData,
) {
    val proxyUrl = proxyUrlUserData.getOrDefault("")
    var selectedOption by remember {
        mutableStateOf(
            ObjectOptions.GitHubProxyUrlOptions.optionsList.find { it.url == proxyUrl }
                ?: ObjectOptions.GitHubProxyUrlOptions.optionsList.first { it.key == "custom" }
        )
    }
    var input by remember { mutableStateOf(if (selectedOption.url == null) proxyUrl else "") }
    var isValid by remember { mutableStateOf(true) }

    AlertDialog (
        onDismissRequest = onDismissRequest,
        title = { Text("设置 GitHub 代理") },
        text = {
            Column {
                ObjectOptions.GitHubProxyUrlOptions.optionsList.forEach { option ->
                    RadioButtonListItem(
                        title = option.name,
                        selected = selectedOption.key == option.key,
                        supportingText = option.description,
                        onClick = { selectedOption = option },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("请注意: 由于代理普遍不支持加速 GitHub API, 所以我们无法在检查环节使用代理。")
                if (selectedOption.key == "custom") {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        isError = !isValid,
                        value = input,
                        supportingText = {
                            Text(
                                "注意: 协议和结尾的\"/\"不可省略",
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        onValueChange = {
                            isValid = (it.isEmpty() || proxyUrlRegex.matches(it))
                            input = it
                        },
                        label = {
                            Text("自定义站点")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (!isValid) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "正确格式示范: \n" +
                                    "https://example.com/\n" +
                                    "https://nth.3rd.example.com/",
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                }

            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when (selectedOption.key) {
                        "custom" -> onConfirmation(input.ifBlank { "" })
                        "disabled" -> onConfirmation("")
                        else -> onConfirmation(selectedOption.url.toString().ifBlank { "" })
                    }
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}