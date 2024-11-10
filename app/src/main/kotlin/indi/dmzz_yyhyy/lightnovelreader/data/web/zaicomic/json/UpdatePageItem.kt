package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json

import com.google.gson.annotations.SerializedName

data class UpdatePageItem(
    @SerializedName("comic_id")
    val id: Int,
    val title: String,
    val cover: String,
)