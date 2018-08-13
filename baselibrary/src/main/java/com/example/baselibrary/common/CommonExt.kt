package com.example.baselibrary.common

import android.view.View
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.base.IBaseView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/*
    扩展Observable执行
 */
fun <T> Observable<T>.execute(subscriber: BaseSubscriber<T>, lifecycleProvider: LifecycleProvider<*>) {
    this.compose(lifecycleProvider.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(subscriber)

}


/*
    扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * 网络请求前的准备工作
 */
fun preparReq(rootView: IBaseView?, presenter: BasePresenter<*>) :Boolean{
    presenter.checkViewAttached()
    if (presenter.checkNetWork()){
        rootView?.apply {
            this.showLoading()
        }
        return true
    }else{
        return false
    }

}