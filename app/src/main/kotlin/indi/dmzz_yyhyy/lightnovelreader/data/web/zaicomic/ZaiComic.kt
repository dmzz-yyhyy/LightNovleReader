package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookVolumes
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationExpandedPageDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationPageDataSource
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.json.ComicChapterComic
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.json.DataContent
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.json.DetailData
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.json.ListDataContent
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.json.SearchItem
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.json.ZaiComicData
import java.net.URLEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ZaiComic : WebBookDataSource {
    class LimitedMap<K, V>(private val limitNum: Int): LinkedHashMap<K, V>() {
        private val keyList = mutableListOf<K>()

        override fun put(key: K, value: V): V? {
            keyList.add(key)
            if (keyList.size > limitNum) {
                this.remove(keyList[0])
                keyList.removeAt(0)
            }
            return super.put(key, value)
        }
    }
    private const val HOST = "https://v4api.zaimanhua.com"
    private val gson = Gson()
    private val comicDetailCacheMap: MutableMap<Int, DetailData> = LimitedMap(10)
    private val comicVolumesCacheMap: MutableMap<Int, BookVolumes> = LimitedMap(10)
    private var searchJob: Job? = null

    override val isOffLineFlow = flow {
        while(true) {
            emit(isOffLine())
            delay(2500)
        }
    }

    private fun ZaiComicData<DataContent<DetailData>>.cacheDetailData(): ZaiComicData<DataContent<DetailData>> {
        if (comicDetailCacheMap.contains(id)) return this
        comicDetailCacheMap[id] = this.data.data
        return this
    }

    override suspend fun isOffLine(): Boolean =
        try {
            !Jsoup.connect(HOST).get().text().contains("It Works")
        } catch (_: Exception) {
            true
        }

    override val id: Int
        get() = "ZaiComic".hashCode()

    override fun getBookInformation(id: Int): BookInformation? {
        val detailData =
            if (comicDetailCacheMap.contains(id))
                comicDetailCacheMap[id]
            else
                Jsoup
                    .connect(HOST +"/app/v1/comic/detail/$id?channel=android&timestamp=${(System.currentTimeMillis() / 1000)}")
                    .ignoreContentType(true)
                    .get()
                    .outputSettings(
                        Document.OutputSettings()
                        .prettyPrint(false)
                        .syntax(Document.OutputSettings.Syntax.xml)
                    )
                    .body()
                    .text()
                    .let {
                        gson.fromJson<ZaiComicData<DataContent<DetailData>>>(it, object : TypeToken<ZaiComicData<DataContent<DetailData>>>() {}.type)
                    }
                    .cacheDetailData()
                    .data
                    .data
        return detailData?.toBookInformation()
    }

    /*
    GET /app/v1/comic/sub/checkIsSub? HTTP/1.1
     */
    override fun getBookVolumes(id: Int): BookVolumes? {
        val detailData =
            if (comicDetailCacheMap.contains(id))
                comicDetailCacheMap[id]
            else
                Jsoup
                    .connect(HOST +"/app/v1/comic/detail/$id?channel=android&timestamp=${(System.currentTimeMillis() / 1000)}")
                    .ignoreContentType(true)
                    .get()
                    .outputSettings(
                        Document.OutputSettings()
                            .prettyPrint(false)
                            .syntax(Document.OutputSettings.Syntax.xml)
                    )
                    .body()
                    .text()
                    .let {
                        gson.fromJson<ZaiComicData<DataContent<DetailData>>>(it, object : TypeToken<ZaiComicData<DataContent<DetailData>>>() {}.type)
                    }
                    .cacheDetailData()
                    .data
                    .data
        return detailData?.toBookVolumes()
            ?.let {
                comicVolumesCacheMap[id] = it
                it
            }
    }

    /*
    GET /app/v1/comic/chapter/33322/158352?channel=android&timestamp=1727804198 HTTP/1.1
     */
    override fun getChapterContent(chapterId: Int, bookId: Int): ChapterContent? {
        val volumes =
            if (comicVolumesCacheMap.contains(bookId))
                comicVolumesCacheMap[bookId] ?: return null
            else
                getBookVolumes(bookId) ?: return null
        val chapterIds = mutableListOf<Int>()
            .apply {
                volumes.volumes.forEach { volume ->
                    volume.chapters.map { it.id }.let(::addAll)
                }
            }
        val chapterIdIndex = chapterIds.indexOfFirst(chapterId::equals)
        if (chapterIdIndex == -1) return null
        val lastChapterId =
            if (chapterIdIndex != 0) chapterIds[chapterIdIndex - 1]
            else -1
        val nextChapterId =
            if (chapterIdIndex != chapterIds.size - 1) chapterIds[chapterIdIndex + 1]
            else -1
        val chapterContent = Jsoup
            .connect(HOST +"/app/v1/comic/chapter/$bookId/$chapterId?channel=android&timestamp=${(System.currentTimeMillis() / 1000)}")
            .ignoreContentType(true)
            .get()
            .outputSettings(
                Document.OutputSettings()
                    .prettyPrint(false)
                    .syntax(Document.OutputSettings.Syntax.xml)
            )
            .body()
            .text()
            .let {
                gson.fromJson<ZaiComicData<DataContent<ComicChapterComic>>>(it, object : TypeToken<ZaiComicData<DataContent<ComicChapterComic>>>() {}.type)
            }
            .data
            .data
            .toChapterContent(lastChapterId, nextChapterId)
        return chapterContent
    }

    override suspend fun getExplorationPageMap(): Map<String, ExplorationPageDataSource> =
        mapOf()

    override val explorationPageTitleList: List<String> =
        listOf()

    override fun getExplorationExpandedPageDataSourceMap(): Map<String, ExplorationExpandedPageDataSource> =
        mapOf()

    /*
    GET /app/v1/search/index?keyword=%E5%85%B3%E4%BA%8E%E9%82%BB%E5%B1%85%E5%AE%B6%E5%A4%A9%E4%BD%BF%E5%A4%A7%E4%BA%BA%E6%8A%8A%E6%88%91%E5%85%BB%E6%88%90%E5%BA%9F%E4%BA%BA%E8%BF%99%E4%BB%B6%E4%BA%8B&page=3&size=20&channel=android&timestamp=1727804461 HTTP/1.1
    GET /app/v1/search/index?keyword=%E5%85%B3%E4%BA%8E%E9%82%BB%E5%B1%85%E5%AE%B6%E5%A4%A9%E4%BD%BF%E5%A4%A7%E4%BA%BA%E6%8A%8A%E6%88%91%E5%85%BB%E6%88%90%E5%BA%9F%E4%BA%BA%E8%BF%99%E4%BB%B6%E4%BA%8B&page=3&size=20&channel=android&timestamp=1727804461 HTTP/1.1
    GET /app/v1/search/index?keyword=%E9%82%BB%E5%B1%85&page=1&size=20&channel=android&timestamp=1727804387 HTTP/1.1
     */
    override fun search(searchType: String, keyword: String): Flow<List<BookInformation>> {
        val comicList = MutableStateFlow(mutableListOf<BookInformation>())
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            var page = 1
            while (true) {
                val ids = Jsoup
                    .connect(
                        HOST + "/app/v1/search/index?keyword=${
                            URLEncoder.encode(
                                keyword,
                                "utf-8"
                            )
                        }&page=$page&size=20&channel=android&timestamp=${(System.currentTimeMillis() / 1000)}"
                    )
                    .ignoreContentType(true)
                    .get()
                    .outputSettings(
                        Document.OutputSettings()
                            .prettyPrint(false)
                            .syntax(Document.OutputSettings.Syntax.xml)
                    )
                    .body()
                    .text()
                    .let {
                        gson.fromJson<ZaiComicData<ListDataContent<SearchItem>>>(
                            it,
                            object : TypeToken<ZaiComicData<ListDataContent<SearchItem>>>() {}.type
                        )
                    }
                    .data
                    .list
                    .let { searchItemList ->
                        if (searchItemList == null) {
                            comicList.update {
                                it.apply {
                                    BookInformation.empty()
                                }
                            }
                            return@launch
                        }
                        searchItemList
                    }
                    .map { it.id }
                ids.forEach { id ->
                    delay(1)
                    comicList.update {
                        it.apply {
                            getBookInformation(id)?.let(::add)
                        }
                    }
                }
                page++
            }
        }
        return comicList
    }

    override val searchTypeMap: Map<String, String> =
        mapOf(
            "按漫画名称搜索" to "name"
        )

    override val searchTipMap: Map<String, String> =
        mapOf(
            "name" to "请输入漫画名称"
        )

    override val searchTypeNameList: List<String> = listOf("按漫画名称搜索")

    override fun stopAllSearch() {
        searchJob?.cancel()
    }
}