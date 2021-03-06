package com.gb.socket1;

/**
 * Created by liuguangli on 17/4/24.
 */

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestOkHttp3 {

    @Test
    public void testDB() {

    }

    /**
     * 测试 OkHttp Get 方法
     */
    @Test
    public void testGet() {

        TestKotlin t = new TestKotlin();
        t.testTime();

        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient();
        // 创建 Request 对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 OkHttp Post 方法
     */
    @Test
    public void testPost() {
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient();
        // 创建 Request 对象
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(mediaType, "{\"name\": \"dalimao\"}");
        RequestBody body = RequestBody.create(mediaType,
                "{\"openId\":\"oE1qXwpVx2IFSzYGwT8rHs-dP1ME\", \"nickname\":\"\\uF8FF just so so，吻猪\"}");
        Request request = new Request.Builder()
//                .url("http://httpbin.org/post")// 请求行
                .url("https://www.sensorte.com/UserService/User/wechatLogin")// 请求行
//                .url("http://test01.sensor668.com:5080/UserService/User/wechatLogin")// 请求行
                //.header(); // 请求头
                .post(body) // 请求体
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试拦截器
     */
    @Test
    public void testInterceptor() {
        //  定义拦截器
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                long start = System.currentTimeMillis();
                Request request = chain.request();
                Response response = chain.proceed(request);
                long end = System.currentTimeMillis();
                System.out.println("interceptor: cost time = " + (end - start));
                return response;
            }
        };
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        // 创建 Request 对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试缓存
     */
    @Test
    public void testCache() {
        // 创建缓存对象
        Cache cache = new Cache(new File("cache.cache"), 1024 * 1024);
        // 创建 OkHttpClient 对象
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        // 创建 Request 对象
        Request request = new Request.Builder()
                .url("http://httpbin.org/get?id=id")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        // OkHttpClient 执行 Request
        try {
            Response response = client.newCall(request).execute();
            Response responseCache = response.cacheResponse();
            Response responseNet = response.networkResponse();
            if (responseCache != null) {
                // 从缓存响应
                System.out.println("response from cache");
            }
            if (responseNet != null) {
                // 从缓存响应
                System.out.println("response from net");
            }

            System.out.println("response:" + response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScheduledExecutorService() {

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        TimerTask t1 = new TimerTask() {

            @Override
            public void run() {
//                Logger.d("testScheduledExecutorService");
                System.out.println("testScheduledExecutorService");
            }
        };
        pool.scheduleWithFixedDelay(t1, 0, 2, TimeUnit.SECONDS);

    }

}
