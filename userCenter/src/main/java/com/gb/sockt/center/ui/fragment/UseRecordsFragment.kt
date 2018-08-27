package com.gb.sockt.center.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.example.baselibrary.base.BaseMvpFragment
import com.example.baselibrary.data.net.execption.ErrorStatus
import com.example.baselibrary.recycleView.EndlessRecyclerOnScrollListener
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
import com.gb.sockt.center.ui.adapter.UseRecordsAdapter
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView
import kotlinx.android.synthetic.main.fragment_use_records.*

/**
 * Created by guobiao on 2018/8/23.
 */
class UseRecordsFragment : BaseMvpFragment<RecordsPresenterImpl>(), RecordsView {
    override fun deleteRechargeRecordOnSuccess() {


    }


    private var isRefresh = false
    private var loadingMore = false
    private var mMaterialHeader: MaterialHeader? = null//头布局
    private var mMaterialFooter: ClassicsFooter? = null//脚布局
    private val mList = ArrayList<UseRecordBean.UseCDZRecordsListBean>()

    private var curPage: Int = 1
    private  var totalPages:Int = 1
    private var adapterPosition:Int = 0

    private val mAdapter by lazy {
        UseRecordsAdapter(activity, mList, R.layout.item_use_record)
    }
    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

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

    override fun showLoading() {
        if (!isRefresh)
            isRefresh = false
        mLayoutStatusView?.showLoading()

    }

    /**
     * 隐藏 Loading
     */
    override fun dismissLoading() {
        mLayoutStatusView?.showContent()
        mRefreshLayout.finishRefresh()
    }

    override fun onError(error: String) {
        showToast(error)
    }

    override fun showError(msg: String, errorCode: Int) {
        if (errorCode == ErrorStatus.NETWORK_ERROR) {
            mLayoutStatusView?.showNoNetwork()
        } else {
            mLayoutStatusView?.showError()
        }
    }

    override fun onDataIsNull() {
        showToast("获取数据失败")
    }

    override fun showRechargeRecords(dataList: List<RechargeRecordsBean>) {


    }

    override fun showUseRecords(dataBean: UseRecordBean) {
        loadingMore =false
        totalPages = dataBean.pageVO.totalPages
        val useCDZRecordsList = dataBean.useCDZRecordsList
        mAdapter.addData(useCDZRecordsList as ArrayList<UseRecordBean.UseCDZRecordsListBean>)

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
        getRecords(curPage = curPage)

    }

    private fun getRecords(curPage: Int) {
        mPresenter.getUseRecords(
                "com.sensor.device.service.impl.UseDeviceManagerServiceImpl.getUseCDZRecordsV2",
                getUserID(),
                curPage = curPage.toString()

        )
    }

    override fun initView() {
        mRecyclerView?.layoutManager = linearLayoutManager
        mRecyclerView?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        mRecyclerView?.itemAnimator = DefaultItemAnimator()

        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            mAdapter.clearData()
            getRecords(curPage)
        }

        if (activity is CommRecordsActivity){
            // 设置监听器。
            mRecyclerView.setSwipeMenuCreator((activity as CommRecordsActivity).swipeMenuCreator)
        }

        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener)
        mRecyclerView?.adapter = mAdapter

        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        mMaterialFooter = mRefreshLayout.refreshFooter as ClassicsFooter?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置 Footer 为 球脉冲
        mMaterialFooter?.spinnerStyle = SpinnerStyle.Scale
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.color_light_black, R.color.color_title_bg)
        mRecyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (!loadingMore) {
                    loadingMore = true
                    curPage += 1
                    if (curPage > totalPages) {
                        showToast("没有更多数据啦")
                        return
                    }
                    getRecords(curPage = curPage)
                }
            }

        })
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
//
//                val deviceid = mDataList[adapterPosition].id
//                mPresenter.deleteUserRecord("CDZ", deviceid, getUserID())

                //                ToastUtil.showSingleToast("list第" + adapterPosition + "; 右侧菜单第" + menuPosition);
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Logger.d("list第$adapterPosition; 左侧菜单第$menuPosition")
            }
        }
    }
}