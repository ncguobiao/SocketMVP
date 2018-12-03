package com.gb.socket1.accountcenter.injection.module

import com.gb.socket1.accountcenter.mvp.service.UserService
import com.gb.socket1.accountcenter.mvp.service.impl.UserServiceImpl
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