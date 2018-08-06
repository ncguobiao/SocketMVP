package com.gb.socket.wxapi;//package com.gb.sockt.wxapi;
//
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.widget.Toast;
//
//
//
//
//public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
//
//
//	private IWXAPI api;
//	private MyReceiver receiver;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
////		setContentView(R.layout.pay_result);
//		IntentFilter filter = new IntentFilter();
//		filter.addAction("com.trilink.bluetooth.dms_new.activity.SelectPayTypeActivity");
//		receiver = new MyReceiver();
//		registerReceiver(receiver,filter);
//
//		api = WXAPIFactory.createWXAPI(this, ConstantURL.APP_ID);
//		api.handleIntent(getIntent(), this);
//	}
//
//	class MyReceiver extends BroadcastReceiver{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//		}
//	}
//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//		setIntent(intent);
//		api.handleIntent(intent, this);
//	}
//
//	@Override
//	public void onReq(BaseReq req) {
//	}
//
//	@Override
//	public void onResp(BaseResp resp) {
//		/*
//		* 2、回调结果的处理，下面是官方的处理方式，直接给了一个dialog，很多人会摸不着头脑，
//		* 如果你不需要这个dialog，直接删除就好了，不需要把官方demo中的布局和资源都复制过来
//		* */
//
//		LogUtil.e("微信支付回调》》resp》" + resp.errCode);
//		/**
//		 * 0 支付成功
//		 -1 发生错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//		 -2 用户取消 发生场景：用户不支付了，点击取消，返回APP。
//		 */
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			if (resp.errCode == 0) {
//				Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//				Intent intent = new Intent();
//				intent.setAction("com.gb.socket.wxapi.WXPayEntryActivity");
//				sendBroadcast(intent);
//			} else if (resp.errCode == -1) {
//				Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//			} else if (resp.errCode == -2) {
//
//				Toast.makeText(WXPayEntryActivity.this, "取消支付", Toast.LENGTH_SHORT).show();
//			}
//			finish();
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		unregisterReceiver(receiver);
//	}
//}