package com.gb.sockt.center.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.example.baselibrary.base.BaseMvpFragment
import com.example.baselibrary.data.net.execption.ErrorStatus
import com.example.baselibrary.showToast
import com.gb.sockt.center.R
import com.gb.sockt.center.data.domain.RechargeRecordsBean
import com.gb.sockt.center.data.domain.UseRecordBean
import com.gb.sockt.center.injection.component.DaggerUserCenterComponent
import com.gb.sockt.center.injection.module.UserCenterModule
import com.gb.sockt.center.mvp.presenter.imp.RecordsPresenterImpl
import com.gb.sockt.center.mvp.view.RecordsView
import com.gb.sockt.center.ui.activity.CommRecordsActivity
import com.gb.sockt.center.ui.adapter.RechargeRecordsAdapter
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.yanzhenjie.recyclerview.swipe.*
import kotlinx.android.synthetic.main.fragment_use_records.*

/**
 * Created by guobiao on 2018/8/23.
 */
class RechargeRecordsFragment : BaseMvpFragment<RecordsPresenterImpl>(), RecordsView {

    private var mTitle: String? = null
    private var deviceName: String? = null

    private val mDataList = ArrayList<RechargeRecordsBean>()
    private var mMaterialHeader: MaterialHeader? = null//头布局
    private var mMaterialFooter: ClassicsFooter? = null//脚布局
    private var isRefresh = false

    private var adapterPosition: Int = 0

    private val mAdapter by lazy {
        RechargeRecordsAdapter(activity, mDataList, R.layout.item_payhistory)
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    companion object {
        fun getInstance(title: String?, deviceName: String?): RechargeRecordsFragment {
            val fragment = RechargeRecordsFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            fragment.deviceName = deviceName
            return fragment
        }

        private var swipeMenuCreator: SwipeMenuCreator?=null

        fun newInstance(): RechargeRecordsFragment {

            return RechargeRecordsFragment()
        }
    }


    /**
     * 显示 Loading （下拉刷新的时候不需要显示 Loading）
     */
    override fun showLoading() {
        if (!isRefresh)
            isRefresh = false
        mLayoutStatusView?.showLoading()
    }

    /**
     * 隐藏 Loading
     */
    override fun dismissLoading() {
        super.dismissLoading()
        mLayoutStatusView?.showContent()
        mRefreshLayout.finishRefresh()
    }

    override fun deleteUseRecordOnSuccess() {}

    override fun showUseRecords(dataBean: UseRecordBean) {}

    override fun showError(msg: String, errorCode: Int) {
        if (errorCode == ErrorStatus.NETWORK_ERROR) {
            mLayoutStatusView?.showNoNetwork()
        } else {
            mLayoutStatusView?.showError()
        }
    }


    override fun deleteRechargeRecordOnSuccess() {
        mDataList?.removeAt(adapterPosition)
        mAdapter?.notifyItemRemoved(adapterPosition)
        showToast("删除成功")
    }

    override fun onError(error: String) {
        showToast(error)
    }

    override fun onDataIsNull() {
        showToast("无法查询到相关数据")
    }


    override fun showRechargeRecords(dataList: List<RechargeRecordsBean>) {
        mAdapter.addData(dataList as ArrayList<RechargeRecordsBean>)
    }

    override fun getLayoutId(): Int = R.layout.fragment_use_records

    override fun injectComponent() {
        DaggerUserCenterComponent.builder()
                .activityComponent(mActivityComponent)
                .userCenterModule(UserCenterModule())
                .build()
                .inject(this)

    }

    /**
     * 请求数据
     */
    override fun lazyLoad() {
        mPresenter.getRechargeRecords(getUserID())
    }


    override fun initView() {
        mPresenter.attachView(this)

        //内容跟随偏移
            mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            mAdapter.clearData()
            mPresenter.getRechargeRecords(getUserID())
        }

        //设置mRecyclerView
        mRecyclerView.addItemDecoration(DividerItemDecoration(
                activity, DividerItemDecoration.HORIZONTAL))

        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
//        if (activity is CommRecordsActivity){
//            // 设置监听器。
//            mRecyclerView.setSwipeMenuCreator(swipeMenuCreator)
//        }
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator)
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener)
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.adapter = mAdapter

        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        mMaterialFooter = mRefreshLayout.refreshFooter as ClassicsFooter?
        //打开下拉刷新区域块背景:设置 Header 为 Material风格
        mMaterialHeader?.setShowBezierWave(true)
        //设置 Footer 为 球脉冲
        mMaterialFooter?.spinnerStyle = SpinnerStyle.Scale
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_light_black, R.color.color_title_bg)




        mLayoutStatusView = multipleStatusView

    }

    private val mMenuItemClickListener = object : SwipeMenuItemClickListener {
        override fun onItemClick(menuBridge: SwipeMenuBridge) {
            menuBridge.closeMenu()
            val direction = menuBridge.direction // 左侧还是右侧菜单。
            // RecyclerView的Item的position。
            adapterPosition = menuBridge.adapterPosition
            val menuPosition = menuBridge.position // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                //                mRecyclerView.setItemViewSwipeEnabled(true);

                val deviceid = mDataList[adapterPosition].id
                mPresenter.deletePayMent("CDZ", deviceid, getUserID())

                //                ToastUtil.showSingleToast("list第" + adapterPosition + "; 右侧菜单第" + menuPosition);
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Logger.d("list第$adapterPosition; 左侧菜单第$menuPosition")
            }
        }
    }


}