package com.dywl.familybed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
                case ServerUrl.handler_success_physical_sign_get:
//                    tv_result.setText("登录成功，跳转。");
//                    Toast.makeText(getApplicationContext(), "成功，跳转。", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "今日用药", Toast.LENGTH_LONG).show();
                    try {
                        String strResult = message.getData().getString("strResult");

                        PhysicalSignJsonBean physicalSignJsonBean = JSONHelper.parseObject(strResult, PhysicalSignJsonBean.class);
                        initPhysicalSign(physicalSignJsonBean);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("请求成功");
                    break;
                case ServerUrl.handler_success_daily_reporting_get:
                    try {
                        String strResult = message.getData().getString("strResult");

                        int count_sb_num = Integer.parseInt(familyBedModelBean.getData().getSbNum());
                        int set_sb_time = Integer.parseInt(familyBedModelBean.getData().getSbZq());

                        TextView tv_physical_sign_count = (TextView) view_custom_list.findViewById(R.id.tv_physical_sign_count);
                        Button btn_save_physical_sign = (Button) view_custom_list.findViewById(R.id.btn_save_physical_sign);
                        MeiriSbJsonBean meiriSbJsonBean = JSONHelper.parseObject(strResult, MeiriSbJsonBean.class);
                        int count_data = meiriSbJsonBean.getData().getMeiriSb().size();
                        int tv_count_data = count_data + 1;

                        String view_sb_time = "";
                        for (MeiriSbJsonModel meiriSbJsonModel : meiriSbJsonBean.getData().getMeiriSb()) {
                            view_sb_time = meiriSbJsonModel.getSbTime();
                        }

                        String view_sb_time_next = "";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        SimpleDateFormat sdf_v = new SimpleDateFormat();
                        sdf_v.applyPattern("HH:mm:ss");
                        try {
                            Date createDate = sdf.parse(view_sb_time);

                            // 获取当前时间
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(createDate);
                            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + set_sb_time);
                            view_sb_time_next = sdf_v.format(cal.getTime());
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }

                        String view_text = "";
                        if (count_data == 0) {
                            view_text = "今日推荐上报" + count_sb_num + "次体征，当前是第" + tv_count_data + "次上报。\n";
                        } else if (count_data > 0 && count_data < count_sb_num) {
                            view_text = "今日推荐上报" + count_sb_num + "次体征，当前是第" + tv_count_data + "次上报。\n" +
                                    "上次上报时间是" + view_sb_time + "\n本次上报推荐时间在" + view_sb_time_next + "之后。";
                        } else if (count_data >= count_sb_num) {
                            view_text = "今日推荐上报" + count_sb_num + "次体征，您已上报完成" + count_data + "次。";
                            btn_save_physical_sign.setVisibility(View.GONE);
                        }
                        tv_physical_sign_count.setText(view_text);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("请求成功");
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

        Intent intent = super.getIntent();
        familyBedModelBean = (FamilyBedModelBean) intent.getSerializableExtra("familyBedModelBean");

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
        alert_list.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alert_list.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

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

        // region 点击事件：数据提交-体征上报
        view_custom_list.findViewById(R.id.btn_save_physical_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ListView list_today_physical_sign = (ListView) view_custom_list.findViewById(R.id.list_physical_sign);
                int listChildCount = list_today_physical_sign.getChildCount();
//                System.out.println("控件数："+listChildCount);


                String bedid = familyBedModelBean.getData().getID();
                String id = ""; // 如果是更新模式，则从参数中获取，否则为空。
                ArrayList<String> array_mc = new ArrayList<String>();
                ArrayList<String> array_zb = new ArrayList<String>();
                String Xy1 = "";
                String Xy2 = "";

                for (int i = 0; i < listChildCount; i++) {
                    View view = list_today_physical_sign.getChildAt(i);
                    TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
                    EditText txt_name_value = (EditText) view.findViewById(R.id.txt_name_value);
                    EditText txt_name_value_left = (EditText) view.findViewById(R.id.txt_name_value_left);
                    EditText txt_name_value_right = (EditText) view.findViewById(R.id.txt_name_value_right);
                    TextView txt_unit = (TextView) view.findViewById(R.id.txt_unit);
                    // 名称
                    array_mc.add(txt_name.getText().toString());
                    if (txt_name.getText().equals("血压")) {
                        // 高压值
                        Xy1 = txt_name_value_left.getText().toString();
                        // 低压值
                        Xy2 = txt_name_value_right.getText().toString();
                        // 指标值
                        array_zb.add(Xy1 + "/" + Xy2);

                    } else {
                        // 指标值
                        array_zb.add(txt_name_value.getText().toString());
                    }

                }

                post_tzsb(bedid, id, array_mc, array_zb, Xy1, Xy2);

                alert_list.dismiss();
            }
        });
        // endregion 点击事件：数据提交-体征上报

        // region 点击事件：弹出页面-体征上报
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSync(familyBedModelBean.getData().getID());
                getMeiRiSb(familyBedModelBean.getData().getID());

                alert_list.show();
            }
        });
        // endregion 点击事件：弹出页面-体征上报

        // region 点击事件：弹出页面-今日用药
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTodaysMedicationData();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                alert.show();
            }
        });
        // endregion 点击事件：弹出页面-今日用药
    }
    // endregion 实现onCreate抽象方法

    // region 获取数据：今日用药
    private void getTodaysMedicationData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在新线程中获取数据，通过handler判断是否获取到数据
                    String result = WebTool.singleData("http://open.dywyhs.com/project/familybed/manage/api/api_padLogin.php?func=getDrug&BedID=" + familyBedModelBean.getData().getID());

                    if (!result.equals("failed")) {

                        handler.sendEmptyMessage(ServerUrl.handler_success);

                        if (result.contains("成功")) {
                            TodaysMedicationJsonBean todaysMedicationJsonBean = JSONHelper.parseObject(result, TodaysMedicationJsonBean.class);


                            init(todaysMedicationJsonBean);
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

        for (TodaysMedicationJsonDrug itemTodaysMedicationJsonDrug : todaysMedicationJsonData.getDrug()) {

//            String[] strPeriodList = itemTodaysMedicationJsonDrug.getPeriod().split(",");
//            char[] strPeriodList = itemTodaysMedicationJsonDrug.getPeriod().toCharArray();
            JSONArray array = new JSONArray(itemTodaysMedicationJsonDrug.getPeriod());

            for (int i = 0; i < array.length(); i++) {

                String strPeriod = array.get(i).toString();
                // region 早上、中午、晚上、睡前各阶段用药信息数据
                if (strPeriod.equals("早上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZaoShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZaoShang.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ZaoShang.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ZaoShang.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_ZaoShang.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_ZaoShang += itemTodaysMedicationJsonDrug.getID() + ",";

                    dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_ZaoShang);
                } else if (strPeriod.equals("中午")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZhongWu = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZhongWu.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ZhongWu.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ZhongWu.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_ZhongWu.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_ZhongWu += itemTodaysMedicationJsonDrug.getID() + ",";

                    dataTodaysMedicationBeanChild_ZhongWu.add(todaysMedicationBeanChild_ZhongWu);
                } else if (strPeriod.equals("晚上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_WanShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_WanShang.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_WanShang.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_WanShang.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_WanShang.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_WanShang += itemTodaysMedicationJsonDrug.getID() + ",";

                    dataTodaysMedicationBeanChild_WanShang.add(todaysMedicationBeanChild_WanShang);

                } else if (strPeriod.equals("睡前")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ShuiQian = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ShuiQian.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ShuiQian.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ShuiQian.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_ShuiQian.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_ShuiQian += itemTodaysMedicationJsonDrug.getID() + ",";

                    dataTodaysMedicationBeanChild_ShuiQian.add(todaysMedicationBeanChild_ShuiQian);
                }
                // endregion
            }
        }

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
            } else if (strPeriod.equals("中午")) {
                todaysMedicationBean_ZhongWu.setPeriod(strPeriod);
                todaysMedicationBean_ZhongWu.setTodaysDate(createDateFormat);
                todaysMedicationBean_ZhongWu.setUsed(itemTodaysMedicationJsonUsed.getUsed());
            } else if (strPeriod.equals("晚上")) {
                todaysMedicationBean_WanShang.setPeriod(strPeriod);
                todaysMedicationBean_WanShang.setTodaysDate(createDateFormat);
                todaysMedicationBean_WanShang.setUsed(itemTodaysMedicationJsonUsed.getUsed());
            } else if (strPeriod.equals("睡前")) {
                todaysMedicationBean_ShuiQian.setUsed(itemTodaysMedicationJsonUsed.getUsed());
                todaysMedicationBean_ShuiQian.setPeriod(strPeriod);
                todaysMedicationBean_ShuiQian.setTodaysDate(createDateFormat);
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
                if (obj.getUsed().equals("未服药")) {
                    holder.setImageResource(R.id.btn_take_medicine, R.mipmap.dialog_weifuyao);
                }
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

    // region 数据提交：体征上报

    /**
     * 体征上报
     *
     * @param bedid    病床号
     * @param id       序号
     * @param array_mc 名称
     * @param array_zb 指标
     * @param Xy1      血压1
     * @param Xy2      血压2
     */
    public void post_tzsb(String bedid, String id, ArrayList<String> array_mc, ArrayList<String> array_zb, String Xy1, String Xy2) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder body = new FormBody.Builder();
        body.add("type", "sbadd");
        body.add("bedid", bedid);
        body.add("id", id);
        for (String mc : array_mc) {
            body.add("mc[]", mc);
        }
        for (String zb : array_zb) {
            body.add("zb[]", zb);
        }
        body.add("PatientID", familyBedModelBean.getData().getPatientID());
        body.add("PatientName", familyBedModelBean.getData().getName());
//        body.add("Xy1", Xy1);
//        body.add("Xy2", Xy2);
//        body.add("Inlet", "App");
        Request request = new Request.Builder()
                .url(ServerUrl.base_url_post)
                .post(body.build())
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
    // endregion 数据提交：体征上报

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

    // region 获取数据：体征上报

    /**
     * 根据床号获取体征上报数据，构建页面
     *
     * @param strBedID 床号
     */
    public void getSync(String strBedID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(ServerUrl.get_physical_sign + strBedID)
                        .build();
                Call call = client.newCall(request);
                try {
                    //同步发送请求
                    Response response = call.execute();
                    if (response.isSuccessful()) {
//                        String strResult = response.body().string();

//                        handler.sendEmptyMessage(0);
                        // 创建消息
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        msg.what = ServerUrl.handler_success_physical_sign_get;
                        bundle.putString("strResult", response.body().string());
                        msg.setData(bundle);
                        handler.sendMessage(msg);
//                        System.out.println("text:" + strResult);
//                        System.out.println("请求成功");

                    } else {
//                        System.out.println("请求失败");
                        handler.sendEmptyMessage(ServerUrl.handler_error);
                    }
                } catch (IOException e) {
//                    System.out.println("error");
                    handler.sendEmptyMessage(ServerUrl.handler_error);
                    e.printStackTrace();
                }
            }
        }).start();
    }
    // endregion 获取数据：体征上报

    // region 获取数据：体征上报->每日体征上报

    /**
     * 根据床号获取体征上报数据，构建页面
     *
     * @param strBedID 床号
     */
    public void getMeiRiSb(String strBedID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .get()
                        .url(ServerUrl.get_daily_reporting + strBedID)
                        .build();
                Call call = client.newCall(request);
                try {
                    //同步发送请求
                    Response response = call.execute();
                    if (response.isSuccessful()) {
//                        String strResult = response.body().string();

//                        handler.sendEmptyMessage(0);
                        // 创建消息
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        msg.what = ServerUrl.handler_success_daily_reporting_get;
                        bundle.putString("strResult", response.body().string());
                        msg.setData(bundle);
                        handler.sendMessage(msg);
//                        System.out.println("text:" + strResult);
//                        System.out.println("请求成功");

                    } else {
//                        System.out.println("请求失败");
                        handler.sendEmptyMessage(ServerUrl.handler_error);
                    }
                } catch (IOException e) {
//                    System.out.println("error");
                    handler.sendEmptyMessage(ServerUrl.handler_error);
                    e.printStackTrace();
                }
            }
        }).start();
    }
    // endregion 获取数据：体征上报->每日体征上报

    // region 构建页面：体征上报
    private void initPhysicalSign(PhysicalSignJsonBean physicalSignJsonBean) throws JSONException {

        // 实例化用于存放各个体征上报档案指标的ListView对象
        ListView list_physical_sign = (ListView) view_custom_list.findViewById(R.id.list_physical_sign);

        // 实例化ListView对象的数据源
        ArrayList<PhysicalSignJsonModel> physicalSignJsonModelList = new ArrayList<PhysicalSignJsonModel>();

        // 遍历体征上报档案数据，为ListView对象的数据源填充数据
        for (PhysicalSignJsonModel physicalSignJsonModel : physicalSignJsonBean.getData().getPhysicalSign()) {

            // 把家庭病床数据源中的体征指标字符串转换为体征指标字符串数组
            String[] strArraySbZb = familyBedModelBean.getData().getSbZb().split(",");
            // 遍历体征指标字符串数组
            for (int i = 0; i < strArraySbZb.length; i++) {

                // 体征指标字符串数组中当前索引对应的指标
                String strSbZb = strArraySbZb[i];

                // 判断”当前索引对应的指标“与”征上报档案数据“当前对应的指标名称一致，则为ListView对象的数据源填充数据
                if (strSbZb.equals(physicalSignJsonModel.getName())) {
                    // 为ListView对象的数据源填充数据
                    physicalSignJsonModelList.add(physicalSignJsonModel);
                }
            }
        }

        // 创建ArrayAdapter
        ListViewAdapter<PhysicalSignJsonModel> physicalSignJsonModelAdapter = new ListViewAdapter<PhysicalSignJsonModel>((ArrayList) physicalSignJsonModelList, R.layout.list_item_physical_sign) {
            @Override
            public void bindView(ViewHolder holder, PhysicalSignJsonModel obj) {
                // 设置体征指标名称
                holder.setText(R.id.txt_name, obj.getName());
                // 设置体征指标单位
                holder.setText(R.id.txt_unit, obj.getUnit());
                // 判断如果体征指标名称是”血压“，则对体征指标填写控件进行显示控制
                if (obj.getName().equals("血压")) {
                    // 血压之外的控件隐藏
                    holder.setVisibility(R.id.txt_name_value, View.GONE);
                    holder.setInputType(R.id.txt_name_value_left,InputType.TYPE_CLASS_TEXT);
                    holder.setInputType(R.id.txt_name_value_right,InputType.TYPE_CLASS_TEXT);
                } else {
                    // 血压相关的控件隐藏
                    holder.setVisibility(R.id.txt_name_value_left, View.GONE);
                    holder.setVisibility(R.id.txt_split, View.GONE);
                    holder.setVisibility(R.id.txt_name_value_right, View.GONE);
                    holder.setInputType(R.id.txt_name_value,InputType.TYPE_CLASS_TEXT);
                }
            }
        };
        // 通过调用setAdapter方法为ListView设置Adapter设置适配器
        list_physical_sign.setAdapter(physicalSignJsonModelAdapter);
    }
    // endregion 构建页面：体征上报
}
