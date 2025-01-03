package indi.dmzz_yyhyy.lightnovelreader.ui.home.settings.data

sealed class ObjectOptions(vararg options: Option) {
    val optionsList: List<Option> = options.toList()

    open class Option(
        val key: String,
        val name: String,
        val description: String,
        val url: String?
    ) {
        override fun equals(other: Any?): Boolean = this.key == (other as? String)
        override fun hashCode(): Int = key.hashCode()
    }

    data object GitHubProxyUrlOptions: ObjectOptions(
        Option("disabled", "未指定", "不使用加速地址", ""),
        Option("gh-proxy.com", "gh-proxy.com", "第三方提供的公益加速服务", "https://gh-proxy.com/"),
        Option("ghgo.xyz", "ghgo.xyz", "第三方提供的公益加速服务", "https://ghgo.xyz/"),
        Option("custom", "自定义", "自行填写加速地址站点", null),
    )
}
