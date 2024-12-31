package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data

sealed class MenuOptions(vararg options: Options) {
    private val _optionsList: MutableList<Options> = options.toMutableList()
    val optionsList: List<Options> get() = _optionsList.toList()
    fun option(key: String, name: String): String {
        _optionsList.add(Options(key, name))
        return key
    }
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

    data object FlipAnimeOptions: MenuOptions() {
        val None = option("none", "无")
        val ScrollWithoutShadow = option("scroll", "滚动")
    }
}