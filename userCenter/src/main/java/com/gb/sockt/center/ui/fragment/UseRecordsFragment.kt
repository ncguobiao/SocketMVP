package com.gb.sockt.center.ui.fragment

import android.os.Bundle
import com.example.baselibrary.base.BaseMvpFragment
import com.example.baselibrary.showToast
import com.gb.sockt.center.R
import com.gb.sockt.center.injection.component.DaggerUserCenterComponent
import com.gb.sockt.center.injection.module.UserCenterModule
import com.gb.sockt.center.mvp.presenter.imp.RecordsPresenterImpl
import com.gb.sockt.center.mvp.view.RecordsView

/**
 * Created by guobiao on 2018/8/23.
 */
class UseRecordsFragment:BaseMvpFragment<RecordsPresenterImpl>(),RecordsView{

    companion object {
        fun newInstance(id: String): UseRecordsFragment {
            var args: Bundle = Bundle()
            var editFragment: UseRecordsFragment = newInstance()
            editFragment.arguments = args
            return editFragment
        }

        fun newInstance(): UseRecordsFragment {
            return UseRecordsFragment()
        }

    }

    override fun onError(error: String) {
        showToast(error)
    }

    override fun onDataIsNull() {
        showToast("获取数据失败")
    }

    override fun getLayoutId(): Int {
       return R.layout.fragment_use_records
    }

    override fun injectComponent() {
        DaggerUserCenterComponent.builder()
                .activityComponent(mActivityComponent)
                .userCenterModule(UserCenterModule())
                .build()
                .inject(this)

        mPresenter.attachView(this)
    }

    override fun lazyLoad() {
        mPresenter.getUseRecords(
                "com.sensor.device.service.impl.UseDeviceManagerServiceImpl.getUseCDZRecordsV2",
                getUserID(),
                curPage = "1"

        )
        mPresenter.getRechargeRecords(getUserID())
    }

    override fun initView() {
    }
}