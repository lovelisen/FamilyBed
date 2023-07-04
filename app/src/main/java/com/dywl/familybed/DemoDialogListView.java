package com.dywl.familybed;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dywl.familybed.adapter.ListViewAdapter;
import com.dywl.familybed.model.TodaysMedicationBean;
import com.dywl.familybed.model.TodaysMedicationBeanChild;

import java.util.ArrayList;
import java.util.List;

public class DemoDialogListView extends AppCompatActivity {

    private Button btn_show;
    private View view_custom;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context mContext;
    private ListView list_today_medication;
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
        setContentView(R.layout.activity_demo_dialog);
        mContext = DemoDialogListView.this;


        btn_show = (Button) findViewById(R.id.btn_show);

        //初始化Builder
        builder = new AlertDialog.Builder(mContext);

        //加载自定义的那个View,同时设置下
        final LayoutInflater inflater = DemoDialogListView.this.getLayoutInflater();
        view_custom = inflater.inflate(R.layout.activity_demo_dialog_listview, null, false);
        builder.setView(view_custom);
        builder.setCancelable(false);
        alert = builder.create();

        init();


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

    private void init() {

        list_today_medication = (ListView) view_custom.findViewById(R.id.list_physical_sign);
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

        todaysMedicationAdapterChild = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage()+"/"+obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild1 = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild1, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage()+"/"+obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, obj.getEntrust());
            }
        };

        todaysMedicationAdapterChild2 = new ListViewAdapter<TodaysMedicationBeanChild>((ArrayList) dataTodaysMedicationBeanChild2, R.layout.today_medication_list_item_child) {
            @Override
            public void bindView(ViewHolder holder, TodaysMedicationBeanChild obj) {
                holder.setText(R.id.txt_drug, obj.getDrug());
                holder.setText(R.id.txt_usage, obj.getUsage()+"/"+obj.getNote());
//                holder.setText(R.id.txt_frequency, obj.getFrequency());
//                holder.setText(R.id.txt_note, obj.getNote());
                holder.setText(R.id.txt_entrust, obj.getEntrust());
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

        todaysMedicationAdapter = new ListViewAdapter<TodaysMedicationBean>((ArrayList) dataTodaysMedicationBean, R.layout.today_medication_list_item) {
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
        list_today_medication.setAdapter(todaysMedicationAdapter);
    }
}