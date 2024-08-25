package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.Bookshelf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookshelfScreen(
    title: String,
    bookshelfId: Int,
    topBar: (@Composable (TopAppBarScrollBehavior, TopAppBarScrollBehavior) -> Unit) -> Unit,
    dialog: (@Composable () -> Unit) -> Unit,
    bookshelf: Bookshelf,
    inti: (Int) -> Unit,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
    onClickDelete: () -> Unit,
    onNameChange: (String) -> Unit,
    onAutoCacheChange: (Boolean) -> Unit,
    onSystemUpdateReminderChange: (Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var dialogVisible by remember { mutableStateOf(false) }
    topBar { enterAlwaysScrollBehavior, _ ->
        TopBar(
            title = title,
            scrollBehavior = enterAlwaysScrollBehavior,
            onClickBack = onClickBack,
            onClickSave = onClickSave
        )
    }
    dialog {
        if (dialogVisible)
            DeleteDialog(
                onDismissRequest = { dialogVisible = false },
                onConfirmation = {
                    dialogVisible = false
                    onClickDelete.invoke()
                }
            )
    }
    LaunchedEffect(bookshelfId) {
        inti(bookshelfId)
    }
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            value = bookshelf.name,
            onValueChange = onNameChange,
            label = { Text("名称") },
            placeholder = { Text("输入书本名称") },
            supportingText = { Text("为书架命名，建议长度在6个汉字以内") },
            maxLines = 1,
            interactionSource = interactionSource,
            trailingIcon = {
                IconButton(onClick = { onNameChange("") }) {
                    Icon(
                        painter = painterResource(R.drawable.cancel_24px),
                        contentDescription = "cancel",
                        tint =
                        if (isFocused) OutlinedTextFieldDefaults.colors().focusedTrailingIconColor
                        else OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor
                    )
                }
            }
        )
        Text(
            modifier = Modifier.padding(16.dp, 10.dp),
            text = "书架设置",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.W700,
            fontSize = 17.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SwitchSettingItem(
            title = "自动缓存",
            description = "自动缓存新加入的书本完整内容",
            value = bookshelf.autoCache,
            onValueChange = onAutoCacheChange
        )
        SwitchSettingItem(
            title = "更新通知提醒",
            description = "如果书本有更新，通过系统通知提示我（限后台时）",
            value = bookshelf.systemUpdateReminder,
            onValueChange = onSystemUpdateReminderChange
        )
        ListItem(
            modifier = Modifier.clickable {
                dialogVisible = true
            },
            headlineContent = { Text(text = "删除此书架", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.W500, fontSize = 16.sp) },
            supportingContent = { Text(text = "将此书架永久移除", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.W500, fontSize = 12.sp, lineHeight = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClickBack) {
                Icon(painterResource(
                    id = R.drawable.arrow_back_24px),
                    contentDescription = "back"
                )
            }
        },
        actions = {
            IconButton(onClickSave) {
                Icon(
                    painter = painterResource(R.drawable.save_24px),
                    contentDescription = "save"
                )
            }
        },
        windowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun SwitchSettingItem(
    title: String,
    description: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    ListItem(
        headlineContent = { Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.W500, fontSize = 16.sp) },
        supportingContent = { Text(text = description, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.W500, fontSize = 12.sp, lineHeight = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = {
            Switch(
                checked = value,
                onCheckedChange = onValueChange
            )
        }
    )
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit) {
    AlertDialog(
        title = {
            Text(
                text = "删除书架",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.W400
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = "确定要删除这个书架吗？它将会永久失去！（真的很久！）",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.W400
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text(
                    text = "确定",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = "取消",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.W500
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}