package com.dywl.familybed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dywl.familybed.model.FamilyBedModelBean;
import com.dywl.familybed.model.RtcEngineConfigJsonBean;
import com.dywl.familybed.utils.JSONHelper;
import com.dywl.familybed.utils.MultipleResult;
import com.dywl.familybed.utils.ToastUtil;
import com.dywl.familybed.utils.WebTool;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case ServerUrl.handler_error:
//                    tv_result.setText("登录失败，请重试。");
                    Toast.makeText(getApplicationContext(), "失败，请重试。", Toast.LENGTH_LONG).show();
                    break;
                case ServerUrl.handler_success:
//                    tv_result.setText("登录成功，跳转。");
                    Toast.makeText(getApplicationContext(), "成功，跳转。", Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    });

    private TextToSpeech mTTs;
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
//        getSync();

        mTTs = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int intResult = mTTs.setLanguage(Locale.CHINESE);
                    if(intResult==TextToSpeech.LANG_MISSING_DATA||intResult==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("mTTs","语言不支持");
                    }
                }else {
                    Log.e("mTTs","初始化失败");
                }

            }
        });
    }
    @Override
    protected void onDestroy() {
        if(mTTs!=null){
            mTTs.stop();
            mTTs.shutdown();
        }
        super.onDestroy();
    }

    public void bClick(View v) {
        if (v.getTag().toString().equals("tagLogin")) {
            hospCode = editZhuYuanHao.getText().toString().trim();
            login_tel = editTextPhone.getText().toString().trim();
//            Toast.makeText(getApplicationContext(), "登录事件", Toast.LENGTH_LONG).show();

//            mTTs.speak(hospCode,TextToSpeech.QUEUE_FLUSH,null);
            if (TextUtils.isEmpty(hospCode)) {
                ToastUtil.showToast("请输入住院号！");
                return;
            }
            if (TextUtils.isEmpty(login_tel)) {
                ToastUtil.showToast("请输入登记手机！");
                return;
            }

            getSync(login_tel,hospCode);

        }

    }

    //同步请求
    public void getSync(String strTel,String strHospCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(ServerUrl.get_login + strTel + "&hospCode=" + strHospCode)
                        .build();
                Call call = client.newCall(request);
                try {
                    //同步发送请求
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        String strResult = response.body().string();

                        FamilyBedModelBean familyBedModelBean = JSONHelper.parseObject(strResult, FamilyBedModelBean.class);

                        MyApp.setFamilyBedModelBean(familyBedModelBean);


                        getRtcEngineConfig();
                        //登录成功后跳转页面
//                        Intent intent = new Intent(LoginActivity.this, MainFamilyBed.class);
                        Intent intent = new Intent(LoginActivity.this, MainFamilyBed.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("familyBedModelBean", familyBedModelBean);
                        intent.putExtras(bundle);
                        startActivity(intent);


                        //完成跳转，关闭此activity（避免返回至此）
                        finish();
//                        System.out.println("text:" + strResult);
//                        System.out.println("请求成功");

                        handler.sendEmptyMessage(ServerUrl.handler_success);
                    } else {
//                        System.out.println("请求失败");
                        handler.sendEmptyMessage(ServerUrl.handler_error);
                    }
                } catch (IOException e) {
//                    System.out.println("error");
                    handler.sendEmptyMessage(ServerUrl.handler_error);
                    e.printStackTrace();
                } catch (JSONException e) {
                    handler.sendEmptyMessage(ServerUrl.handler_error);
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    //异步请求
    public void getAsync(String strBedID) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ServerUrl.get_today_medication +strBedID)
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
                .url(ServerUrl.base_url_post)
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



    // region 获取数据：视频通话配置
    private void getRtcEngineConfig() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在新线程中获取数据，通过handler判断是否获取到数据
                    String result = WebTool.singleData(ServerUrl.get_rtc_engine_config);

                    if (!result.equals("failed")) {


                        if (result.contains("成功")) {
//                            // 创建消息
//                            Message msg = new Message();
//                            Bundle bundle = new Bundle();
//                            msg.what = ServerUrl.handler_success_today_medication_get;
//                            bundle.putString("strResult", result);
//                            msg.setData(bundle);
//                            handler.sendMessage(msg);

                            RtcEngineConfigJsonBean rtcEngineConfigJsonBean = JSONHelper.parseObject(result, RtcEngineConfigJsonBean.class);

                            MyApp.setRtcEngineConfigJsonBean(rtcEngineConfigJsonBean);

                        } else {

                            handler.sendEmptyMessage(ServerUrl.handler_error);
                        }

                    } else {
                        handler.sendEmptyMessage(ServerUrl.handler_error);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    // endregion 获取数据：视频通话配置
}
