package com.cs.test_okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chenshuai on 2016/10/21.
 * 封装工具栏
 */

public class OkMannager {
    private OkHttpClient client;
    private Handler handler;
    private volatile static OkMannager mannager;//多个线程同时访问的时候学习需要用到volatile
    private final String TAG = OkMannager.class.getSimpleName();
    //提交json字符串
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串
    private static final MediaType MEDIA_TYPE_MARRKDOOM = MediaType.parse("text/x-markdown;charset=utf-8");

    private OkMannager() {
        client = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }

    //采用单列模式来运行对象
    public static OkMannager getInstance() {
        OkMannager instance = null;
        if (mannager == null) {
            synchronized (OkMannager.class) {//进行了同步
                instance = new OkMannager();
                mannager = instance;
            }
        }
        return instance;
    }
    //同步请求

    /**
     * 同步请求在安卓开发中不常用，会阻塞UI线程
     *
     * @param url
     * @return
     */
    public String  synGetByURL(String url) {
        //构建一个request请求；
        Request request = new Request.Builder().url(url).build();//请求体
        Response response = null;//请求结果
        if (response == null) {
            try {
                response = client.newCall(request).execute();//execute是同步数据
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (Exception e) {

            }
        }
        return null;

    }

    /**
     * 异步请求json,返回时String
     * @param url
     * @param callBack
     */
    public void asyncJsonStringByURL(String  url,final Funcl callBack){
        final Request request = new Request.Builder().url(url).build();//请求体
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonStringMethod(response.body().string(),callBack);

                }
            }
        });
    }
/**
 * 封装为异步请求的jsonobject对象
 *
 */
    public void asyncJsonObjectByURL(String url, final Func4 callBack) {
        final Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessJsonObjectMethod(response.body().string(),callBack);

                }
            }
        });
    }

    /**
     * 返回的是字节数组
     * @param url
     * @param callBack
     */
    public void asyncGetByteByteByUrl(String url,final Func2 callBack){
        final Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSuccessByteMethod(response.body().bytes(),callBack);

                }
            }
        });

    }

    /**
     * image的一异步请求
     * 结果是Bitmap类型的
     * @param url
     * @param callBack
     */
    public void asyncDownLoadImageByURL(String url,final Func3 callBack){
        final Request request=new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                   byte[] data=response.body().bytes();
                    Bitmap bitmap1 = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Bitmap bitmap = new CropSquareTrans().transform(bitmap1);
                    callBack.onResponse(bitmap);

                }
            }
        });
    }

    /**
     * 模拟表单操作
     * @param url
     * @param params
     * @param callBack
     */
    public void sendComplenForm(String url, Map<String ,String > params, final Func4 callBack){
        FormBody.Builder form=new FormBody.Builder();//表单对象
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String,String> entry: params.entrySet()) {
                form.add(entry.getKey(),entry.getValue());
            }
            RequestBody requestBody=form.build();
            final Request request=new Request.Builder().url(url).post(requestBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response != null&& response.isSuccessful()) {
                        onSuccessJsonObjectMethod(response.body().string(),callBack);
                    }
                }
            });
        }
    }


    /**
     *请求返回的数据是json
     * @param jsonValue
     * @param callBack
     */
    private void onSuccessJsonStringMethod(final String jsonValue,final Funcl callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack!=null){
                    try {
                        callBack.onResponse(jsonValue);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 返回响应的结果是json对象
     * @param jsonValue
     * @param callBack
     */
    private void onSuccessJsonObjectMethod(final String  jsonValue,final Func4 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack!=null){
                    try {
                        callBack.onResponse(new JSONObject(jsonValue));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void onSuccessByteMethod(final byte[] bytes,final Func2 callBack) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack!=null){
                    try {
                        callBack.onResponse(bytes);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    interface Funcl {
        void onResponse(String result);
    }

    interface Func2 {
        void onResponse(byte[] bytes);
    }

    interface Func3 {
        void onResponse(Bitmap bitmap);
    }

    interface Func4 {
        void onResponse(JSONObject json);
    }
}
