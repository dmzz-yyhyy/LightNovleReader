package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.exploration

import com.google.gson.reflect.TypeToken
import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationBooksRow
import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationDisplayBook
import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationPage
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationPageDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.ZaiComic
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.ZaiComic.HOST
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.ZaiComic.getBookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json.RecommendData
import indi.dmzz_yyhyy.lightnovelreader.utils.autoReconnectionGetJsonText
import indi.dmzz_yyhyy.lightnovelreader.utils.getImageSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

object RecommendExplorationPageDataSource : ExplorationPageDataSource {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var lock = false
    private val explorationBooksRows: MutableStateFlow<List<ExplorationBooksRow>> = MutableStateFlow(emptyList())
    private val explorationPage = ExplorationPage("推荐", explorationBooksRows)

    override fun getExplorationPage(): ExplorationPage {
        if (lock) return explorationPage
        lock = true
        scope.launch {
            Jsoup
                .connect(HOST + "/app/v1/comic/recommend/index?channel=android&timestamp=${(System.currentTimeMillis() / 1000)}")
                .autoReconnectionGetJsonText()
                .let {
                    ZaiComic.gson.fromJson<List<RecommendData>>(
                        it,
                        object : TypeToken<List<RecommendData>>() {}.type
                    )
                }
                .forEach { recommendData ->
                    val books = recommendData.data.mapNotNull {
                        if (it.type != 1) return@mapNotNull null
                        val coverSize = getImageSize(it.cover) ?: return@mapNotNull null
                        if (coverSize.width > coverSize.height )
                            return@mapNotNull ExplorationDisplayBook(it.id, it.title, getBookInformation(it.id)?.coverUrl ?: return@mapNotNull null)
                        ExplorationDisplayBook(it.id, it.title, it.cover)
                    }
                    if (books.isEmpty()) return@forEach
                    explorationBooksRows.update {
                        it + ExplorationBooksRow(recommendData.title, books)
                    }
                }
        }
        return explorationPage
    }
}