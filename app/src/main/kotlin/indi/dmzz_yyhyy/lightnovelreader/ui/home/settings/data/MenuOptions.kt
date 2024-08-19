package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data

sealed class MenuOptions(vararg options: Options) {
    val optionsList: List<Options> = options.toList()
    class Options(
        val key: String,
        val name: String
    ) {
        override fun equals(other: Any?): Boolean = this.key == other

        override fun hashCode(): Int = key.hashCode()
    }
    fun get(key: String): Options = optionsList.first { it.equals(key) }

    data object UpdateChannelOptions: MenuOptions(
        Options("Release", "正式发布版"),
        Options("Development", "开发版"),
    )

    data object DarkModeOptions: MenuOptions(
        Options("FollowSystem", "跟随系统"),
        Options("Enabled", "启用"),
        Options("Disabled", "禁用")
    )

    data object AppLocaleOptions: MenuOptions(
        Options("zh-CN", "zh_CN"),
        Options("zh-HK", "zh_HK"),
        Options("zh-TW", "zh_TW"),
        Options("ja-JP", "ja_JP")
    )
}