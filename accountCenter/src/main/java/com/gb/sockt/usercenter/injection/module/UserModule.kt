package com.gb.sockt.usercenter.injection.module

import com.gb.sockt.usercenter.mvp.service.UserService
import com.gb.sockt.usercenter.mvp.service.impl.UserServiceImpl
import dagger.Module
import dagger.Provides

/**
 * Created by guobiao on 2018/8/5.
 */
@Module
class UserModule {

    @Provides
    fun provideService(service: UserServiceImpl): UserService {
        return service
    }
}