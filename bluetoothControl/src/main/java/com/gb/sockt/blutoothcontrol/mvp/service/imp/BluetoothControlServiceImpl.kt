package com.gb.sockt.blutoothcontrol.mvp.service.imp

import com.example.baselibrary.common.BaseResp
import com.gb.sockt.blutoothcontrol.data.repository.BlueToothControlRepository
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/13.
 */
class BluetoothControlServiceImpl @Inject constructor() : BluetoothContrlService {


    @Inject
    lateinit var repository: BlueToothControlRepository

    override fun requestSurplusTime(userId: String, deviceId: String): Observable<BaseResp> {

        return repository.requestSurplusTime(userId, deviceId)
    }


    override fun requestCanUse(userId: String, deviceId: String, time: String): Observable<BaseResp> {

        return repository.requestCanUse(userId, deviceId, time)
    }


    override fun openSuccess2Service(userId: String, deviceId: String, time: String): Observable<BaseResp>{
        return repository.openSuccess2Service(userId, deviceId, time)
    }

    /**
     * 加时成功通知服务器
     */
    override fun addTimeSuccess2Service(userId: String, deviceId: String, time: String): Observable<BaseResp>{
        return repository.addTimeSuccess2Service(userId, deviceId, time)
    }

    override fun addTimeCanUse(userId: String, deviceId: String, time: String): Observable<BaseResp>{
        return repository.addTimeCanUse(userId, deviceId, time)

    }
}