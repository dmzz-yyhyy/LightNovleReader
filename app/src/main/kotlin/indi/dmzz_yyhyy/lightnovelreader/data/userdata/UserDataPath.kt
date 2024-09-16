package indi.dmzz_yyhyy.lightnovelreader.data.userdata

sealed class UserDataPath(
    private val name: String,
    private val parent: UserDataPath? = null,
) {
    open val path: String get() = "${parent?.path?.plus(".") ?: ""}$name"
    open val groupChildrenPath: MutableList<String> = emptyList<String>().toMutableList()
    open val groupChildren: MutableList<UserDataPath> = emptyList<UserDataPath>().toMutableList()
    init {
        parent?.let {
            groupChildrenPath.add("${parent.path.plus(".")}$name")
            groupChildren.add(this)
        }
    }
    data object Reader : UserDataPath("reader") {
        data object FontSize : UserDataPath("fontSize",Reader)
        data object FontLineHeight : UserDataPath("fontLineHeight", Reader)
        data object KeepScreenOn : UserDataPath("keepScreenOn", Reader)
        data object IsUsingFlipPage : UserDataPath("isUsingFlipPage", Reader)
        data object IsUsingClickFlipPage : UserDataPath("isUsingClickFlipPage", Reader)
        data object IsUsingVolumeKeyFlip : UserDataPath("isUsingVolumeKeyFlip", Reader)
        data object IsUsingFlipAnime : UserDataPath("isUsingFlipAnime", Reader)
        data object EnableBatteryIndicator : UserDataPath("enableBatteryIndicator", Reader)
        data object EnableTimeIndicator : UserDataPath("enableTimeIndicator", Reader)
        data object EnableReadingChapterProgressIndicator : UserDataPath("enableReadingChapterProgressIndicator", Reader)
        data object AutoPadding : UserDataPath("autoPadding", Reader)
        data object TopPadding : UserDataPath("topPadding", Reader)
        data object BottomPadding : UserDataPath("bottomPadding", Reader)
        data object LeftPadding : UserDataPath("leftPadding", Reader)
        data object RightPadding : UserDataPath("rightPadding", Reader)
    }
    data object ReadingBooks : UserDataPath("reading_books")
    data object Search: UserDataPath("search") {
        data object History : UserDataPath("history", Search)
    }
    data object Settings: UserDataPath("settings") {
        data object App : UserDataPath("app", Settings) {
            data object AutoCheckUpdate : UserDataPath("auto_check_update", App)
            data object UpdateChannel: UserDataPath("update_channel", App)
            data object Statistics : UserDataPath("statistics", App)
            data object MaxCache : UserDataPath("max_cache", App)
        }
        data object Display: UserDataPath("display", Settings) {
            data object DarkMode : UserDataPath("dark_mode", Display)
            data object DynamicColors : UserDataPath("dynamic_color", Display)
            data object AppLocale : UserDataPath("app_locale", Display)
        }
        data object Reader : UserDataPath("reader", Settings) {
            data object FontSize : LinkUserData(Reader.FontSize)
            data object FontLineHeight : LinkUserData(Reader.FontLineHeight)
            data object KeepScreenOn : LinkUserData(Reader.KeepScreenOn)
        }
    }
    data object Bookshelf : UserDataPath("bookshelf") {
    }
}

open class LinkUserData(
    private val userDataPath: UserDataPath
): UserDataPath("") {
    override val path: String
        get() = userDataPath.path
    override val groupChildrenPath = userDataPath.groupChildrenPath
    override val groupChildren = userDataPath.groupChildren
}