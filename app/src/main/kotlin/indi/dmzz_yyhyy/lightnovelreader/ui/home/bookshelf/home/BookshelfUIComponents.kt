package indi.dmzz_yyhyy.lightnovelreader.ui.home.bookshelf.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import indi.dmzz_yyhyy.lightnovelreader.R
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.ui.components.Cover
import indi.dmzz_yyhyy.lightnovelreader.utils.SwipeAction

@Composable
fun BookCardContent(
    selected: Boolean,
    modifier: Modifier = Modifier,
    bookInformation: BookInformation,
    latestChapterTitle: String? = null
) {
    Row(
        modifier = modifier.height(136.dp).padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(90.dp, 136.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier.graphicsLayer(alpha = if (selected) 0.7f else 1f)
            ) {
                Cover(
                    width = 90.dp,
                    height = 136.dp,
                    url = bookInformation.coverUrl,
                    rounded = 8.dp
                )
                if (latestChapterTitle != null) {
                    Box(
                        modifier = Modifier.padding(4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Badge(
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val color = MaterialTheme.colorScheme.primary
                    Canvas(
                        modifier = Modifier.size(36.dp)
                    ) {
                        drawCircle(
                            color = color,
                            radius = 18.dp.toPx(),
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .size(22.dp),
                        painter = painterResource(R.drawable.check_24px),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null
                    )
                }
            }
        }


        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            val titleLineHeight = 20.sp
            Text(
                modifier = Modifier.height(
                    with(LocalDensity.current) { (titleLineHeight * 2).toDp() }
                ).wrapContentHeight(Alignment.CenterVertically),
                text = bookInformation.title,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                lineHeight = titleLineHeight,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bookInformation.author,
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 20.sp,
                    fontSize = 14.sp,
                )
                BookStatusIcon(bookInformation)
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    text = stringResource(
                        R.string.book_info_update_date,
                        bookInformation.lastUpdated.year,
                        bookInformation.lastUpdated.monthValue,
                        bookInformation.lastUpdated.dayOfMonth
                    )
                )
                Text(
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    text = stringResource(
                        R.string.book_info_word_count_kilo,
                        bookInformation.wordCount / 1000
                    )
                )
            }
            println("$latestChapterTitle !!!")
            if (latestChapterTitle == null) {
                Text(
                    text = bookInformation.description.trim(),
                    maxLines = 2,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    lineHeight = 18.sp,
                )
            } else {
                Column {
                    Row {

                        Text(
                            text = "已更新至: ",
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                        )
                    }
                    Row {
                        Text(
                            text = latestChapterTitle,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookStatusIcon(bookInformation: BookInformation) {
    val modifier = Modifier.height(16.dp).width(16.dp)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (bookInformation.isComplete) {
            Icon(
                modifier = modifier,
                painter = painterResource(R.drawable.done_all_24px),
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.outline
            )
        } else {
            Icon(
                modifier = modifier,
                painter = painterResource(R.drawable.hourglass_top_24px),
                contentDescription = "In Progress",
                tint = MaterialTheme.colorScheme.outline
            )
        }

        // 可实现: 已动画化标识

        /*if (bookInformation.isAnimated) {
            Icon(
                modifier = modifier,
                painter = painterResource(R.drawable.live_tv_24px),
                contentDescription = "Animated",
                tint = MaterialTheme.colorScheme.outline
            )
        }*/
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookCardItem(
    modifier: Modifier = Modifier,
    bookInformation: BookInformation,
    selected: Boolean = false,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    latestChapterTitle: String? = null,
    swipeToRightAction: SwipeAction = SwipeAction.None,
    swipeToLeftAction: SwipeAction = SwipeAction.None,
    progress: (SwipeAction) -> Unit?,
    haptic: HapticFeedback
){

    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { it * 0.6f },
        confirmValueChange = {
            when (it) {
                StartToEnd -> {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    progress.invoke(swipeToRightAction)
                    println("OK Start to End")
                }
                EndToStart -> {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    progress.invoke(swipeToLeftAction)
                    println("OK End to Start")
                }
                Settled -> { }
            }
            false
        },
    )

    LaunchedEffect(dismissState.dismissDirection) {
        if (dismissState.dismissDirection != Settled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surface,
        animationSpec = tween(durationMillis = 300)
    )

    Card {
        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            enableDismissFromEndToStart = swipeToLeftAction != SwipeAction.None,
            enableDismissFromStartToEnd = swipeToRightAction != SwipeAction.None,
            backgroundContent = {
                DismissBackground(
                    dismissState = dismissState,
                    swipeToLeftAction = swipeToLeftAction,
                    swipeToRightAction = swipeToRightAction
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .combinedClickable(
                            onClick = onClick,
                            onLongClick = onLongPress,
                        )
                ) {
                    BookCardContent(
                        selected = selected,
                        latestChapterTitle = latestChapterTitle,
                        bookInformation = bookInformation
                    )
                }
            }
        )
    }
}


@Composable
private fun DismissBackground(
    dismissState: SwipeToDismissBoxState,
    swipeToRightAction: SwipeAction,
    swipeToLeftAction: SwipeAction
) {
    val color = when (dismissState.dismissDirection) {
        StartToEnd -> swipeToRightAction.color
        EndToStart -> swipeToLeftAction.color
        Settled -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .clip(RoundedCornerShape(12.dp))
            .padding(28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (dismissState.dismissDirection == StartToEnd) {
            Icon(
                painter = painterResource(id = swipeToRightAction.iconRes),
                contentDescription = swipeToRightAction.description,
                tint = MaterialTheme.colorScheme.surface
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (dismissState.dismissDirection == EndToStart) {
            Icon(
                painter = painterResource(id = swipeToLeftAction.iconRes),
                contentDescription = swipeToLeftAction.description,
                tint = MaterialTheme.colorScheme.surface
            )
        }
    }
}
