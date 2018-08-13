package com.gb.sockt.blutoothcontrol.mvp.service

import com.example.baselibrary.common.BaseResp
import io.reactivex.Observable

/**
 * Created by guobiao on 2018/8/13.
 */
interface BluetoothContrlService {

   fun requestSurplusTime(userId: String, deviceId: String):Observable<BaseResp>


   fun requestCanUse(userId: String, deviceId: String,time:String):Observable<BaseResp>
}