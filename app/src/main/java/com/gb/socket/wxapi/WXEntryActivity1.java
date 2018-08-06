package com.gb.socket.wxapi;//package com.gb.sockt.wxapi;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.gb.socket.bean.WXLoginBean;
//import com.gb.socket.constant.ConstantURL;
//import com.google.gson.Gson;
//import com.tencent.mm.opensdk.modelbase.BaseReq;
//import com.tencent.mm.opensdk.modelbase.BaseResp;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//
///**
// * 授权后，微信客户端调用的页面（ Android:exported="true"）
// */
//public class WXEntryActivity1 extends Activity implements IWXAPIEventHandler {
//
//    private IWXAPI api;
//    private BaseResp resp = null;
//    // 获取第一步的code后，请求以下链接获取access_token
//    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
//    // 获取用户个人信息
//    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
//    private String WX_APP_SECRET = "cc8122eaa3605e82f32fb3d61d3bee13";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        api = WXAPIFactory.createWXAPI(this, ConstantURL.APP_ID, false);
//
//        api.handleIntent(getIntent(), this);
//    }
//
//    // 微信发送请求到第三方应用时，会回调到该方法
//    @Override
//    public void onReq(BaseReq req) {
//        finish();
//    }
//
//    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
//    @Override
//    public void onResp(BaseResp resp) {
//        String result = "";
//        if (resp != null) {
//            this.resp = resp;
//        }
//        switch (resp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = "发送成功";
//                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                String code = ((SendAuth.Resp) resp).code;
//
//            /*
//             * 将你前面得到的AppID、AppSecret、code，拼接成URL 获取access_token等等的信息(微信)
//             */
//                final String get_access_token = getCodeRequest(code);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                       try {
//                           URL url = new URL(get_access_token);
//                           HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//                           //设置连接属性
//                           httpConn.setDoOutput(true);// 使用 URL 连接进行输出
//                           httpConn.setDoInput(true);// 使用 URL 连接进行输入
//                           httpConn.setUseCaches(false);// 忽略缓存
//                           httpConn.setRequestMethod("GET");// 设置URL请求方法
//                           // 设置请求属性
//                           // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
//                           httpConn.setRequestProperty("Content-Type", "application/octet-stream");
//                           httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//                           httpConn.setRequestProperty("Charset", "UTF-8");
//                           InputStream is = httpConn.getInputStream();   //获取输入流，此时才真正建立链接
//                           InputStreamReader isr = new InputStreamReader(is);
//                           BufferedReader bufferReader = new BufferedReader(isr);
//                           String resultData = "";
//                           String inputLine  = "";
//                           while((inputLine = bufferReader.readLine()) != null){
//                                               resultData += inputLine + "\n";
//                           }
//                           bufferReader.close();
//                           WXLoginBean wxLoginBean = new Gson().fromJson(resultData, WXLoginBean.class);
//                           String access_token = wxLoginBean.getAccess_token();//接口调用凭证
//                           String Refresh_token = wxLoginBean.getRefresh_token();//用户刷新access_token
//                           int expires_in = wxLoginBean.getExpires_in();//access_token接口调用凭证超时时间，单位（秒）
//                           String openid = wxLoginBean.getOpenid();//授权用户唯一标识
//                           String scope = wxLoginBean.getScope();//用户授权的作用域，使用逗号（,）分隔
//                           String unionid = wxLoginBean.getUnionid();//当且仅当该移动应用已获得该用户的userinfo授权时，才会出现该字段
//
//                           String get_user_info_url = getUserInfo(
//                                   access_token, openid);
//                           getUserInfo(get_user_info_url);
//
//                       }catch (Exception e){
//                           e.printStackTrace();
//                       }
//                    }
//                }).start();
//
//                finish();
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = "发送取消";
//                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                finish();
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = "发送被拒绝";
//                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                finish();
//                break;
//            case BaseResp.ErrCode.ERR_BAN:
//                result = "什么几把毛错误啊";
//                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                finish();
//                break;
//            default:
//                result = "发送返回";
//                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                finish();
//                break;
//        }
//    }
//
//    /**
//     * 通过拼接的用户信息url获取用户信息
//     *
//     * @param user_info_url
//     */
//    private void getUserInfo(String user_info_url) {
//        try {
//            URL url = new URL(user_info_url);
//            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//            //设置连接属性
//            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
//            httpConn.setDoInput(true);// 使用 URL 连接进行输入
//            httpConn.setUseCaches(false);// 忽略缓存
//            httpConn.setRequestMethod("GET");// 设置URL请求方法
//            // 设置请求属性
//            // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
//            httpConn.setRequestProperty("Content-Type", "application/octet-stream");
//            httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//            httpConn.setRequestProperty("Charset", "UTF-8");
//            InputStream is = httpConn.getInputStream();   //获取输入流，此时才真正建立链接
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader bufferReader = new BufferedReader(isr);
//            String resultData = "";
//            String inputLine  = "";
//            while((inputLine = bufferReader.readLine()) != null){
//                resultData += inputLine + "\n";
//            }
//            bufferReader.close();
////            获取用户登陆信息
////            WXUserBean wxUserBean = new Gson().fromJson(resultData, WXUserBean.class);
////            String nickname = wxUserBean.getNickname();
////            String headimgurl = wxUserBean.getHeadimgurl();
////            String openid = wxUserBean.getOpenid();
////            Toast.makeText(WXEntryActivity1.this, "nickname="+nickname+" "+"openid="+"openid", Toast.LENGTH_SHORT).show();
////            LogUtil.e( "nickname="+nickname+" "+"openid="+openid);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//        finish();
//    }
//
//    /**
//     * 获取access_token的URL（微信）
//     *
//     * @param code 授权时，微信回调给的
//     * @return URL
//     */
//    private String getCodeRequest(String code) {
//        String result = null;
//        GetCodeRequest = GetCodeRequest.replace("APPID",
//                urlEnodeUTF8(ConstantURL.APP_ID));
//        GetCodeRequest = GetCodeRequest.replace("SECRET",
//                urlEnodeUTF8(WX_APP_SECRET));
//        GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
//        result = GetCodeRequest;
//        return result;
//    }
//
//    /**
//     * 获取用户个人信息的URL（微信）
//     *
//     * @param access_token 获取access_token时给的
//     * @param openid       获取access_token时给的
//     * @return URL
//     */
//    private String getUserInfo(String access_token, String openid) {
//        String result = null;
//        GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN",
//                urlEnodeUTF8(access_token));
//        GetUserInfo = GetUserInfo.replace("OPENID", urlEnodeUTF8(openid));
//        result = GetUserInfo;
//        return result;
//    }
//
//    private String urlEnodeUTF8(String str) {
//        String result = str;
//        try {
//            result = URLEncoder.encode(str, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//}
