package com.gb.sockt.blutoothcontrol.mvp.service.imp

import com.example.baselibrary.common.BaseResp
import com.gb.sockt.blutoothcontrol.ble.BlueToothControl
import com.gb.sockt.blutoothcontrol.data.repository.BlueToothControlRepository
import com.gb.sockt.blutoothcontrol.mvp.service.BluetoothContrlService
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/13.
 */
class BluetoothContrlServiceImpl @Inject constructor() : BluetoothContrlService {


    @Inject
    lateinit var repository: BlueToothControlRepository

    override fun requestSurplusTime(userId: String, deviceId: String): Observable<BaseResp> {

        return repository.requestSurplusTime(userId, deviceId)
    }


    override fun requestCanUse(userId: String, deviceId: String, time: String): Observable<BaseResp> {

        return repository.requestCanUse(userId, deviceId, time)
    }
}