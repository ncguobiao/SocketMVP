package com.gb.sockt.center.injection.module

import com.gb.sockt.center.mvp.service.UserCenterService
import com.gb.sockt.center.mvp.service.imp.UserCenterServiceImpl
import dagger.Module
import dagger.Provides

/**
 * Created by guobiao on 2018/8/23.
 */
@Module
class UserCenterModule {

    @Provides
    fun provideService(service: UserCenterServiceImpl): UserCenterService {
        return service
    }
}