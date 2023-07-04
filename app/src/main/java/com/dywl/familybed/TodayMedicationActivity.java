package com.dywl.familybed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dywl.familybed.adapter.ListViewAdapter;
import com.dywl.familybed.model.MeiriSbJsonBean;
import com.dywl.familybed.model.MeiriSbJsonModel;
import com.dywl.familybed.model.PhysicalSignJsonBean;
import com.dywl.familybed.model.PhysicalSignJsonModel;
import com.dywl.familybed.model.TodaysMedicationBean;
import com.dywl.familybed.model.TodaysMedicationBeanChild;
import com.dywl.familybed.model.TodaysMedicationJsonBean;
import com.dywl.familybed.model.TodaysMedicationJsonData;
import com.dywl.familybed.model.TodaysMedicationJsonDrug;
import com.dywl.familybed.model.TodaysMedicationJsonUsed;
import com.dywl.familybed.utils.JSONHelper;
import com.dywl.familybed.utils.ToastUtil;
import com.dywl.familybed.utils.WebTool;
import com.dywl.familybed.wigit.OriginalTTSManager;
import com.dywl.familybed.wigit.WhyTTS;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TodayMedicationActivity extends BaseActivity {

    private Context mContext;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private WhyTTS whyTTS;
    String toolTipText = "null";
    //region 初始化今日体征上报页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 设置为全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.today_medication_activity);//创建客户经理主页面

        mContext = TodayMedicationActivity.this;


        // region 按钮事件：返回
        ImageView ac_back_icon = (ImageView) findViewById(R.id.ac_back_icon);//实例化图片设置图片控件
        ac_back_icon.setOnClickListener(new View.OnClickListener() {//添加点击事件
            @Override
            public void onClick(View v) {
                if(toolTipText !="null"){
                    whyTTS.pause();
                }
                Intent intent = new Intent(TodayMedicationActivity.this, MainFamilyBed.class);//点击跳转到设置窗口
                startActivity(intent);
            }
        });
        // endregion 按钮事件：返回

        // region 按钮事件：查看历史
        ImageView img_setting = (ImageView) findViewById(R.id.img_setting);//实例化图片设置图片控件
        img_setting.setOnClickListener(new View.OnClickListener() {//添加点击事件
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AccountManagerActivity.this, SettingActivity.class);//点击跳转到设置窗口
//                startActivity(intent);
            }
        });
        // endregion 按钮事件：查看历史

        getTodaysMedicationData();

        whyTTS= OriginalTTSManager.getInstance(this);

    }
    //endregion

    //region 跳转页面 回调方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
    //endregion

    // region 在主线程中定义Handler
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case ServerUrl.handler_success:
//                    tv_result.setText("登录失败，请重试。");
                    Toast.makeText(getApplicationContext(), "响应成功。", Toast.LENGTH_LONG).show();
                    break;
                case ServerUrl.handler_error:
