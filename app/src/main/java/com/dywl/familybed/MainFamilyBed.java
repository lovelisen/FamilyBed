package com.dywl.familybed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dywl.familybed.adapter.ListViewAdapter;
import com.dywl.familybed.model.FamilyBedModelBean;
import com.dywl.familybed.model.TodaysMedicationBean;
import com.dywl.familybed.model.TodaysMedicationBeanChild;
import com.dywl.familybed.utils.SysExitUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainFamilyBed extends Activity {
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

    private ImageView imageView5;
    private View view_custom;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;

    private GridView grid_today_medication;
    private ListView list_today_medication_child;
    private ListViewAdapter<TodaysMedicationBean> todaysMedicationAdapter = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild1 = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild2 = null;
    private List<TodaysMedicationBean> dataTodaysMedicationBean = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild1 = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familybedmain); //

        SysExitUtil.activityList.add(MainFamilyBed.this);

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
        imageView5 = findViewById(R.id.imageView5);

        Intent intent = super.getIntent();
//        FamilyBedModelBean familyBedModelBean = intent.getSerializableExtra("familyBedModelBean", FamilyBedModelBean.class);
        FamilyBedModelBean familyBedModelBean = (FamilyBedModelBean) intent.getSerializableExtra("familyBedModelBean");

        textView1.setText(familyBedModelBean.getData().getName() + "(" +familyBedModelBean.getData().getSex()+ ")");
        textView2.setText("病种：" +familyBedModelBean.getData().getDisease());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date createDate = sdf.parse(familyBedModelBean.getData().getCreateTime());
            String createDateFormat = sdf.format(createDate);
            textView3.setText("建床时间：" + createDateFormat);
            // 获取当前时间
            Calendar cal = Calendar.getInstance();
            Date nowDate = cal.getTime();
            int dateBett = daysBetween(createDate, nowDate);
            if(dateBett==0)
            {
                dateBett =1;
            }
            textView4.setText("已建床"+dateBett+"天");
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }

        textView5.setText("主管医生：" + familyBedModelBean.getData().getDoctor());
        textView7.setText("责任护士：" + familyBedModelBean.getData().getNurse());
        textView6.setText("住院号：" +familyBedModelBean.getData().getNumber());
        textView8.setText("科室：" +familyBedModelBean.getData().getDepartment());
        textView9.setText("饮食推荐：" +familyBedModelBean.getData().getDiet());
        textView10.setText("护理等级：" +familyBedModelBean.getData().getNurseLV());



        mContext = MainFamilyBed.this;
        //初始化Builder
        builder = new AlertDialog.Builder(mContext);
