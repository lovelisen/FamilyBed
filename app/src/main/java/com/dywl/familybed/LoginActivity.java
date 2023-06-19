package com.dywl.familybed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dywl.familybed.model.FamilyBedModelBean;
import com.dywl.familybed.utils.JSONHelper;
import com.dywl.familybed.utils.MultipleResult;
import com.dywl.familybed.utils.WebTool;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String result;
    private TextView tv_result;
    private EditText editZhuYuanHao;
    private EditText editTextPhone;
    private String login_tel;
    private String hospCode;
    private ArrayList<MultipleResult> result_list;
    private String url_post="http://open.dywyhs.com/project/familybed/wx/wx_AjaxInfo.php";
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case -1:
//                    tv_result.setText("登录失败，请重试。");
                    Toast.makeText(getApplicationContext(), "失败，请重试。", Toast.LENGTH_LONG).show();
                    break;
                case 0:
//                    tv_result.setText("登录成功，跳转。");
                    Toast.makeText(getApplicationContext(), "成功，跳转。", Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 设置为横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        // 设置为全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        tv_result = findViewById(R.id.resultMessage);
        editZhuYuanHao = (EditText) findViewById(R.id.editZhuYuanHao);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        //默认焦点设置在用户名上
        editZhuYuanHao.requestFocus();

    }

    public void bClick(View v) {
        if (v.getTag().toString().equals("tagLogin")) {
            hospCode = editZhuYuanHao.getText().toString().trim();
            login_tel = editTextPhone.getText().toString().trim();
//            Toast.makeText(getApplicationContext(), "登录事件", Toast.LENGTH_LONG).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //在新线程中获取数据，通过handler判断是否获取到数据
                        //n为向PHP传输的数据
                        String n = "本地测试数据";
                        result = WebTool.singleData("http://open.dywyhs.com/project/familybed/manage/api/api_padLogin.php?func=login&tel=" + login_tel + "&hospCode=" + hospCode);

                        if (!result.equals("failed")) {

//                            handler.sendEmptyMessage(0);

                            if (result.contains("登录失败")) {

                                handler.sendEmptyMessage(-1);

                            } else {
                                FamilyBedModelBean familyBedModelBean = JSONHelper.parseObject(result, FamilyBedModelBean.class);

                                //登录成功后跳转页面
                                Intent intent = new Intent(LoginActivity.this, MainFamilyBed.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("familyBedModelBean", familyBedModelBean);
                                intent.putExtras(bundle);
                                startActivity(intent);

                                //完成跳转，关闭此activity（避免返回至此）
                                finish();
                            }

                        } else {
                            handler.sendEmptyMessage(-1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }

    }

    //同步请求
    public void getSync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url("你的后台地址")
                        .build();
                Call call = client.newCall(request);
                try {
                    //同步发送请求
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        String s = response.body().string();
                        System.out.println("text:" + s);
                        System.out.println("请求成功");
                    } else {
                        System.out.println("请求失败");
                    }
                } catch (IOException e) {
                    System.out.println("error");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //异步请求
    public void getAsync() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("你的后台地址")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    System.out.println(result);
                }
            }
        });
    }

    public void post() {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("type", "fyjl")
                .add("ids", "1")
                .add("sd", "早上")
                .add("Inlet", "App")
                .build();
        Request request = new Request.Builder()
                .url(url_post)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("响应");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    System.out.println("响应成功："+result);
                }
            }
        });
    }
}
