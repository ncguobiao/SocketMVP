package com.gb.sockt.blutoothcontrol.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.sockt.blutoothcontrol.api.BluetoothControlApi
import com.gb.sockt.blutoothcontrol.data.domain.RequestCantUseReq
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/13.
 */
class BlueToothControlRepository @Inject constructor() {

    /**
     * 查询剩余时间
     */
    fun requestSurplusTime(userId: String, deviceId: String): Observable<BaseResp> {

        return RetrofitFactory.instance.create(BluetoothControlApi::class.java)
                .requestSurplusTime(req = RequestCantUseReq(userId, deviceId,"")).compose()
    }


    /**
     * 查询是否可用
     */
    fun requestCanUse(userId: String, deviceId: String, time: String): Observable<BaseResp> {

        return RetrofitFactory.instance.create(BluetoothControlApi::class.java)
                .requestCantUse(req = RequestCantUseReq(userId, deviceId, time)).compose()
    }
}