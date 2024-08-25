package indi.dmzz_yyhyy.lightnovelreader.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.filter.Filter
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.filter.SingleChoiceFilter
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.filter.SwitchFilter

@Composable
fun Filter.Component(dialog: (@Composable () -> Unit) -> Unit) {
    when (this) {
        is SwitchFilter -> {
            var enable by remember { mutableStateOf(this.enable) }
            LaunchedEffect(this.enable) {
                enable = this@Component.enable
            }
            BaseFilter(
                title = this.getTitle(),
                selected = enable,
                onClick = {
                    enable = !enable
                    this.enable = enable
                }
            )
        }
        is SingleChoiceFilter -> {
            var enable by remember { mutableStateOf(this.choice != this.getDefaultChoice()) }
            var displayDialog by remember { mutableStateOf(false) }
            var selected by remember { mutableStateOf(this.choice) }
            LaunchedEffect(this.choice) {
                enable = this@Component.choice != this@Component.getDefaultChoice()
                displayDialog = false
                selected = this@Component.choice
            }
            LaunchedEffect(displayDialog) {
                if (displayDialog)
                    dialog {
                        FilterChipsDialog(
                            enable = displayDialog,
                            selected = selected,
                            title = this@Component.dialogTitle,
                            description = this@Component.description,
                            onSelectedChange = {
                                selected = it
                                enable = it != this@Component.getDefaultChoice()
                            },
                            choices = this@Component.getAllChoices(),
                            onConfirmation = {
                                displayDialog = false
                                this@Component.choice = selected
                            },
                            onDismissRequest = {
                                displayDialog = false
                                selected = this@Component.choice
                            },
                        )
                    }
                else
                    dialog {}
            }
            BaseFilter(
                title = "${this.getTitle()}: $selected",
                selected = enable,
                onClick = {
                    displayDialog = true
                }
            )
        }
    }
}

@Composable
fun BaseFilter(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            AnimatedContent(
                targetState = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "FilterTitleColorAnime",
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    color = it
                )
            }
        },
        modifier = modifier,
        leadingIcon = {
            AnimatedVisibility(
                visible = selected,
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.check_24px),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipsDialog(
    enable: Boolean,
    selected: String,
    title: String,
    description: String,
    choices: List<String>,
    onSelectedChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AnimatedVisibility(
        visible = enable,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        BaseDialog(
            icon = painterResource(R.drawable.text_fields_24px),
            title = title,
            description = description,
            onDismissRequest = onDismissRequest,
            onConfirmation = onConfirmation,
            dismissText = stringResource(id = R.string.cancel),
            confirmationText = stringResource(id = R.string.apply),
        ) {
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 33.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                choices.forEach { choice ->
                    FilterChip(
                        modifier = Modifier.padding(0.dp),
                        selected = choice == selected,
                        onClick = {
                            onSelectedChange(choice)
                        },
                        label = {
                            AnimatedContent(
                                targetState = if (choice == selected) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "FilterTitleColorAnime",
                            ) {
                                Text(
                                    text = choice,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.W500,
                                    fontSize = 14.sp,
                                    color = it
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}