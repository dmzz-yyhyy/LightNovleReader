package indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.filter

import androidx.annotation.IntRange

abstract class SliderFilter(
    private val title: String,
    val description: String,
    defaultValue: Float,
    val valueRange: ClosedFloatingPointRange<Float>,
    @IntRange(from = 0) val steps: Int = 0,
    private val onChange: () -> Unit
) : Filter() {
    abstract var enabled: Boolean
    abstract val displayValue: String
    open val displayTitle = title

    var value: Float = defaultValue
        set(value) {
            onChange()
            field = value
        }
    override fun getType(): FilterTypes = FilterTypes.SLIDER
    override fun getTitle(): String = title
}