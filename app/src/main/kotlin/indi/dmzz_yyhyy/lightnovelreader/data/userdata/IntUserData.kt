package indi.dmzz_yyhyy.lightnovelreader.data.userdata

import indi.dmzz_yyhyy.lightnovelreader.data.local.room.dao.UserDataDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IntUserData (
    override val path: String,
    private val userDataDao: UserDataDao
) : UserData<Int>(path) {
    override fun set(value: Int) {
        userDataDao.update(path, group, "Int", value.toString())
    }

    override fun get(): Int? {
        return userDataDao.get(path)?.toInt()
    }

    override fun getFlow(): Flow<Int?> {
        return userDataDao.getFlow(path).map { it?.toInt() }
    }
}