package com.dywl.familybed;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.dywl.familybed.model.FamilyBedModelBean;
import com.dywl.familybed.model.RtcEngineConfigJsonBean;
import com.dywl.familybed.utils.ContextUitls;
import com.dywl.familybed.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MyApp  extends MultiDexApplication {

    //region 声明变量及类型
    public static Context instance;
    public static volatile MyApp app;


    public static FamilyBedModelBean getFamilyBedModelBean() {
        return familyBedModelBean;
    }

    public static void setFamilyBedModelBean(FamilyBedModelBean familyBedModelBean) {
        MyApp.familyBedModelBean = familyBedModelBean;
    }

    private static FamilyBedModelBean familyBedModelBean;


    public static RtcEngineConfigJsonBean getRtcEngineConfigJsonBean() {
        return rtcEngineConfigJsonBean;
    }

    public static void setRtcEngineConfigJsonBean(RtcEngineConfigJsonBean rtcEngineConfigJsonBean) {
        MyApp.rtcEngineConfigJsonBean = rtcEngineConfigJsonBean;
    }

    private static RtcEngineConfigJsonBean rtcEngineConfigJsonBean;

    private static DisplayImageOptions options;
    public static String spName = "LoanSP";

    public static int MainPageSize = 10;//每页显示条数

    public static int initYear = 2018;

    public static String urlSignature = "dywl";
    public static String urlSignaturePass = "dy12345";

    //百度OCR相关验证
    public static String baiduOcrAppKey = "UKeZuPVwcgOED38DbeV1od08";
    public static String baiduOcrAppSecret = "fUQxGRqgpxbC13dHq4VBWmqrxO8ncbez";
    //endregion

    //region 加载图片、主页面上下文、百度地图接口初始化等方法
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        instance = getApplicationContext();
        ContextUitls.init(this);
        ImageLoaderUtil.configImageLoader(this);
//        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
//        SDKInitializer.initialize(this);
//        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
//        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标
//        SDKInitializer.setCoordType(CoordType.BD09LL);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.icon_common_default_head)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.icon_common_default_head)// 设置图片加载/解码过程中错误时候显示的图片
                .showImageOnLoading(R.mipmap.icon_common_default_head)// 设置下载的时候显示的图片
                .displayer(new FadeInBitmapDisplayer(300))//是否图片加载好后渐入的动画时间，可能会出现闪动
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .build();//构建完成
    }
    //endregion

    //region 配置一个DisplayImageOptions对象来作为ImageLoader.getInstance().displayImage（）中的参数
    public static DisplayImageOptions getOptions() {
        return options;
    }
    //endregion

    //region 获取登录用户信息
    //获取用户ID
    public static String getUserID(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("User_ID","");
    }
    //用户姓名
    public static String getUserName(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("User_Name","");
    }
    //角色ID
    public static String getRole_ID(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Role_ID","");
    }
    //岗位名称
    public static String getRole_Name(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Role_Name","");
    }
    //部门id
    public static String getOrg_ID(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_ID","");
    }
    //办理部门id
    public static String getOrg_Name(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_Name","");
    }
    //标记："1"客户经理，"2"审核，"3"管理员，"4"支付 ，"5"超级管理员,"6"征信查询岗
    public static String getFlag(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Flag","");
    }
    //管理层次：0：总行，1：支行 string类型
    public static String getOrg_Jnum(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_Jnum","");
    }
    //ClientId：对应返回结果通知透传时需要的参数
    public static String getClientId(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("clientId","");
    }
    //公告弹出时间
    public static String getAlertNoticeTime(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("alertNoticeTime","");
    }
    //是否可以提交
    public static String getSumit(){
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Sumit","");
    }

    //Header
    public static String getAgency_code() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Agency_code", "YX");
    }

    public static String getKey() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Key", "test");
    }

    public static String getOrg_code() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_code", "YX");
    }

    public static String getOrg_pwd() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_pwd", "nsh20200219!GYSF");
    }

    public static String getOrg_req_no() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_req_no", "f1fe2593-f5c7-44aa-9fd5-6fc7613b6270");
    }

    public static String getOrg_user() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Org_user", "YX");
    }

    public static String getService_code() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Service_code", "Service_code");
    }
    public static String getSerial_time() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Serial_time", "Serial_time");
    }
    public static String getSerial_no() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Serial_no", "Serial_no");
    }
    public static String getRequest_time() {
        SharedPreferences sp = instance.getSharedPreferences(MyApp.spName, Context.MODE_PRIVATE);
        return sp.getString("Request_time", "Request_time");
    }
    //endregion
}
