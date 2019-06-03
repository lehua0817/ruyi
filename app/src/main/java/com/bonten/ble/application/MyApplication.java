package com.bonten.ble.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.bt.elderbracelet.activity.EmptyActivity;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bt.elderbracelet.tools.BaseUtils;
import com.bt.elderbracelet.tools.MethodUtils;
import com.bt.elderbracelet.tools.SpHelp;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

public class MyApplication extends Application {
    public static final int STEP_HISTORY = 1;
    public static final int CALORIA_HISTORY = 2;
    public static final int SPORT_TIME_HISTORY = 3;
    public static final int DISTANCE_HISTORY = 4;

    private static MyApplication instance;
    private List<Activity> activityList = new LinkedList<Activity>();
    public static int activityStuts = 0;
    public static int style = 0;
    public static int registerValue;
    public static String date = "";
    public static ModelDao model = null;
    public static String id = null;

    public static int sleepTime = 0;
    public static int sleepDeepTime1 = 0;
    public static int sleepDeepTime2 = 0;
    public static int sleepDeepTime3 = 0;
    public static ArrayList<String> hobbyList;
    public static ArrayList<String> dietList;
    public static ArrayList<String> allergicList;
    public static ArrayList<String> illnessList;

    public static String nStep_distance = "";
    public static SharedPreferences sp;
    public static final String PRESSURE_TYPE = "bracelet_pressure";
    public static final String SUGAR_TYPE = "sugar";

    public static boolean cb_switch = false;

    public static String ConfigPath = "";
    public static String Default_Ip = "http://szry888.com/";
    public static String Ip_Address = "";

    /**
     * 蓝牙连接标志
     */
    public static boolean isConnected = false;
    public static IRemoteService remoteService = null;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        model = new ModelDao(getApplicationContext());
        context = getApplicationContext();
        if (sp == null) {
            sp = context.getSharedPreferences("data", 0);
        }

        ConfigPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ruyi_configuation.txt";
        File file = new File(ConfigPath);
        if (file.exists()) {
            Properties pro = MethodUtils.loadConfig(this, ConfigPath);
            if (pro.get("ip") != null) {
                System.out.println("配置文件存在");
                Ip_Address = BaseUtils.deleteChar((String) pro.get("ip"), '\\');
                System.out.println("读取到的Ip地址是：" + Ip_Address);
            }
        } else {
            System.out.println("配置文件不存在");
            Ip_Address = Default_Ip;
            System.out.println("默认Ip地址是：" + Ip_Address);
            Properties prop = new Properties();
            prop.put("ip", Default_Ip);
            MethodUtils.saveConfig(this, ConfigPath, prop);
        }

        uploadLoginDate();
        try {
            CrashReport.initCrashReport(getApplicationContext(), "900025103", false);
        } catch (Exception e) {
            Log.e("TAG", "腾讯bugly 异常......");
        }
    }

    public void uploadLoginDate() {
        if (!TextUtils.isEmpty(SpHelp.getUserId())) {
            JSONObject object = new JSONObject();
            try {
                object.put("uId", SpHelp.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpRequest.post(URLConstant.URL_LOGIN_DATE, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        System.out.println("上传登陆时间成功");
                    } else {
                        MethodUtils.showToast(getApplicationContext(), response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(getApplicationContext(), "请检查网络是否异常");
                }
            });
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }

    /**
     * 退出程序使用
     */
    //添加Activity 到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }
    //遍历所有Activity 并finish

    public void exit() {
        for (Activity activity : activityList) {
            // 这里之所以要保留EmptyActivity，是因为它连接了SampleBleService，这个不能断开
            if (!activity.getClass().equals(EmptyActivity.class)) {
                activity.finish();
            }
        }
    }


}
