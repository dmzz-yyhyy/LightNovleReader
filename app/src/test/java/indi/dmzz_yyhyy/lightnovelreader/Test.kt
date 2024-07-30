package indi.dmzz_yyhyy.lightnovelreader

import java.net.URLEncoder
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jsoup.Connection
import org.jsoup.Jsoup

fun Connection.wenku8Cookie(): Connection =
    this.cookie("__51uvsct__1xtyjOqSZ75DRXC0", "1")
        .cookie(" __51vcke__1xtyjOqSZ75DRXC0", "5fd1e310-a176-5ee6-9144-ed977bccf14e")
        .cookie(" __51vuft__1xtyjOqSZ75DRXC0", "1691164424380")
        .cookie(
            " Hm_lvt_d72896ddbf8d27c750e3b365ea2fc902",
            "1695572903,1695666346,1696009387,1696966471"
        )
        .cookie(" Hm_lvt_acfbfe93830e0272a88e1cc73d4d6d0f", "1721130033,1721491724,1721570341")
        .cookie(" PHPSESSID", "4d1c461c284bfa784985dc462d92188a")
        .cookie(
            " jieqiUserInfo",
            "jieqiUserId%3D1125456%2CjieqiUserName%3Dyyhyy%2CjieqiUserGroup%3D3%2CjieqiUserVip%3D0%2CjieqiUserPassword%3Deb62861281462fd923fb99218735fef0%2CjieqiUserName_un%3Dyyhyy%2CjieqiUserHonor_un%3D%26%23x666E%3B%26%23x901A%3B%26%23x4F1A%3B%26%23x5458%3B%2CjieqiUserGroupName_un%3D%26%23x666E%3B%26%23x901A%3B%26%23x4F1A%3B%26%23x5458%3B%2CjieqiUserLogin%3D1721745838"
        )
        .cookie(" jieqiVisitInfo", "jieqiUserLogin%3D1721745838%2CjieqiUserId%3D1125456")
        .cookie(
            " cf_clearance",
            "rAZBJvDmKV_DyAMY3k8n0_tMWW_lEz3ycWfYtjfTPcg-1721745844-1.0.1.1-mqt8uqswt6KtEdjtDq5m_yrRpR0x6QUhux3.J5B_OQMCso87cCu2psOEn0KVC1xOzmJinWcs7eeZTAi1ruNA_w"
        )
        .cookie(" HMACCOUNT", "10DAC0CE2BEFA41A")
        .cookie(" _clck", "jvuxvk%7C2%7Cfnp%7C0%7C1658")
        .cookie(" Hm_lvt_d72896ddbf8d27c750e3b365ea2fc902", "")
        .cookie(" Hm_lpvt_d72896ddbf8d27c750e3b365ea2fc902", "1721745932")
        .cookie(" _clsk", "1xyg0vc%7C1721745933282%7C2%7C1%7Co.clarity.ms%2Fcollect")

private val DATA_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun main() {
    val searchResult = MutableStateFlow(emptyList<BookInformation>())
    val keyword = "我"
    val searchType = "articlename"
    Jsoup
        .connect("https://www.wenku8.net/modules/article/search.php?searchtype=$searchType&searchkey=${URLEncoder.encode(keyword, Charset.forName("gb2312"))}")
        .wenku8Cookie()
        .get()
        .selectFirst("#pagelink > a.last")?.text()?.toInt()?.let { maxPage ->
            (0..<maxPage).map{ index ->
                searchResult.update {
                    it + Jsoup
                        .connect("https://www.wenku8.net/modules/article/search.php?searchtype=$searchType&searchkey=${URLEncoder.encode(keyword, Charset.forName("gb2312"))}&page=$index")
                        .wenku8Cookie()
                        .get()
                        .select("#content > table > tbody > tr > td > div")
                        .map { element ->
                            BookInformation(
                                id = element.selectFirst("div > div:nth-child(1) > a")
                                    ?.attr("href")
                                    ?.replace("/book/", "")
                                    ?.replace(".htm", "")
                                    ?.toInt() ?: -1,
                                title = element.selectFirst("div > div:nth-child(1) > a")
                                    ?.attr("title") ?: "",
                                coverUrl = element.selectFirst("div > div:nth-child(1) > a > img")
                                    ?.attr("src") ?: "",
                                author = element.selectFirst("div > div:nth-child(2) > p:nth-child(2)")
                                    ?.text()?.split("/")?.getOrNull(0)
                                    ?.split(":")?.getOrNull(1) ?: "",
                                description = element.selectFirst("div > div:nth-child(2) > p:nth-child(5)")
                                    ?.text()?.replace("简介:", "") ?: "",
                                publishingHouse = element.selectFirst("div > div:nth-child(2) > p:nth-child(2)")
                                    ?.text()?.split("/")?.getOrNull(1)
                                    ?.split(":")?.getOrNull(1) ?: "",
                                wordCount = element.selectFirst("div > div:nth-child(2) > p:nth-child(3)")
                                    ?.text()?.split("/")?.getOrNull(1)
                                    ?.split(":")?.getOrNull(1)
                                    ?.replace("K", "")?.toInt()?.times(1000) ?: -1,
                                lastUpdated = element.selectFirst("div > div:nth-child(2) > p:nth-child(3)")
                                    ?.text()?.split("/")?.getOrNull(0)
                                    ?.split(":")?.getOrNull(1)
                                    ?.let {
                                        LocalDate.parse(it, DATA_TIME_FORMATTER)
                                    }
                                    ?.atStartOfDay() ?: LocalDateTime.MIN,
                                isComplete = element.selectFirst("div > div:nth-child(2) > p:nth-child(3)")
                                    ?.text()?.split("/")?.getOrNull(2) == "已完结"
                            )
                        }
                }
            }
        }
}