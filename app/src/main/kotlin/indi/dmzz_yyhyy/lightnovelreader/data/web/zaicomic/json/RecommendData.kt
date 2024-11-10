package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json

import com.google.gson.annotations.SerializedName

data class RecommendData (
    @SerializedName("title")
    val title: String,
    @SerializedName("data")
    val data: List<RecommendItem>
)

data class RecommendItem (
    @SerializedName("obj_id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("type")
    val type: Int
)