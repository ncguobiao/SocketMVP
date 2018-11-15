package com.gb.sockt.blutoothcontrol.data.repository

import com.example.baselibrary.common.BaseResp
import com.example.baselibrary.compose
import com.example.baselibrary.data.net.RetrofitFactory
import com.gb.sockt.blutoothcontrol.api.BluetoothControlApi
import com.gb.sockt.blutoothcontrol.data.domain.RequestCantUseReq
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
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


    fun openSuccess2Service(userId: String, deviceId: String, time: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(BluetoothControlApi::class.java)
                .openSuccess2Service(req = RequestCantUseReq(userId, deviceId, time)).compose()
    }

    /**
     * 加时成功通知服务器
     */
    fun addTimeSuccess2Service(userId: String, deviceId: String, time: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(BluetoothControlApi::class.java)
                .addTimeSuccess2Service(req = RequestCantUseReq(userId, deviceId, time)).compose()
    }

    /**
     * 请求是否可以加时操作
     */
    fun addTimeCanUse(userId: String, deviceId: String, time: String): Observable<BaseResp>{
        return RetrofitFactory.instance.create(BluetoothControlApi::class.java)
                .addTimeCanUse(req = RequestCantUseReq(userId, deviceId, time)).compose()

    }
}