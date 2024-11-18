package indi.dmzz_yyhyy.lightnovelreader.data.json

import com.google.gson.annotations.SerializedName
import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.UserDataEntity

data class UserDataData(
    @SerializedName("path")
    val path: String,
    @SerializedName("group")
    val group: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("value")
    val value: String
)

fun UserDataEntity.toJsonData() =
    UserDataData(
        path = this.path,
        group = this.group,
        type = this.type,
        value = this.value
    )