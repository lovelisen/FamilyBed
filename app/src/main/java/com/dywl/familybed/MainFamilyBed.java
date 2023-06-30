package com.dywl.familybed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dywl.familybed.adapter.ListViewAdapter;
import com.dywl.familybed.model.FamilyBedModelBean;
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
import com.dywl.familybed.utils.SysExitUtil;
import com.dywl.familybed.utils.WebTool;

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


public class MainFamilyBed extends Activity {
    private FamilyBedModelBean familyBedModelBean = null;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private TextView textView8;
    private TextView textView9;
    private TextView textView10;

    // 体征上报
    private ImageView imageView4;
    // 今日用药
    private ImageView imageView5;
    private View view_custom;
    private View view_custom_list;
    private AlertDialog.Builder builder_list = null;
    private AlertDialog alert = null;
    private AlertDialog alert_list = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;
    private List<TodaysMedicationJsonDrug> listTodaysMedicationJsonDrug = null;
    private List<TodaysMedicationJsonUsed> listTodaysMedicationJsonUsed = null;

    private TextToSpeech mTTs;
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

    // region 实现onCreate抽象方法
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familybedmain); //

        SysExitUtil.activityList.add(MainFamilyBed.this);

//        Intent intent = super.getIntent();
//        familyBedModelBean = (FamilyBedModelBean) intent.getSerializableExtra("familyBedModelBean");

        familyBedModelBean = MyApp.getFamilyBedModelBean();

        // region 数据初始化：患者基本信息模块
        textView1 = findViewById(R.id.main_patient_name);
        textView6 = findViewById(R.id.main_patient_number);
        textView8 = findViewById(R.id.main_patient_department);
        textView2 = findViewById(R.id.main_patient_disease);
        textView3 = findViewById(R.id.main_patient_create_bed_date);
        textView4 = findViewById(R.id.main_patient_bed);
        textView5 = findViewById(R.id.main_patient_attending_doctor);
        textView7 = findViewById(R.id.main_patient_patrol_nurse);
        textView9 = findViewById(R.id.main_patient_dietary_guidelines);
        textView10 = findViewById(R.id.main_patient_note);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);

        textView1.setText(familyBedModelBean.getData().getName() + "(" + familyBedModelBean.getData().getSex() + ")");
        textView2.setText("病种：" + familyBedModelBean.getData().getDisease());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date createDate = sdf.parse(familyBedModelBean.getData().getCreateTime());
            String createDateFormat = sdf.format(createDate);
            textView3.setText("建床时间：" + createDateFormat);
            // 获取当前时间
            Calendar cal = Calendar.getInstance();
            Date nowDate = cal.getTime();
            int dateBett = daysBetween(createDate, nowDate);
            if (dateBett == 0) {
                dateBett = 1;
            }
            textView4.setText("已建床" + dateBett + "天");
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        textView5.setText("主管医生：" + familyBedModelBean.getData().getDoctor());
        textView7.setText("责任护士：" + familyBedModelBean.getData().getNurse());
        textView6.setText("住院号：" + familyBedModelBean.getData().getNumber());
        textView8.setText("科室：" + familyBedModelBean.getData().getDepartment());
        textView9.setText("饮食推荐：" + familyBedModelBean.getData().getDiet());
        textView10.setText("护理等级：" + familyBedModelBean.getData().getNurseLV());
        // endregion 数据初始化：患者基本信息模块

        mContext = MainFamilyBed.this;

        // region 弹出框初始化：今日用药、体征上报
        //初始化Builder
        builder = new AlertDialog.Builder(mContext);
        builder_list = new AlertDialog.Builder(mContext);
//        builder = new AlertDialog.Builder(mContext,R.style.styletest);
//        builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
//        builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder = new AlertDialog.Builder(mContext, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);
        builder_list = new AlertDialog.Builder(mContext, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = MainFamilyBed.this.getLayoutInflater();
        final LayoutInflater inflater_list = MainFamilyBed.this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.activity_demo_dialog_gridview, null, false);
        view_custom_list = inflater_list.inflate(R.layout.activity_demo_dialog_listview, null, false);
        builder.setView(view_custom);
        builder_list.setView(view_custom_list);
        builder.setCancelable(false);
        builder_list.setCancelable(false);

        alert = builder.create();
        alert_list = builder_list.create();

