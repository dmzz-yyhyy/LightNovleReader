package indi.dmzz_yyhyy.lightnovelreader.data.web.zaicomic.json

import com.google.gson.annotations.SerializedName

data class TagTypeItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("tagId")
    val tagId: Int
)