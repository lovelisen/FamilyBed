package com.dywl.familybed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dywl.familybed.model.FamilyBedModelBean;
import com.dywl.familybed.model.TodaysMedicationJsonBean;
import com.dywl.familybed.utils.SysExitUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    // 在线咨询
    private ImageView imageView7;
    private View view_custom;
    private View view_custom_list;
    private AlertDialog.Builder builder_list = null;
    private AlertDialog alert = null;
    private AlertDialog alert_list = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;

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
        imageView7 = findViewById(R.id.imageView7);

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
//        //初始化Builder
//        builder = new AlertDialog.Builder(mContext);
//        builder_list = new AlertDialog.Builder(mContext);
////        builder = new AlertDialog.Builder(mContext,R.style.styletest);
////        builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
////        builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
//        builder = new AlertDialog.Builder(mContext, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);
//        builder_list = new AlertDialog.Builder(mContext, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);
//
//        //加载自定义的那个View,同时设置下
//        final LayoutInflater inflater = MainFamilyBed.this.getLayoutInflater();
//        final LayoutInflater inflater_list = MainFamilyBed.this.getLayoutInflater();
//        view_custom = inflater.inflate(R.layout.activity_demo_dialog_gridview, null, false);
//        view_custom_list = inflater_list.inflate(R.layout.activity_demo_dialog_listview, null, false);
//        builder.setView(view_custom);
//        builder_list.setView(view_custom_list);
//        builder.setCancelable(false);
//        builder_list.setCancelable(false);
//
//        alert = builder.create();
//        alert_list = builder_list.create();
//
////        TextView txt_name_value = (TextView) view_custom_list.findViewById(R.id.txt_name_value);
////        alert_list.setOnShowListener(new DialogInterface.OnShowListener() {
////            @Override
////            public void onShow(DialogInterface dialog) {
////                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                imm.showSoftInput(txt_name_value, InputMethodManager.SHOW_IMPLICIT);
////
////            }
////        });
//
////        alert = builder.setTitle("今日用药提醒").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
//////                Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
////                alert.dismiss();
////            }
////        }).create();
////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
////        alert_list.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
////        alert_list.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
////                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
////        alert_list.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//
//////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
////        alert.getWindow().getAttributes().dimAmount = 0f;
////        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        view_custom.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert.dismiss();
//            }
//        });
//        view_custom_list.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert_list.dismiss();
//            }
//        });
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

                Intent intent = new Intent(MainFamilyBed.this, TodayMedicationActivity.class);
                startActivity(intent);
            }
        });
        // endregion 点击事件：弹出页面-今日用药

        // region 点击事件：弹出页面-今日用药
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainFamilyBed.this, VideoChatViewActivity.class);
                startActivity(intent);
            }
        });
        // endregion 点击事件：弹出页面-今日用药

    }
    // endregion 实现onCreate抽象方法

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
}
