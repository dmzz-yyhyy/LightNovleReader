package indi.dmzz_yyhyy.lightnovelreader.zaicomic.exploration

import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationBooksRow
import indi.dmzz_yyhyy.lightnovelreader.data.exploration.ExplorationPage
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationPageDataSource
import kotlinx.coroutines.flow.MutableStateFlow

object RecommendExplorationPage: ExplorationPageDataSource {
    override fun getExplorationPage(): ExplorationPage {
        val rows = MutableStateFlow(mutableListOf<ExplorationBooksRow>())
        SC
        return ExplorationPage(
            title = "推荐",
            rows = rows
        )
    }
}