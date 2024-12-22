package indi.dmzz_yyhyy.lightnovelreader.utils

import androidx.compose.ui.graphics.Color
import indi.dmzz_yyhyy.lightnovelreader.R

@Suppress("LeakingThis")
sealed class SwipeAction(
    val id: String,
    val iconRes: Int,
    val color: Color,
    val description: String,
) {

    companion object {
        private val _all = mutableMapOf<String, SwipeAction>()
        val all: Map<String, SwipeAction> = _all
    }

    init {
        _all[id] = this
    }

    data object AddToBookshelf : SwipeAction(
        id = "add_to_bookshelf",
        iconRes = R.drawable.bookmark_add_24px,
        color = Color(0xff2ECC71),
        description = ""
    )

    data object RemoveFromBookshelf : SwipeAction(
        id = "remove_from_bookshelf",
        iconRes = R.drawable.delete_forever_24px,
        color = Color(0xffE74C3C),
        description = ""
    )

    data object Pin : SwipeAction(
        id = "pin",
        iconRes = R.drawable.keep_24px,
        color = Color(0xff007AFF),
        description = ""
    )

    data object Expand : SwipeAction(
        id = "expand",
        iconRes = R.drawable.expand_circle_down_24px,
        color = Color(0xffF1C40F),
        description = ""
    )

    data object Info : SwipeAction(
        id = "info",
        iconRes = R.drawable.info_24px,
        color = Color(0xffE67E22),
        description = ""
    )

    data object None : SwipeAction(
        id = "none",
        iconRes = R.drawable.block_24px,
        color = Color.Transparent,
        description = ""
    )
}