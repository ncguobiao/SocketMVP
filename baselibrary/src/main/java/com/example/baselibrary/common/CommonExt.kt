package com.example.baselibrary.common

import android.view.View
import com.example.baselibrary.base.BasePresenter
import com.example.baselibrary.base.IBaseView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable

/*
    扩展Observable执行
 */
fun <T> Observable<T>.execute(subscriber: BaseSubscriber<T>, lifecycleProvider: LifecycleProvider<*>) {
    this.compose(lifecycleProvider.bindToLifecycle())
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
fun preparReq(rootView: IBaseView?, presenter: BasePresenter<*>) {
    if (!presenter.checkNetWork()) {
        return
    }
    presenter.checkViewAttached()
    rootView?.apply {
        this.showLoading()
    }
}