package com.dywl.familybed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dywl.familybed.adapter.ListViewAdapter;
import com.dywl.familybed.model.FamilyBedModelBean;
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

    private ImageView imageView5;
    private View view_custom;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;

//    private ListViewAdapter<TodaysMedicationBean> todaysMedicationAdapter = null;
//    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ZaoShang = null;
//    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ZhongWu = null;
//    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_WanShang = null;
//    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ShuiQian = null;
//    private List<TodaysMedicationBean> dataTodaysMedicationBean = null;
//    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZaoShang = null;
//    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZhongWu = null;
//    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_WanShang = null;
//    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ShuiQian = null;


    private List<TodaysMedicationJsonDrug> listTodaysMedicationJsonDrug = null;
    private List<TodaysMedicationJsonUsed> listTodaysMedicationJsonUsed = null;
    private String url_post="http://open.dywyhs.com/project/familybed/wx/wx_AjaxInfo.php";

    /**
     *
     */
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
//                    Toast.makeText(getApplicationContext(), "成功，跳转。", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "今日用药", Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familybedmain); //

        SysExitUtil.activityList.add(MainFamilyBed.this);

        Intent intent = super.getIntent();
        familyBedModelBean = (FamilyBedModelBean) intent.getSerializableExtra("familyBedModelBean");

        // region 患者基本信息模块
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
        // endregion

        // region 今日用药弹出框模块
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

        alert = builder.create();

        view_custom.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
//        alert = builder.setTitle("今日用药提醒").setPositiveButton("关闭", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
//                alert.dismiss();
//            }
//        }).create();
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
////        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        alert.getWindow().getAttributes().dimAmount = 0f;
//        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // endregion

//        // 初始化今日用药数据
//        getTodaysMedicationData();

        // 今日用药点击事件
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTodaysMedicationData();

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                alert.show();
            }
        });
    }

    private void getTodaysMedicationData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在新线程中获取数据，通过handler判断是否获取到数据
                    String result = WebTool.singleData("http://open.dywyhs.com/project/familybed/manage/api/api_padLogin.php?func=getDrug&BedID=1");

                    if (!result.equals("failed")) {

                        handler.sendEmptyMessage(0);

                        if (result.contains("成功")) {
                            TodaysMedicationJsonBean todaysMedicationJsonBean = JSONHelper.parseObject(result, TodaysMedicationJsonBean.class);


                            init(todaysMedicationJsonBean);
                        } else {

                            handler.sendEmptyMessage(-1);
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
        List<TodaysMedicationBean> dataTodaysMedicationBean = null;
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZaoShang = null;
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZhongWu = null;
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_WanShang = null;
        List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ShuiQian = null;

        dataTodaysMedicationBean = new ArrayList<TodaysMedicationBean>();
        dataTodaysMedicationBeanChild_ZaoShang = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild_ZhongWu = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild_WanShang = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild_ShuiQian = new ArrayList<TodaysMedicationBeanChild>();

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

                if (strPeriod.equals("早上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZaoShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZaoShang.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ZaoShang.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ZaoShang.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_ZaoShang.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_ZaoShang +=itemTodaysMedicationJsonDrug.getID()+",";

                    dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_ZaoShang);
                } else if (strPeriod.equals("中午")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZhongWu = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZhongWu.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ZhongWu.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ZhongWu.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_ZhongWu.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_ZhongWu +=itemTodaysMedicationJsonDrug.getID()+",";

                    dataTodaysMedicationBeanChild_ZhongWu.add(todaysMedicationBeanChild_ZhongWu);
                } else if (strPeriod.equals("晚上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_WanShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_WanShang.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_WanShang.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_WanShang.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_WanShang.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_WanShang +=itemTodaysMedicationJsonDrug.getID()+",";

                    dataTodaysMedicationBeanChild_WanShang.add(todaysMedicationBeanChild_WanShang);

                } else if (strPeriod.equals("睡前")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ShuiQian = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ShuiQian.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ShuiQian.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ShuiQian.setNote(itemTodaysMedicationJsonDrug.getNote());
                    todaysMedicationBeanChild_ShuiQian.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());

                    ids_ShuiQian +=itemTodaysMedicationJsonDrug.getID()+",";

                    dataTodaysMedicationBeanChild_ShuiQian.add(todaysMedicationBeanChild_ShuiQian);
                }

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

        if(ids_ZaoShang.substring(ids_ZaoShang.length()-1).equals(",")){
            ids_ZaoShang = ids_ZaoShang.substring(0,ids_ZaoShang.length() - 1);
        }
        if(ids_ZhongWu.substring(ids_ZhongWu.length()-1).equals(",")){
            ids_ZhongWu = ids_ZhongWu.substring(0,ids_ZhongWu.length() - 1);
        }
        if(ids_WanShang.substring(ids_WanShang.length()-1).equals(",")){
            ids_WanShang = ids_WanShang.substring(0,ids_WanShang.length() - 1);
        }
        if(ids_ShuiQian.substring(ids_ShuiQian.length()-1).equals(",")){
            ids_ShuiQian = ids_ShuiQian.substring(0,ids_ShuiQian.length() - 1);
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
                holder.setTag(R.id.btn_take_medicine,  familyBedModelBean.getData().getPatientID()+"!"+familyBedModelBean.getData().getID()+"!"+obj.getPeriod()+"!"+obj.getIds());
                if(obj.getUsed().equals("未服药")){
                    holder.setImageResource(R.id.btn_take_medicine,R.mipmap.dialog_weifuyao);
                }
                holder.setOnClickListener(R.id.btn_take_medicine, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] split = v.getTag().toString().split("!");
//                        Toast.makeText(getApplicationContext(), "床号："+split[1]+"    患者编号："+split[0]+"    时段："+split[2]+"    IDS："+split[3], Toast.LENGTH_SHORT).show();
                        // 调用已服药接口
                        post(split[2],split[3]);
                        alert.dismiss();
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


    public void post(String sd,String ids) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("type", "fyjl")
                .add("ids", ids)
                .add("sd", sd)
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
                System.out.println("响应失败！");
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
