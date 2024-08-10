package indi.dmzz_yyhyy.lightnovelreader.data.web

import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookVolumes
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationExpandedPageDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationPageDataSource
import kotlinx.coroutines.flow.Flow

interface WebBookDataSource {
    /**
     * 获取当前软件整体是否处于离线状态的数据流
     * 此数据流应当为热数据流, 并且不断对状态进行更新
     * 该函数应当保证主线程安全
     *
     *@return 是否处于离线状态的数据流
     */
    suspend fun getIsOffLineFlow(): Flow<Boolean>

    /**
     * 此函数无需保证主线程安全性, 为阻塞函数, 获取到数据前应当保持阻塞
     * 此函数应当自行实现断线重连等逻辑
     *
     * @param id 书本id
     * @return 经过格式化后的书本元数据, 如未找到改书则返回null
     */
    suspend fun getBookInformation(id: Int): BookInformation?

    /**
     * 此函数无需保证主线程安全性, 为阻塞函数, 获取到数据前应当保持阻塞
     * 此函数应当自行实现断线重连等逻辑
     *
     * @param id 书本id
     * @return 经过格式化后的书本章节目录数据, 如未找到改书则返回null
     */
    suspend fun getBookVolumes(id: Int): BookVolumes?

    /**
     * 此函数无需保证主线程安全性, 为阻塞函数, 获取到数据前应当保持阻塞
     * 此函数应当自行实现断线重连等逻辑
     *
     * @param chapterId 章节id
     * @param bookId 章节所属书本id
     * @return 经过格式化后的书本章节类容录数据, 如未找到改书则返回null
     */
    suspend fun getChapterContent(chapterId: Int, bookId: Int): ChapterContent?

    /**
     * 获取探索页面的标题和页面数据源的对应表
     * 此函数应当保证主线程安全
     *
     * @return 探索页面的标题和页面数据源的对应表
     */
    suspend fun getExplorationPageMap(): Map<String, ExplorationPageDataSource>

    /**
     * 获取所有探索页页面的标题
     * 此函数应当保证主线程安全
     *
     * @return 有序的所有探索页页面的标题列表
     */
    suspend fun getExplorationPageTitleList(): List<String>

    /**
     * 获取各个探索页横栏的展开页的id与展开页数据源的对应表
     * 此函数应当保证主线程安全
     *
     * @return 各个探索页横栏的展开页的id与展开页数据源的对应表
     */
    fun getExplorationExpandedPageDataSourceMap(): Map<String, ExplorationExpandedPageDataSource>

    /**
     * 执行搜索任务
     *
     * 应当返回搜索结果的热数据流
     * 并且以空书本元数据[BookInformation.empty]作为列表结尾时表示搜索结束
     * 此函数本身应当保证主线程安全
     *
     * @param searchType 搜索类别
     * @param keyword 搜索关键词
     * @return 搜索结果的数据流
     */
    fun search(searchType: String, keyword: String): Flow<List<BookInformation>>

    /**
     * 获取搜索类型名称和id的对应表
     * 此函数应当保证主线程安全
     *
     * @return 以搜索类型名称为键, 以id为值的Map
     */
    fun getSearchTypeMap(): Map<String, String>

    /**
     * 获取搜索类型id和搜索栏提示的对应表
     * 此函数应当保证主线程安全
     *
     * @return 以搜索类型id为键, 以搜索栏提示为值的Map
     */
    fun getSearchTipMap(): Map<String, String>

    /**
     * 获取搜索类型名称的有序列表
     * 此函数应当保证主线程安全
     *
     * @return 搜索类型名称的有序列表
     */
    fun getSearchTypeNameList(): List<String>

    /**
     * 停止当前所执行的所有搜索任务
     * 此函数应当保证主线程安全
     *
     */
    fun stopAllSearch()
}