//        builder = new AlertDialog.Builder(mContext,R.style.styletest);
//        builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
//        builder = new AlertDialog.Builder(mContext, androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder = new AlertDialog.Builder(mContext, com.google.android.material.R.style.Theme_Material3_Light_Dialog_Alert);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = MainFamilyBed.this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.activity_demo_dialog_gridview, null, false);
        builder.setView(view_custom);
        builder.setCancelable(false);
        alert = builder.setTitle("今日用药提醒").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        }).create();
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        alert.getWindow().getAttributes().dimAmount = 0f;
//        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        init();

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });
    }

    private void init() {

        grid_today_medication = (GridView) view_custom.findViewById(R.id.grid_today_medication);
        list_today_medication_child = (ListView) view_custom.findViewById(R.id.list_today_medication_child);

        dataTodaysMedicationBean = new ArrayList<TodaysMedicationBean>();
        dataTodaysMedicationBeanChild = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild1 = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild2 = new ArrayList<TodaysMedicationBeanChild>();
        TodaysMedicationBeanChild todaysMedicationBeanChild = new TodaysMedicationBeanChild();
        TodaysMedicationBeanChild todaysMedicationBeanChild1 = new TodaysMedicationBeanChild();
        TodaysMedicationBeanChild todaysMedicationBeanChild2 = new TodaysMedicationBeanChild();
        todaysMedicationBeanChild.setDrug("阿司匹林肠溶片");
        todaysMedicationBeanChild.setUsage("口服");
        todaysMedicationBeanChild.setFrequency("一天两次");
        todaysMedicationBeanChild.setNote("一粒");
        todaysMedicationBeanChild.setEntrust("饭后");
        todaysMedicationBeanChild1.setDrug("阿托伐他汀钙片");
        todaysMedicationBeanChild1.setUsage("口服");
        todaysMedicationBeanChild1.setFrequency("一天两次");
        todaysMedicationBeanChild1.setNote("两片");
        todaysMedicationBeanChild1.setEntrust(" ");
        todaysMedicationBeanChild2.setDrug("二甲双胍");
        todaysMedicationBeanChild2.setUsage("口服");
        todaysMedicationBeanChild2.setFrequency("一天三次");
        todaysMedicationBeanChild2.setNote("0.5mg");
        todaysMedicationBeanChild2.setEntrust("饭前半小时");
        dataTodaysMedicationBeanChild.add(todaysMedicationBeanChild);
        dataTodaysMedicationBeanChild.add(todaysMedicationBeanChild1);
        dataTodaysMedicationBeanChild.add(todaysMedicationBeanChild2);

        dataTodaysMedicationBeanChild1.add(todaysMedicationBeanChild);
        dataTodaysMedicationBeanChild2.add(todaysMedicationBeanChild1);

        todaysMedicationAdapterChild = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild, R.layout.list_item_todays_medication_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage()+"/"+obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托："+obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild1 = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild1, R.layout.list_item_todays_medication_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage()+"/"+obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托："+obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild2 = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild2, R.layout.list_item_todays_medication_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage()+"/"+obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, "嘱托："+obj.getEntrust());
            }
        };


        TodaysMedicationBean todaysMedicationBean = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean1 = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean2 = new TodaysMedicationBean();
        todaysMedicationBean.setTodaysDate("2023-6-14");
        todaysMedicationBean.setPeriod("早上用药");
        todaysMedicationBean1.setTodaysDate("2023-6-14");
        todaysMedicationBean1.setPeriod("中午用药");
        todaysMedicationBean2.setTodaysDate("2023-6-14");
        todaysMedicationBean2.setPeriod("晚上用药");
        todaysMedicationBean.setAdapter(todaysMedicationAdapterChild);
        todaysMedicationBean1.setAdapter(todaysMedicationAdapterChild1);
        todaysMedicationBean2.setAdapter(todaysMedicationAdapterChild2);

        dataTodaysMedicationBean.add(todaysMedicationBean);
        dataTodaysMedicationBean.add(todaysMedicationBean1);
        dataTodaysMedicationBean.add(todaysMedicationBean2);

        todaysMedicationAdapter = new ListViewAdapter<TodaysMedicationBean>((ArrayList) dataTodaysMedicationBean, R.layout.list_item_todays_medication) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBean obj) {
                holder.setText(R.id.txt_today_date, obj.getTodaysDate());
                holder.setText(R.id.txt_period, obj.getPeriod());
                holder.setAdapter(R.id.list_today_medication_child,obj.getAdapter());
                holder.setTag(R.id.btn_take_medicine,obj.getTodaysDate()+obj.getPeriod());
                holder.setOnClickListener(R.id.btn_take_medicine,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), v.getTag().toString(), Toast.LENGTH_SHORT).show();
//                        alert.dismiss();
                    }
                });
            }
        };

        //ListView设置Adapter：
        grid_today_medication.setAdapter(todaysMedicationAdapter);
    }
    public void clickFimilyBed(View v) {
        Intent intent;
        if (v.getTag().toString().equals("tagViewPlan")) {
            Toast.makeText(getApplicationContext(), "查看诊疗计划", Toast.LENGTH_LONG).show();
//            intent = new Intent(MainFamilyBed.this,
//                    UserlistActivity.class);
////            intent.putExtra("LoginUserID", strLoginUserID);// 需要给UserInfoSetActivity传递的参数
//            startActivity(intent);
        } else if (v.getTag().toString().equals("tagExit")) {
            Toast.makeText(getApplicationContext(), "退出", Toast.LENGTH_LONG).show();
            intent = new Intent(MainFamilyBed.this,
                    LoginActivity.class);
//            intent.putExtra("LoginUserID", strLoginUserID);// 需要给UserInfoSetActivity传递的参数
//            intent.putExtra("UserPosition",
//                    strUserPosition);// 需要给UserInfoSetActivity传递的参数
            startActivity(intent);
        }
    }

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
}
