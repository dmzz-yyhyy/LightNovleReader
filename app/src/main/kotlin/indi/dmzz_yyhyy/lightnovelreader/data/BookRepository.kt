package indi.dmzz_yyhyy.lightnovelreader.data

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookInformation
import indi.dmzz_yyhyy.lightnovelreader.data.book.BookVolumes
import indi.dmzz_yyhyy.lightnovelreader.data.book.ChapterContent
import indi.dmzz_yyhyy.lightnovelreader.data.book.UserReadingData
import indi.dmzz_yyhyy.lightnovelreader.data.bookshelf.BookshelfRepository
import indi.dmzz_yyhyy.lightnovelreader.data.local.LocalBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.work.CacheBookWork
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Singleton
class BookRepository @Inject constructor(
    private val webBookDataSource: WebBookDataSource,
    private val localBookDataSource: LocalBookDataSource,
    private val bookshelfRepository: BookshelfRepository,
    private val workManager: WorkManager
) {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    suspend fun getBookInformation(id: Int): Flow<BookInformation> {
        val bookInformation: MutableStateFlow<BookInformation> =
            MutableStateFlow(localBookDataSource.getBookInformation(id) ?: BookInformation.empty())
        coroutineScope.launch {
            webBookDataSource.getBookInformation(id)?.let { information ->
                localBookDataSource.updateBookInformation(information)
                localBookDataSource.getBookInformation(id)?.let { newInfo ->
                    bookInformation.update { newInfo }
                    bookshelfRepository.getBookshelfBookMetadata(information.id)?.let { bookshelfBookMetadata ->
                        if (bookshelfBookMetadata.lastUpdate.isBefore(information.lastUpdated))
                            bookshelfBookMetadata.bookShelfIds.forEach {
                                bookshelfRepository.updateBookshelfBookMetadataLastUpdateTime(information.id, information.lastUpdated)
                                bookshelfRepository.addUpdatedBooksIntoBookShelf(it, id)
                            }
                    }
                }
            }
        }
        return bookInformation
    }

    suspend fun getBookVolumes(id: Int): Flow<BookVolumes> {
        val bookVolumes: MutableStateFlow<BookVolumes> =
            MutableStateFlow(localBookDataSource.getBookVolumes(id) ?: BookVolumes.empty())
        coroutineScope.launch {
            webBookDataSource.getBookVolumes(id)?.let { information ->
                localBookDataSource.updateBookVolumes(id, information)
                localBookDataSource.getBookVolumes(id)?.let { newBookVolumes ->
                    bookVolumes.update {
                        newBookVolumes
                    }
                }
            }
        }
        return bookVolumes
    }

    suspend fun getChapterContent(chapterId: Int, bookId: Int): Flow<ChapterContent> {
        val chapterContent: MutableStateFlow<ChapterContent> =
            MutableStateFlow(localBookDataSource.getChapterContent(chapterId) ?: ChapterContent.empty())
        coroutineScope.launch {
            webBookDataSource.getChapterContent(
                chapterId = chapterId,
                bookId = bookId
            )?.let { content ->
                localBookDataSource.updateChapterContent(content)
                localBookDataSource.getChapterContent(chapterId)?.let { newContent ->
                    chapterContent.update {
                        newContent
                    }
                }
            }
        }
        return chapterContent
    }

    fun getUserReadingData(bookId: Int): Flow<UserReadingData> =
        localBookDataSource.getUserReadingData(bookId).map { it }

    fun updateUserReadingData(id: Int, update: (UserReadingData) -> UserReadingData) {
        localBookDataSource.updateUserReadingData(id, update)
    }

    fun cacheBook(bookId: Int): OneTimeWorkRequest {
        val workRequest = OneTimeWorkRequestBuilder<CacheBookWork>()
            .setInputData(workDataOf(
                "bookId" to bookId
            ))
            .build()
        workManager.enqueueUniqueWork(
            bookId.toString(),
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        return workRequest
    }

    fun isCacheBookWorkFlow(workId: UUID) = workManager.getWorkInfoByIdFlow(workId)
}