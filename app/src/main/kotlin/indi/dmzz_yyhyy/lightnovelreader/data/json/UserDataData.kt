package indi.dmzz_yyhyy.lightnovelreader.data.json

import indi.dmzz_yyhyy.lightnovelreader.data.local.room.entity.UserDataEntity

data class UserDataData(
    val path: String,
    val group: String,
    val type: String,
    val value: String
)

fun UserDataEntity.toJsonData() =
    UserDataData(
        path = this.path,
        group = this.group,
        type = this.type,
        value = this.value
    )