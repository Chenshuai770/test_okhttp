package com.cs.test_okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * okhttp关于网络异步加载图片
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private Request request;
    private Button button2;
    private Button button;
    private OkHttpClient client;
    private final static int SUCCESS_STATUS = 1;
    private final static int FAIL_STATUS = 0;

    private OkMannager mannager;
    private final static String TAG = "TAG";
    private String image_path = "http://ww4.sinaimg.cn/large/610dc034jw1f8xz7ip2u5j20u011h78h.jpg";
    private String json_path = "http://www.tngou.net/api/cook/list";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_STATUS:
                    byte[] result = (byte[]) msg.obj;//读取流数据
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                    Bitmap bitmap1 = new CropSquareTrans().transform(bitmap);
                    image.setImageBitmap(bitmap1);

                    break;
                case FAIL_STATUS:

                    break;
            }
        }
    };
    private Button button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        mannager = OkMannager.getInstance();//单列实力额外
        client = new OkHttpClient();//实例化okhttp,
        request = new Request.Builder().get().url(image_path).build();//创建回调并传入url
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //异步 加载信息，装换为字节流
            case R.id.button1:
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = handler.obtainMessage();
                        if (response.isSuccessful()) {
                            message.what = SUCCESS_STATUS;
                            message.obj = response.body().bytes();
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(FAIL_STATUS);
                        }
                    }
                });

                break;

            case R.id.button2:
                mannager.asyncJsonStringByURL(json_path, new OkMannager.Funcl() {
                    @Override
                    public void onResponse(String result) {
                        Log.d(TAG, result);
                    }
                });

                break;
            case R.id.button3:
                //mannager.sendComplenForm();
                break;
        }
    }
}
