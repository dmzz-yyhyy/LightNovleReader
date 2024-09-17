package indi.dmzz_yyhyy.lightnovelreader.ui.home.exploration.expanded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import indi.dmzz_yyhyy.lightnovelreader.data.ExplorationRepository
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.web.exploration.ExplorationExpandedPageDataSource
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
class ExpandedPageViewModel @Inject constructor(
    private val explorationRepository: ExplorationRepository,
    private val bookshelfRepository: BookshelfRepository,
) : ViewModel() {
    private var expandedPageDataSource: ExplorationExpandedPageDataSource? = null
    private var explorationExpandedPageBookListCollectJob: Job? = null
    private var loadMoreJob: Job? = null
    private var lastExpandedPageDataSourceId: String = ""
    private val _uiState = MutableExpandedPageUiState()
    val uiState: ExpandedPageUiState = _uiState

    fun init(expandedPageDataSourceId: String) {
        if (expandedPageDataSourceId == lastExpandedPageDataSourceId) return
        lastExpandedPageDataSourceId = expandedPageDataSourceId
        loadMoreJob?.cancel()
        explorationExpandedPageBookListCollectJob?.cancel()
        expandedPageDataSource = explorationRepository.getExplorationExpandedPageDataSource(expandedPageDataSourceId)
        explorationExpandedPageBookListCollectJob = viewModelScope.launch(Dispatchers.IO) {
            expandedPageDataSource?.let { explorationExpandedPageDataSource ->
                explorationExpandedPageDataSource.refresh()
                _uiState.pageTitle = explorationExpandedPageDataSource.getTitle()
                _uiState.filters = explorationExpandedPageDataSource.getFilters().toMutableList()
                explorationExpandedPageDataSource.getResultFlow().collect {
                    _uiState.bookList = it.toMutableList()
                    if (it.isEmpty()) { explorationExpandedPageDataSource.loadMore() }
                }
            }
        }
        viewModelScope.launch {
            bookshelfRepository.getAllBookshelfBookIdsFlow().collect {
                _uiState.allBookshelfBookIds = it.toMutableList()
            }
        }
    }

    fun loadMore() {
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch(Dispatchers.IO) {
            if (expandedPageDataSource?.hasMore() == false) return@launch
            expandedPageDataSource?.loadMore()
        }
    }

    fun clear() {
        lastExpandedPageDataSourceId = ""
    }
}
