package indi.dmzz_yyhyy.lightnovelreader.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import indi.dmzz_yyhyy.lightnovelreader.data.UserDataRepository
import indi.dmzz_yyhyy.lightnovelreader.data.userdata.UserDataPath
import indi.dmzz_yyhyy.lightnovelreader.data.web.WebBookDataSource
import indi.dmzz_yyhyy.lightnovelreader.data.web.wenku8.Wenku8Api
import indi.dmzz_yyhyy.lightnovelreader.zaicomic.ZaiComic
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  WebDataSourceModule {
    private val webDataSources = listOf(ZaiComic, Wenku8Api)
    @Singleton
    @Provides
    fun provideWebDataSource(userDataRepository: UserDataRepository): WebBookDataSource {
        val webDataSourcesId = userDataRepository.intUserData(UserDataPath.Settings.Data.WebDataSourceId.path).get()
        return webDataSources.find { it.id == webDataSourcesId } ?: Wenku8Api
    }
}