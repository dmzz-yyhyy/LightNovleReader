package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json

import com.google.gson.annotations.SerializedName

data class ZaiComicData<T>(
    @SerializedName("errno")
    val errno: Int,
    @SerializedName("errmsg")
    val errmsg: String,
    @SerializedName("data")
    val data: T
)

data class DataContent<T>(
    @SerializedName("data")
    val data: T
)

data class ListDataContent<T>(
    @SerializedName("list")
    val list: List<T>?
)