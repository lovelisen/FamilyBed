package com.dywl.familybed;

public class ServerUrl {

    // region 服务器地址:百度
    //百度token
    public static final String BAIDU_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    //百度OCR身份证认证
    public static final String BAIDU_IDCARD = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
    //行驶证识别
    public static final String BAIDU_VEHICLE_LICENSE = "https://aip.baidubce.com/rest/2.0/ocr/v1/vehicle_license";
    //车牌识别
    public static final String BAIDU_LICENSE_PLATE = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
    //营业执照识别
    public static final String BAIDU_BUSINESS_LICENSE = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_license";
    //银行卡识别
    public static final String BAIDU_BANKCARD = "https://aip.baidubce.com/rest/2.0/ocr/v1/bankcard";
    //驾驶证识别
    public static final String BAIDU_DRIVING = "https://aip.baidubce.com/rest/2.0/ocr/v1/driving_license";
    // endregion 服务器地址:百度

    // region 服务器地址：患者端
    //正式
//    public static final String BASE_URL_IP = "http://open.dywyhs.com/project/familybed/";

    //临时
    public static final String BASE_URL_IP = "http://192.168.3.4:8082/";

    //测试
    //public static final String BASE_URL_IP = "http://192.168.3.4:8082/";
    // endregion 服务器地址：患者端

    // region 接口定义：患者端
    // 统一获取数据接口地址
    public static final String base_url_get = BASE_URL_IP + "manage/api/api_padLogin.php";

    // 统一提交数据接口地址
    public static final String base_url_post = BASE_URL_IP + "wx/wx_AjaxInfo.php";

    // 获取体征指标档案
    public static final String get_login = base_url_get + "?func=login&tel=";

    // 获取体征指标档案
    public static final String get_physical_sign = base_url_get + "?func=options&BedID=";

    // 获取每日体征上报数据
    public static final String get_daily_reporting = base_url_get + "?func=getDailyReporting&BedID=";

    // 获取今日用药数据
    public static final String get_today_medication = base_url_get + "?func=getDrug&BedID=";
    //endregion 接口定义：患者端

    //region 常量定义：Handler专用

    //获取客户经理可以办理的业务品种
    public static final int handler_error = -1;
    public static final int handler_success = 0;

    public static final int handler_success_today_medication_get = 20230001;

    public static final int handler_success_today_medication_post = 20230002;
    // 获取体征档案
    public static final int handler_success_physical_sign_get = 20230011;
    // 每日数据上报
    public static final int handler_success_daily_reporting_post = 20230012;
    // 获取每日上报数据
    public static final int handler_success_daily_reporting_get = 20230013;
    //endregion 常量定义：Handler专用

}
