package indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.filter

import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation

class WordCountFilter(onChange: () -> Unit) : SliderFilter(
    title = "字数限制",
    description = "仅显示字数大于该值的书本，若为0则显示全部书本。",
    defaultValue = 0f,
    valueRange = 0f..200_0000f,
    steps = 9,
    onChange = onChange
), LocalFilter {
    override var enabled: Boolean
        get() = value != 0f
        set(value) { if (!value) this.value = 0f }
    override val displayValue: String
        get() = if (value == 0f) "无限制" else "${(value / 1000).toInt()}K"

    override val displayTitle: String
        get() = "字数"
    override fun filter(bookInformation: BookInformation): Boolean =
        !enabled || bookInformation.wordCount >= value
}