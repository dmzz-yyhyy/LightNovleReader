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

    data object UpdatePlatformOptions: MenuOptions(
        Options("GitHub", "GitHub"),
        Options("AppCenter", "Microsoft App Center"),
        Options("LightNovelReader", "LightNovelReader API"),
    )

    data object DarkModeOptions: MenuOptions(
        Options("FollowSystem", "跟随系统"),
        Options("Enabled", "覆盖启用"),
        Options("Disabled", "覆盖禁用")
    )

    data object AppLocaleOptions: MenuOptions(
        Options("zh-CN", "简体中文 (中国大陆)"),
        Options("zh-HK", "繁體中文 (香港)"),
        Options("zh-TW", "繁體中文 (臺灣)"),
        Options("ja-JP", "日本語"),
        Options("ko-kr", "한국어 (대한민국)"),
        Options("ko-kp", "한국어 (조선민주주의인민공화국)")
    )
}