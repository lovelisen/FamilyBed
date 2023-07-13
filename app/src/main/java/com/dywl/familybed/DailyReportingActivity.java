package com.dywl.familybed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.dywl.familybed.utils.JSONHelper;
import com.dywl.familybed.utils.ToastUtil;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DailyReportingActivity extends BaseActivity {

    //region 初始化今日体征上报页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 设置为全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.daily_reporting_activity);//创建客户经理主页面



        // region 按钮事件：返回
        ImageView ac_back_icon = (ImageView) findViewById(R.id.ac_back_icon);//实例化图片设置图片控件
        ac_back_icon.setOnClickListener(new View.OnClickListener() {//添加点击事件
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(DailyReportingActivity.this, MainFamilyBed.class);//点击跳转到设置窗口
//                startActivity(intent);
                finish();
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

                Toast.makeText(getApplicationContext(), "系统提示：功能建设中……", Toast.LENGTH_LONG).show();
            }
        });
        // endregion 按钮事件：查看历史

        // region 按钮事件：上报
        Button btn_save_physical_sign = (Button) findViewById(R.id.btn_save_physical_sign);//实例化图片设置图片控件
        //添加点击事件
        btn_save_physical_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ListView list_today_physical_sign = (ListView) findViewById(R.id.list_physical_sign);
                int listChildCount = list_today_physical_sign.getChildCount();

                String bedid = MyApp.getFamilyBedModelBean().getData().getID();
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
                    String text_name_text = txt_name.getText().toString();
                    array_mc.add(text_name_text);
                    if (text_name_text.equals("血压")) {
                        // 高压值
                        Xy1 = txt_name_value_left.getText().toString();
                        // 低压值
                        Xy2 = txt_name_value_right.getText().toString();

                        if (TextUtils.isEmpty(Xy1)) {
                            ToastUtil.showToast("请输入血压-收缩压！");
                            return;
                        }
                        if (TextUtils.isEmpty(Xy2)) {
                            ToastUtil.showToast("请输入血压-舒张压！");
                            return;
                        }

                        // 指标值
                        array_zb.add(Xy1 + "/" + Xy2);

                    } else {
                        // 指标值
                        String text_value = txt_name_value.getText().toString();
                        if (TextUtils.isEmpty(text_value)) {
                            ToastUtil.showToast("请输入"+text_name_text+"！");
                            return;
                        }
                        array_zb.add(text_value);

                    }

                }

                post_tzsb(bedid, id, array_mc, array_zb, Xy1, Xy2);

                finish();

            }
        });
        // endregion 按钮事件：上报


        getSync(MyApp.getFamilyBedModelBean().getData().getID());
        getMeiRiSb(MyApp.getFamilyBedModelBean().getData().getID());

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
                    ToastUtil.showToast("响应成功。");
                    break;
                case ServerUrl.handler_error:
//                    tv_result.setText("登录失败，请重试。");
                    ToastUtil.showToast("失败，请重试。");
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
                    break;
                case ServerUrl.handler_success_daily_reporting_get:
                    try {
                        String strResult = message.getData().getString("strResult");

                        int count_sb_num = Integer.parseInt(MyApp.getFamilyBedModelBean().getData().getSbNum());
                        int set_sb_time = Integer.parseInt(MyApp.getFamilyBedModelBean().getData().getSbZq());

                        TextView tv_physical_sign_count = (TextView) findViewById(R.id.tv_physical_sign_count);
                        Button btn_save_physical_sign = (Button) findViewById(R.id.btn_save_physical_sign);
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
//                            btn_save_physical_sign.setVisibility(View.GONE);
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

    // region 获取数据：体征上报，用于构建页面

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
//
////                        handler.sendEmptyMessage(0);
//
//                        try {
////                        String strResult = message.getData().getString("strResult");
//
//                            PhysicalSignJsonBean physicalSignJsonBean = JSONHelper.parseObject(strResult, PhysicalSignJsonBean.class);
//                            initPhysicalSign(physicalSignJsonBean);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }

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
    // endregion 获取数据：体征上报，用于构建页面

    // region 获取数据：体征上报->每日体征上报，用于构建页面，提示信息

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
    // endregion 获取数据：体征上报->每日体征上报，用于构建页面，提示信息

    // region 构建页面：体征上报
    private void initPhysicalSign(PhysicalSignJsonBean physicalSignJsonBean) throws JSONException {

        // 实例化用于存放各个体征上报档案指标的ListView对象
        ListView list_physical_sign = (ListView) findViewById(R.id.list_physical_sign);

        // 实例化ListView对象的数据源
        ArrayList<PhysicalSignJsonModel> physicalSignJsonModelList = new ArrayList<PhysicalSignJsonModel>();

        // 遍历体征上报档案数据，为ListView对象的数据源填充数据
        for (PhysicalSignJsonModel physicalSignJsonModel : physicalSignJsonBean.getData().getPhysicalSign()) {

            // 把家庭病床数据源中的体征指标字符串转换为体征指标字符串数组
            String[] strArraySbZb = MyApp.getFamilyBedModelBean().getData().getSbZb().split(",");
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
        ListViewAdapter<PhysicalSignJsonModel> physicalSignJsonModelAdapter = new ListViewAdapter<PhysicalSignJsonModel>((ArrayList) physicalSignJsonModelList, R.layout.daily_reporting_list_item) {
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
//                    holder.setInputType(R.id.txt_name_value_left,InputType.TYPE_CLASS_TEXT);
//                    holder.setInputType(R.id.txt_name_value_right,InputType.TYPE_CLASS_TEXT);
                } else {
                    // 血压相关的控件隐藏
                    holder.setVisibility(R.id.txt_name_value_left, View.GONE);
                    holder.setVisibility(R.id.txt_split, View.GONE);
                    holder.setVisibility(R.id.txt_name_value_right, View.GONE);
//                    holder.setInputType(R.id.txt_name_value,InputType.TYPE_CLASS_TEXT);
                }
            }
        };
        // 通过调用setAdapter方法为ListView设置Adapter设置适配器
        list_physical_sign.setAdapter(physicalSignJsonModelAdapter);
    }
    // endregion 构建页面：体征上报

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
        body.add("type", "sbaddNew");
        body.add("bedid", bedid);
        body.add("id", id);
        for (String mc : array_mc) {
            body.add("mc[]", mc);
        }
        for (String zb : array_zb) {
            body.add("zb[]", zb);
        }
        body.add("PatientID", MyApp.getFamilyBedModelBean().getData().getPatientID());
        body.add("PatientName", MyApp.getFamilyBedModelBean().getData().getName());
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
}
