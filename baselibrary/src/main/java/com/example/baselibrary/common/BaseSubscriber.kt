package com.example.baselibrary.common

import com.example.baselibrary.base.IBaseView
import com.example.baselibrary.common.Constant.SERVICE_ERROR
import com.kotlin.base.rx.DataNullException
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/*
    Rx订阅者默认实现
 */
open class BaseSubscriber<T>(val baseView: IBaseView) : Observer<T> {

    override fun onSubscribe(p0: Disposable) {
    }


    override fun onComplete() {
        baseView.dismissLoading()
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable) {
        baseView.dismissLoading()
        when (e) {
            is BaseException -> baseView.onError(e.returnMsg)
            is DataNullException -> baseView.onDataIsNull()
            else -> baseView.onError(SERVICE_ERROR+e.toString())
        }

    }
}