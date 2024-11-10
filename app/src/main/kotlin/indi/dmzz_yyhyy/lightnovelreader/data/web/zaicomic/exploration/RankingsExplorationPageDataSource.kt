package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.exploration

import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationBooksRow
import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationPage
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationPageDataSource
import kotlinx.coroutines.flow.MutableStateFlow

object RankingsExplorationPageDataSource : ExplorationPageDataSource {
    private var lock = false
    private val explorationBooksRows: MutableStateFlow<List<ExplorationBooksRow>> = MutableStateFlow(emptyList())
    private val explorationPage = ExplorationPage("排行", explorationBooksRows)

    override fun getExplorationPage(): ExplorationPage {
        if (lock) return explorationPage
        return explorationPage
    }
}