//        TextView txt_name_value = (TextView) view_custom_list.findViewById(R.id.txt_name_value);
//        alert_list.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(txt_name_value, InputMethodManager.SHOW_IMPLICIT);
//
//            }
//        });

//        alert = builder.setTitle("今日用药提醒").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
//                alert.dismiss();
//            }
//        }).create();
//        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        alert_list.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        alert_list.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
//                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        alert_list.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        alert.getWindow().getAttributes().dimAmount = 0f;
//        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view_custom.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        view_custom_list.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_list.dismiss();
            }
        });
        // endregion  弹出框初始化：今日用药、体征上报

        // region 点击事件：弹出页面-体征上报
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainFamilyBed.this, DailyReportingActivity.class);
                startActivity(intent);

            }
        });
        // endregion 点击事件：弹出页面-体征上报

        // region 点击事件：弹出页面-今日用药
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTodaysMedicationData();

                alert.show();
            }
        });
        // endregion 点击事件：弹出页面-今日用药


        mTTs = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int intResult = mTTs.setLanguage(Locale.CHINESE);
                    if (intResult == TextToSpeech.LANG_MISSING_DATA || intResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("mTTs", "语言不支持");
                    }
                } else {
                    Log.e("mTTs", "初始化失败");
                }

            }
        });
    }
    // endregion 实现onCreate抽象方法

    // region 获取数据：今日用药
    private void getTodaysMedicationData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在新线程中获取数据，通过handler判断是否获取到数据
                    String result = WebTool.singleData(ServerUrl.get_today_medication + familyBedModelBean.getData().getID());

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
        GridView grid_today_medication = (GridView) view_custom.findViewById(R.id.grid_today_medication);


        TodaysMedicationJsonData todaysMedicationJsonData = todaysMedicationJsonBean.getData();
        listTodaysMedicationJsonDrug = todaysMedicationJsonData.getDrug();
        listTodaysMedicationJsonUsed = todaysMedicationJsonData.getUsed();


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

        String name = familyBedModelBean.getData().getName();


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


        todaysMedicationAdapterChild_ZaoShang = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_ZaoShang, R.layout.list_item_todays_medication_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild_ZhongWu = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_ZhongWu, R.layout.list_item_todays_medication_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild_WanShang = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_WanShang, R.layout.list_item_todays_medication_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage() + "/" + obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托：" + obj.getEntrust());
            }
        };
        todaysMedicationAdapterChild_ShuiQian = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild_ShuiQian, R.layout.list_item_todays_medication_child) {
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

        todaysMedicationAdapter = new ListViewAdapter<TodaysMedicationBean>((ArrayList) dataTodaysMedicationBean, R.layout.list_item_todays_medication) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBean obj) {
                holder.setText(R.id.txt_today_date, obj.getTodaysDate());
                holder.setText(R.id.txt_period, obj.getPeriod());
                holder.setAdapter(R.id.list_today_medication_child, obj.getAdapter());
                holder.setText(R.id.btn_take_medicine, obj.getUsed());
                holder.setTag(R.id.btn_take_medicine, familyBedModelBean.getData().getPatientID() + "!" + familyBedModelBean.getData().getID() + "!" + obj.getPeriod() + "!" + obj.getIds());
                holder.setTag(R.id.imageView_voice_ico, obj.getVoices());
                if (obj.getUsed().equals("未服药")) {
                    holder.setImageResource(R.id.btn_take_medicine, R.mipmap.dialog_weifuyao);
                }
                holder.setOnClickListener(R.id.imageView_voice_ico, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mTTs.speak(v.getTag().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
                holder.setOnClickListener(R.id.btn_take_medicine, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] split = v.getTag().toString().split("!");
//                        Toast.makeText(getApplicationContext(), "床号："+split[1]+"    患者编号："+split[0]+"    时段："+split[2]+"    IDS："+split[3], Toast.LENGTH_SHORT).show();
                        // 调用已服药接口
                        post(split[2], split[3]);
                        alert.dismiss();
                    }
                });
            }
        };

        //ListView设置Adapter：
        grid_today_medication.setAdapter(todaysMedicationAdapter);
    }
    // endregion 构建页面:今日用药提醒

    // region 计算两个日期之间相差的天数

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }
    // endregion 计算两个日期之间相差的天数


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
