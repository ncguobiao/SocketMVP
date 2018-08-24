package com.gb.sockt.center.mvp.service.imp

import com.example.baselibrary.common.BaseResp
import com.gb.sockt.center.data.repository.UseCenterRepository
import com.gb.sockt.center.mvp.service.UserCenterService
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by guobiao on 2018/8/23.
 */
class UserCenterServiceImpl @Inject constructor() : UserCenterService {

    @Inject
    lateinit var repository: UseCenterRepository

    override fun getUseRecords(className: String, userId: String, curPage: String): Observable<BaseResp> {
        return repository.getUseRecords(className, userId, curPage)

    }

    override fun getRechargeRecords(userId: String): Observable<BaseResp> {
        return repository.getRechargeRecords(userId)
    }
}