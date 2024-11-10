package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json

import com.google.gson.annotations.SerializedName

data class RecommendData (
    val title: String,
    val data: List<RecommendItem>
)

data class RecommendItem (
    @SerializedName("obj_id")
    val id: Int,
    val title: String,
    val cover: String,
    val type: Int
)