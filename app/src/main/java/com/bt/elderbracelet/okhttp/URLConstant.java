package com.bt.elderbracelet.okhttp;

public class URLConstant {

    // 服务器根域名前缀
    // 根Ip地址由配置文件获取，在项目中是 MyApplication.Ip_Address
    // public static final String ROOT = "http://sdev.whu.edu.cn/ruyi/";
    //public static final String ROOT = "http://szry888.com/";
    //static final String ROOT = "http://10.132.9.178:8080/";

    // 注册信息Url
    public static final String URL_REGISTER = "user/register.template";
    //登陆验证表
    public static final String URL_ALREADY_REGISTER = "user/ifRegister.template";

    // 用户信息详情Url
    public static final String URL_UPLOAD_PERSONAL_DETAIL = "user/addInformation.template";
    public static final String URL_GET_PERSONAL_DETAIL = "user/getInformation.template";

    //下面分别是上传 运动数据上传Url，睡眠数据上传Url，
    // 心率数据上传Url，血糖数据上传Url，
    // 血压数据上传Url，血氧数据上传Url，
    public static final String URL_UPLOAD_SPORT = "one/userData/userSport.template";
    public static final String URL_UPLOAD_SLEEP = "one/userData/userSleep.template";
    public static final String URL_UPLOAD_HEART_RATE = "one/userData/userHeartRate.template";
    public static final String URL_UPLOAD_BLOODSUGAR = "one/userData/userBloodSugar.template";
    public static final String URL_UPLOAD_BLOODPRESSURE = "one/userData/userBloodPressure.template";
    public static final String URL_UPLOAD_BLOODOXYGEN = "one/userData/userBloodOxygen.template";

    //下面分别是分页查询，一次查7天的 运动，睡眠，心率，血压，血氧，血糖数据
    public static final String URL_GET_SPORT = "one/userData/getSport.template";
    public static final String URL_GET_SLEEP = "one/userData/getSleep.template";
    public static final String URL_GET_HEART_RATE = "one/userData/getHeartRate.template";
    public static final String URL_GET_BLOODSUGAR = "one/userData/getBloodSugar.template";
    public static final String URL_GET_BLOODPRESSURE = "one/userData/getBloodPressure.template";
    public static final String URL_GET_BLOODOXYGEN = "one/userData/getBloodOxygen.template";

    //下面分别是查询某一天的 运动，睡眠，心率，血压，血氧，血糖数据
    public static final String URL_GET_ONE_SPORT = "one/userData/getOneSport.template";
    public static final String URL_GET_ONE_SLEEP = "one/userData/getOneSleep.template";
    public static final String URL_GET_ONE_HEART_RATE = "one/userData/getOneHeartRate.template";
    public static final String URL_GET_ONE_BLOODSUGAR = "one/userData/getOneBloodSugar.template";
    public static final String URL_GET_ONE_BLOODPRESSURE = "one/userData/getOneBloodPressure.template";
    public static final String URL_GET_ONE_BLOODOXYGEN = "one/userData/getOneBloodOxygen.template";

    public static final String URL_CONTACT_SERVICE = "one/contactCustomerService/contactCustomerService.template";

    public static final String URL_PUSH = "one/push/push.template";    //查看后台有没有新的推送消息
    public static final String URL_PUSH_DETAIL = "one/push/detail.template";   // 查看某条推送消息详情
    public static final String URL_PUSH_HISTORY = "one/push/history.template";  //给某用户推送过的历史消息
    public static final String URL_FAVORITE = "one/push/favorite.template"; // 用户id + 收藏列表Id
    public static final String URL_FAVORITE_HISTORY = "one/push/favoritelist.template"; //用户收藏过的所有信息
    public static final String URL_DELETE_HISTORY = "one/push/deletehistory.template";  //用户删除过的信息

    public static final String URL_SHARE = "share/share.template";

    public static final String URL_GET_VERIFY_CODE = "one/Home/sendAuthenticode.template";
    public static final String URL_ALTER_PWD = "one/user/updatePassword.template";
    public static final String URL_FIND_PWD = "one/user/findPassword.template";
    public static final String URL_LOGIN_DATE = "one/user/updateLastLoginDate.template";
}