//                    tv_result.setText("登录失败，请重试。");
                    Toast.makeText(getApplicationContext(), "失败，请重试。", Toast.LENGTH_LONG).show();
                    break;
                case ServerUrl.handler_success_today_medication_get:
                    try {
                        String strResult = message.getData().getString("strResult");
                        TodaysMedicationJsonBean todaysMedicationJsonBean = JSONHelper.parseObject(strResult, TodaysMedicationJsonBean.class);


                        init(todaysMedicationJsonBean);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    break;
            }
            return false;
        }
    });
    // endregion 在主线程中定义Handler

    // region 获取数据：今日用药
    private void getTodaysMedicationData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在新线程中获取数据，通过handler判断是否获取到数据
                    String result = WebTool.singleData(ServerUrl.get_today_medication + MyApp.getFamilyBedModelBean().getData().getID());

                    if (!result.equals("failed")) {


                        if (result.contains("成功")) {
                            // 创建消息
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            msg.what = ServerUrl.handler_success_today_medication_get;
                            bundle.putString("strResult", result);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
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
    // endregion 获取数据：今日用药

    // region 构建页面:今日用药提醒
    private void init(TodaysMedicationJsonBean todaysMedicationJsonBean) throws JSONException {
        GridView grid_today_medication = (GridView) findViewById(R.id.grid_today_medication);


        TodaysMedicationJsonData todaysMedicationJsonData = todaysMedicationJsonBean.getData();

        List<TodaysMedicationJsonDrug> listTodaysMedicationJsonDrug = todaysMedicationJsonData.getDrug();
        List<TodaysMedicationJsonUsed> listTodaysMedicationJsonUsed = todaysMedicationJsonData.getUsed();

        ListViewAdapter<TodaysMedicationBean> todaysMedicationAdapter = null;
        ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ZaoShang = null;
        ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ZhongWu = null;
        ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_WanShang = null;
        ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ShuiQian = null;
        List<TodaysMedicationBean> dataTodaysMedicationBean = new ArrayList<TodaysMedicationBean>();
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZaoShang = new ArrayList<TodaysMedicationBeanChild>();
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZhongWu = new ArrayList<TodaysMedicationBeanChild>();
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_WanShang = new ArrayList<TodaysMedicationBeanChild>();
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ShuiQian = new ArrayList<TodaysMedicationBeanChild>();

        TodaysMedicationBean todaysMedicationBean_ZaoShang = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean_ZhongWu = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean_WanShang = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean_ShuiQian = new TodaysMedicationBean();

        String ids_ZaoShang = "";
        String ids_ZhongWu = "";
        String ids_WanShang = "";
        String ids_ShuiQian = "";

        String voice_ZaoShang = "";
        String voice_ZhongWu = "";
        String voice_WanShang = "";
        String voice_ShuiQian = "";

        String name = MyApp.getFamilyBedModelBean().getData().getName();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前时间
        Calendar cal = Calendar.getInstance();
        Date nowDate = cal.getTime();
        String createDateFormat = sdf.format(nowDate);

        for (TodaysMedicationJsonUsed itemTodaysMedicationJsonUsed : todaysMedicationJsonData.getUsed()) {
            String strPeriod = itemTodaysMedicationJsonUsed.getPeriod();
            if (strPeriod.equals("早上")) {
                todaysMedicationBean_ZaoShang.setPeriod(strPeriod);
                todaysMedicationBean_ZaoShang.setTodaysDate(createDateFormat);
                todaysMedicationBean_ZaoShang.setUsed(itemTodaysMedicationJsonUsed.getUsed());
                voice_ZaoShang += "姓名：" + name + "，今天是" + createDateFormat + "，现在播报您" + strPeriod + "的用药提醒：";
            } else if (strPeriod.equals("中午")) {
                todaysMedicationBean_ZhongWu.setPeriod(strPeriod);
                todaysMedicationBean_ZhongWu.setTodaysDate(createDateFormat);
                todaysMedicationBean_ZhongWu.setUsed(itemTodaysMedicationJsonUsed.getUsed());
                voice_ZhongWu += "姓名：" + name + "，今天是" + createDateFormat + "，现在播报您" + strPeriod + "的用药提醒：";
            } else if (strPeriod.equals("晚上")) {
                todaysMedicationBean_WanShang.setPeriod(strPeriod);
                todaysMedicationBean_WanShang.setTodaysDate(createDateFormat);
                todaysMedicationBean_WanShang.setUsed(itemTodaysMedicationJsonUsed.getUsed());
                voice_WanShang += "姓名：" + name + "，今天是" + createDateFormat + "，现在播报您" + strPeriod + "的用药提醒：";
            } else if (strPeriod.equals("睡前")) {
                todaysMedicationBean_ShuiQian.setUsed(itemTodaysMedicationJsonUsed.getUsed());
                todaysMedicationBean_ShuiQian.setPeriod(strPeriod);
                todaysMedicationBean_ShuiQian.setTodaysDate(createDateFormat);
                voice_ShuiQian += "姓名：" + name + "，今天是" + createDateFormat + "，现在播报您" + strPeriod + "的用药提醒：";
            }
        }

        for (TodaysMedicationJsonDrug itemTodaysMedicationJsonDrug : todaysMedicationJsonData.getDrug()) {

//            String[] strPeriodList = itemTodaysMedicationJsonDrug.getPeriod().split(",");
//            char[] strPeriodList = itemTodaysMedicationJsonDrug.getPeriod().toCharArray();
            JSONArray array = new JSONArray(itemTodaysMedicationJsonDrug.getPeriod());

            for (int i = 0; i < array.length(); i++) {
                // 时段
                String strPeriod = array.get(i).toString();
                // 药品名称
                String drug = itemTodaysMedicationJsonDrug.getDrug();
                // 用法
                String usage = itemTodaysMedicationJsonDrug.getUsage();
                // 剂量
                String note = itemTodaysMedicationJsonDrug.getNote();
                // 嘱托
                String entrust = itemTodaysMedicationJsonDrug.getEntrust();

                // region 早上、中午、晚上、睡前各阶段用药信息数据
                if (strPeriod.equals("早上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZaoShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZaoShang.setDrug(drug);
                    todaysMedicationBeanChild_ZaoShang.setUsage(usage);
                    todaysMedicationBeanChild_ZaoShang.setNote(note);
                    todaysMedicationBeanChild_ZaoShang.setEntrust(entrust);

                    ids_ZaoShang += itemTodaysMedicationJsonDrug.getID() + ",";

                    voice_ZaoShang += "，药品名称：" + drug + "，用法：" + usage + "，剂量：" + note + "，嘱托：" + entrust;

                    dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_ZaoShang);
                } else if (strPeriod.equals("中午")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZhongWu = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZhongWu.setDrug(drug);
                    todaysMedicationBeanChild_ZhongWu.setUsage(usage);
                    todaysMedicationBeanChild_ZhongWu.setNote(note);
                    todaysMedicationBeanChild_ZhongWu.setEntrust(entrust);

                    ids_ZhongWu += itemTodaysMedicationJsonDrug.getID() + ",";
                    voice_ZhongWu += "，药品名称：" + drug + "，用法：" + usage + "，剂量：" + note + "，嘱托：" + entrust;

                    dataTodaysMedicationBeanChild_ZhongWu.add(todaysMedicationBeanChild_ZhongWu);
                } else if (strPeriod.equals("晚上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_WanShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_WanShang.setDrug(drug);
                    todaysMedicationBeanChild_WanShang.setUsage(usage);
                    todaysMedicationBeanChild_WanShang.setNote(note);
                    todaysMedicationBeanChild_WanShang.setEntrust(entrust);

                    ids_WanShang += itemTodaysMedicationJsonDrug.getID() + ",";
                    voice_WanShang += "，药品名称：" + drug + "，用法：" + usage + "，剂量：" + note + "，嘱托：" + entrust;

                    dataTodaysMedicationBeanChild_WanShang.add(todaysMedicationBeanChild_WanShang);

                } else if (strPeriod.equals("睡前")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ShuiQian = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ShuiQian.setDrug(drug);
                    todaysMedicationBeanChild_ShuiQian.setUsage(usage);
                    todaysMedicationBeanChild_ShuiQian.setNote(note);
                    todaysMedicationBeanChild_ShuiQian.setEntrust(entrust);

                    ids_ShuiQian += itemTodaysMedicationJsonDrug.getID() + ",";
                    voice_ShuiQian += "，药品名称：" + drug + "，用法：" + usage + "，剂量：" + note + "，嘱托：" + entrust;

                    dataTodaysMedicationBeanChild_ShuiQian.add(todaysMedicationBeanChild_ShuiQian);
                }
                // endregion
            }
        }


        todaysMedicationAdapterChild_ZaoShang = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_ZaoShang, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild_ZhongWu = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_ZhongWu, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild_WanShang = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_WanShang, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };
        todaysMedicationAdapterChild_ShuiQian = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_ShuiQian, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };

        todaysMedicationBean_ZaoShang.setAdapter(todaysMedicationAdapterChild_ZaoShang);
        todaysMedicationBean_ZhongWu.setAdapter(todaysMedicationAdapterChild_ZhongWu);
        todaysMedicationBean_WanShang.setAdapter(todaysMedicationAdapterChild_WanShang);
        todaysMedicationBean_ShuiQian.setAdapter(todaysMedicationAdapterChild_ShuiQian);

        if (ids_ZaoShang.substring(ids_ZaoShang.length() - 1).equals(",")) {
            ids_ZaoShang = ids_ZaoShang.substring(0, ids_ZaoShang.length() - 1);
        }
        if (ids_ZhongWu.substring(ids_ZhongWu.length() - 1).equals(",")) {
            ids_ZhongWu = ids_ZhongWu.substring(0, ids_ZhongWu.length() - 1);
        }
        if (ids_WanShang.substring(ids_WanShang.length() - 1).equals(",")) {
            ids_WanShang = ids_WanShang.substring(0, ids_WanShang.length() - 1);
        }
        if (ids_ShuiQian.substring(ids_ShuiQian.length() - 1).equals(",")) {
            ids_ShuiQian = ids_ShuiQian.substring(0, ids_ShuiQian.length() - 1);
        }

        todaysMedicationBean_ZaoShang.setIds(ids_ZaoShang);
        todaysMedicationBean_ZhongWu.setIds(ids_ZhongWu);
        todaysMedicationBean_WanShang.setIds(ids_WanShang);
        todaysMedicationBean_ShuiQian.setIds(ids_ShuiQian);

        todaysMedicationBean_ZaoShang.setVoices(voice_ZaoShang);
        todaysMedicationBean_ZhongWu.setVoices(voice_ZhongWu);
        todaysMedicationBean_WanShang.setVoices(voice_WanShang);
        todaysMedicationBean_ShuiQian.setVoices(voice_ShuiQian);


        dataTodaysMedicationBean.add(todaysMedicationBean_ZaoShang);
        dataTodaysMedicationBean.add(todaysMedicationBean_ZhongWu);
        dataTodaysMedicationBean.add(todaysMedicationBean_WanShang);
        dataTodaysMedicationBean.add(todaysMedicationBean_ShuiQian);

        todaysMedicationAdapter = new ListViewAdapter<TodaysMedicationBean>((ArrayList) dataTodaysMedicationBean, R.layout.today_medication_list_item) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBean obj) {
                holder.setText(R.id.txt_today_date, obj.getTodaysDate());
                holder.setText(R.id.txt_period, obj.getPeriod());
                holder.setAdapter(R.id.list_today_medication_child, obj.getAdapter());
                holder.setText(R.id.btn_take_medicine, obj.getUsed());
                holder.setTag(R.id.btn_take_medicine, MyApp.getFamilyBedModelBean().getData().getPatientID() + "!" + MyApp.getFamilyBedModelBean().getData().getID() + "!" + obj.getPeriod() + "!" + obj.getIds());
                holder.setTag(R.id.imageView_voice_ico, obj.getVoices());
                holder.setTooltipText(R.id.imageView_voice_ico, "start");
                holder.setOnClickListener(R.id.imageView_voice_ico, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // region 初始化设置所有语音播放按钮的图标和状态
                        GridView grid_today_medication = (GridView) findViewById(R.id.grid_today_medication);
                        int listChildCount = grid_today_medication.getChildCount();
                        for (int i = 0; i < listChildCount; i++) {
                            View view = grid_today_medication.getChildAt(i);
                            ImageView imageView_voice_ico = (ImageView) view.findViewById(R.id.imageView_voice_ico);
                            imageView_voice_ico.setImageResource(R.mipmap.voice_ico_start);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.setTooltipText("start");
                            }
                        }
                        // endregion 初始化设置所有语音播放按钮的图标和状态

                        // region 控制播放、停止
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            toolTipText = v.getTooltipText().toString();
                        }
                        if(toolTipText == "start"){
                            whyTTS.speak(v.getTag().toString());

                            ((ImageView) v).setImageResource(R.mipmap.voice_ico_stop);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.setTooltipText("stop");
                            }
                        } else if (toolTipText == "stop") {
                            whyTTS.pause();
                            ((ImageView) v).setImageResource(R.mipmap.voice_ico_start);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                v.setTooltipText("start");
                            }
                        }
                        // endregion 控制播放、停止
                    }
                });

                if (obj.getUsed().equals("未服药")) {
                    holder.setImageResource(R.id.btn_take_medicine, R.mipmap.dialog_weifuyao);
                    holder.setOnClickListener(R.id.btn_take_medicine, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] split = v.getTag().toString().split("!");
//                        Toast.makeText(getApplicationContext(), "床号："+split[1]+"    患者编号："+split[0]+"    时段："+split[2]+"    IDS："+split[3], Toast.LENGTH_SHORT).show();
                            // 调用已服药接口
                            post(split[2], split[3]);

                            holder.setImageResource(R.id.btn_take_medicine, R.mipmap.dialog_yifuyao);

                            holder.setText(R.id.btn_take_medicine, "已服药");
                            holder.setOnClickListener(R.id.btn_take_medicine,null);

//                            holder.setEnabled(R.id.btn_take_medicine,false);
//                            getTodaysMedicationData();

                        }
                    });
                }
            }
        };
        //ListView设置Adapter：
        grid_today_medication.setAdapter(todaysMedicationAdapter);

        todaysMedicationAdapter.notifyDataSetChanged();
    }
    // endregion 构建页面:今日用药提醒


    // region 数据提交：今日用药未服药->已服药
    public void post(String sd, String ids) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("type", "fyjl")
                .add("ids", ids)
                .add("sd", sd)
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
//                System.out.println("响应失败！");

                handler.sendEmptyMessage(ServerUrl.handler_error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
//                    System.out.println("响应成功："+result);
                    handler.sendEmptyMessage(ServerUrl.handler_success);
                }
            }
        });
    }
    // endregion 数据提交：今日用药未服药->已服药

}
