package com.dywl.familybed;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dywl.familybed.adapter.ListViewAdapter;
import com.dywl.familybed.model.TodaysMedicationBean;
import com.dywl.familybed.model.TodaysMedicationBeanChild;
import com.dywl.familybed.model.TodaysMedicationJsonBean;
import com.dywl.familybed.model.TodaysMedicationJsonData;
import com.dywl.familybed.model.TodaysMedicationJsonDrug;
import com.dywl.familybed.model.TodaysMedicationJsonUsed;
import com.dywl.familybed.utils.JSONHelper;
import com.dywl.familybed.utils.WebTool;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DemoDialogGridView extends AppCompatActivity {

    private Button btn_show;
    private View view_custom;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;
    private GridView grid_today_medication;
    private BaseAdapter mAdapter = null;
    private ListView list_today_medication_child;
    private ListViewAdapter<TodaysMedicationBean> todaysMedicationAdapter = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ZaoShang = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ZhongWu = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_WanShang = null;
    private ListViewAdapter<TodaysMedicationBeanChild> todaysMedicationAdapterChild_ShuiQian = null;
    private List<TodaysMedicationBean> dataTodaysMedicationBean = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZaoShang = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ZhongWu = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_WanShang = null;
    private List<TodaysMedicationBeanChild> dataTodaysMedicationBeanChild_ShuiQian = null;


    private String result;
    TodaysMedicationJsonBean todaysMedicationJsonBean = null;
    TodaysMedicationJsonData todaysMedicationJsonData = null;
    List<TodaysMedicationJsonDrug> listTodaysMedicationJsonDrug = null;
    List<TodaysMedicationJsonUsed> listTodaysMedicationJsonUsed = null;
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
        setContentView(R.layout.activity_demo_dialog);
        btn_show = (Button) findViewById(R.id.btn_show);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //在新线程中获取数据，通过handler判断是否获取到数据
                    //n为向PHP传输的数据
                    String n = "本地测试数据";
                    result = WebTool.singleData("http://open.dywyhs.com/project/familybed/manage/api/api_padLogin.php?func=getDrug&BedID=1");

                    if (!result.equals("failed")) {

                        handler.sendEmptyMessage(0);

                        if (result.contains("成功")) {
                            todaysMedicationJsonBean = JSONHelper.parseObject(result, TodaysMedicationJsonBean.class);

                            init();
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


        mContext = DemoDialogGridView.this;
        //初始化Builder
        builder = new AlertDialog.Builder(mContext);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = DemoDialogGridView.this.getLayoutInflater();
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


//        view_custom.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "对话框已关闭~", Toast.LENGTH_SHORT).show();
//                alert.dismiss();
//            }
//        });
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();
            }
        });

    }

    private void init() throws JSONException {

        todaysMedicationJsonData = todaysMedicationJsonBean.getData();
        listTodaysMedicationJsonDrug = todaysMedicationJsonData.getDrug();
        listTodaysMedicationJsonUsed = todaysMedicationJsonData.getUsed();

        dataTodaysMedicationBean = new ArrayList<TodaysMedicationBean>();
        dataTodaysMedicationBeanChild_ZaoShang = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild_ZhongWu = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild_WanShang = new ArrayList<TodaysMedicationBeanChild>();
        dataTodaysMedicationBeanChild_ShuiQian = new ArrayList<TodaysMedicationBeanChild>();

        TodaysMedicationBean todaysMedicationBean_ZaoShang = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean_ZhongWu = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean_WanShang = new TodaysMedicationBean();
        TodaysMedicationBean todaysMedicationBean_ShuiQian = new TodaysMedicationBean();


        for (TodaysMedicationJsonDrug itemTodaysMedicationJsonDrug : todaysMedicationJsonData.getDrug()) {

//            String[] strPeriodList = itemTodaysMedicationJsonDrug.getPeriod().split(",");
//            char[] strPeriodList = itemTodaysMedicationJsonDrug.getPeriod().toCharArray();
            JSONArray array = new JSONArray(itemTodaysMedicationJsonDrug.getPeriod());

            for (int i = 0; i < array.length(); i++) {
                
                String strPeriod= array.get(i).toString();

                if (strPeriod.equals("早上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZaoShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZaoShang.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ZaoShang.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ZaoShang.setNote(itemTodaysMedicationJsonDrug.getNote());


                        todaysMedicationBeanChild_ZaoShang.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());


                    dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_ZaoShang);
                } else if (strPeriod.equals("中午")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ZhongWu = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ZhongWu.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ZhongWu.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ZhongWu.setNote(itemTodaysMedicationJsonDrug.getNote());


                        todaysMedicationBeanChild_ZhongWu.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());


                    dataTodaysMedicationBeanChild_ZhongWu.add(todaysMedicationBeanChild_ZhongWu);
                } else if (strPeriod.equals("晚上")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_WanShang = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_WanShang.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_WanShang.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_WanShang.setNote(itemTodaysMedicationJsonDrug.getNote());



                        todaysMedicationBeanChild_WanShang.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());


                    dataTodaysMedicationBeanChild_WanShang.add(todaysMedicationBeanChild_WanShang);

                } else if (strPeriod.equals("睡前")) {
                    TodaysMedicationBeanChild todaysMedicationBeanChild_ShuiQian = new TodaysMedicationBeanChild();
                    todaysMedicationBeanChild_ShuiQian.setDrug(itemTodaysMedicationJsonDrug.getDrug());
                    todaysMedicationBeanChild_ShuiQian.setUsage(itemTodaysMedicationJsonDrug.getUsage());
                    todaysMedicationBeanChild_ShuiQian.setNote(itemTodaysMedicationJsonDrug.getNote());


                        todaysMedicationBeanChild_ShuiQian.setEntrust(itemTodaysMedicationJsonDrug.getEntrust());


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


        grid_today_medication = (GridView) view_custom.findViewById(R.id.grid_today_medication);
        list_today_medication_child = (ListView) view_custom.findViewById(R.id.list_today_medication_child);
//
//        todaysMedicationBeanChild_ZaoShang.setDrug("阿司匹林肠溶片");
//        todaysMedicationBeanChild_ZaoShang.setUsage("口服");
//        todaysMedicationBeanChild_ZaoShang.setFrequency("一天两次");
//        todaysMedicationBeanChild_ZaoShang.setNote("一粒");
//        todaysMedicationBeanChild_ZaoShang.setEntrust("饭后");
//        todaysMedicationBeanChild_ZhongWu.setDrug("阿托伐他汀钙片");
//        todaysMedicationBeanChild_ZhongWu.setUsage("口服");
//        todaysMedicationBeanChild_ZhongWu.setFrequency("一天两次");
//        todaysMedicationBeanChild_ZhongWu.setNote("两片");
//        todaysMedicationBeanChild_ZhongWu.setEntrust(" ");
//        todaysMedicationBeanChild_WanShang.setDrug("二甲双胍");
//        todaysMedicationBeanChild_WanShang.setUsage("口服");
//        todaysMedicationBeanChild_WanShang.setFrequency("一天三次");
//        todaysMedicationBeanChild_WanShang.setNote("0.5mg");
//        todaysMedicationBeanChild_WanShang.setEntrust("饭前半小时");
//        dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_ZaoShang);
//        dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_ZhongWu);
//        dataTodaysMedicationBeanChild_ZaoShang.add(todaysMedicationBeanChild_WanShang);
//
//        dataTodaysMedicationBeanChild_ZhongWu.add(todaysMedicationBeanChild_ZaoShang);
//        dataTodaysMedicationBeanChild_WanShang.add(todaysMedicationBeanChild_ZhongWu);

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

//
//        todaysMedicationBean_ZaoShang.setTodaysDate("2023-6-14");
//        todaysMedicationBean_ZaoShang.setPeriod("早上用药");
//        todaysMedicationBean_ZhongWu.setTodaysDate("2023-6-14");
//        todaysMedicationBean_ZhongWu.setPeriod("中午用药");
//        todaysMedicationBean_WanShang.setTodaysDate("2023-6-14");
//        todaysMedicationBean_WanShang.setPeriod("晚上用药");
        todaysMedicationBean_ZaoShang.setAdapter(todaysMedicationAdapterChild_ZaoShang);
        todaysMedicationBean_ZhongWu.setAdapter(todaysMedicationAdapterChild_ZhongWu);
        todaysMedicationBean_WanShang.setAdapter(todaysMedicationAdapterChild_WanShang);
        todaysMedicationBean_ShuiQian.setAdapter(todaysMedicationAdapterChild_ShuiQian);

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
                holder.setTag(R.id.btn_take_medicine, obj.getTodaysDate() + obj.getPeriod());
                holder.setOnClickListener(R.id.btn_take_medicine, new View.OnClickListener() {
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
}