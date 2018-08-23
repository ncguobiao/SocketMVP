package com.gb.socket

/**
 * Created by liuguangli on 17/4/24.
 */
import com.example.baselibrary.dao.GreenDaoHelper
import com.example.baselibrary.dao.model.DeviceUseRecords
import com.example.baselibrary.utils.DateUtils
import com.orhanobut.logger.Logger
import org.junit.Test


class TestKotlin {
    /**
     * 测试 OkHttp Get 方法
     */
    @Test
    fun testGet() {
        val a: Byte = -12
        val i = a.toInt() and 0XFF
    }

    @Test
    fun testTime() {
        val startTime = DateUtils.getTodayDateTime()
        val timeStamp = DateUtils.getTimeStamp(startTime)
        val hour = 2?.toLong()
        hour?.apply {
            this * 3600 * 1000 + timeStamp
        }
        Logger.d(hour)
    }

//    fun testDB() {
//        val dao = GreenDaoHelper.getInstance().daoSession.deviceUseRecordsDao
//        val deviceUseRecord = DeviceUseRecords(null, getUserID(), "222", "222", "11", "44")
//
//        dao.deleteAll()
//        val loadAll = dao.loadAll()
//        Logger.d("loadAll:\${loadAll}")
//    }


}
