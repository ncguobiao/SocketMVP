package com.example.baselibrary.widght;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baselibrary.R;


/**
 * @author vondear
 * @date 2016/7/19
 * 确认 取消 Dialog
 */
public class RxDialogSureCancel extends RxDialog {

    private ImageView mIvLogo;
    private TextView mTvContent;
    private TextView mTvSure;
    private TextView mTvCancel;
    private TextView mTvTitle;

    public RxDialogSureCancel(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public RxDialogSureCancel(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public RxDialogSureCancel(Context context) {
        super(context);
        initView();
    }

    public RxDialogSureCancel(Activity context) {
        super(context);
        initView();
    }

    public RxDialogSureCancel(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public RxDialogSureCancel build(){
        return this;
    }

    public ImageView getLogoView() {
        return mIvLogo;
    }

    public RxDialogSureCancel  setTitle(String title) {
        mTvTitle.setText(title);
        return this;
    }

    public TextView getTitleView() {
        return mTvTitle;
    }

    public RxDialogSureCancel setContent(String content) {
        this.mTvContent.setText(content);
        return this;
    }

    public TextView getContentView() {
        return mTvContent;
    }

    public RxDialogSureCancel setSure(String strSure) {
        this.mTvSure.setText(strSure);
        return this;
    }

    public TextView getSureView() {
        return mTvSure;
    }

    public RxDialogSureCancel setCancel(String strCancel) {
        this.mTvCancel.setText(strCancel);
        return this;
    }

    public TextView getCancelView() {
        return mTvCancel;
    }

    public RxDialogSureCancel setSureListener(View.OnClickListener sureListener) {
        mTvSure.setOnClickListener(sureListener);
        return this;
    }

    public RxDialogSureCancel setCancelListener(View.OnClickListener cancelListener) {
        mTvCancel.setOnClickListener(cancelListener);
        return this;
    }

    public RxDialogSureCancel setOnCancelable(boolean flag) {
        super.setCancelable(flag);
        return this;
    }

    public RxDialogSureCancel setOnCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        return this;
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sure_false, null);
        mIvLogo = (ImageView) dialogView.findViewById(R.id.iv_logo);
        mTvSure = (TextView) dialogView.findViewById(R.id.tv_sure);
        mTvCancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        mTvContent = (TextView) dialogView.findViewById(R.id.tv_content);
        mTvContent.setTextIsSelectable(true);
        mTvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        setContentView(dialogView);
    }
}
