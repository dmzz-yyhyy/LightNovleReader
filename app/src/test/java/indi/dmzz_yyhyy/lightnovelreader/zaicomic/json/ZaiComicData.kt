package indi.dmzz_yyhyy.lightnovelreader.zaicomic.json

data class ZaiComicData<T>(
    val errno: Int,
    val errmsg: String,
    val data: T
)

data class DataContent<T>(
    val data: T
)

data class ListDataContent<T>(
    val list: List<T>?
)