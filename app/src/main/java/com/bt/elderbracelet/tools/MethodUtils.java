package com.bt.elderbracelet.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bt.elderbracelet.activity.PushListActivity;
import com.bt.elderbracelet.data.ModelDao;
import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.entity.BloodSugar;
import com.bt.elderbracelet.entity.HeartRate;
import com.bt.elderbracelet.entity.Sleep;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.okhttp.HttpRequest;
import com.bt.elderbracelet.okhttp.URLConstant;
import com.bttow.elderbracelet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2017/8/26.
 */

public class MethodUtils {

    private static Context context;
    private static ProgressDialog dialog;
    private static Toast toast;


    public MethodUtils(Context mContext) {
        context = mContext;
    }


    public static void showToast(final Context context, final String msg) {
        if ("main".equalsIgnoreCase(Thread.currentThread().getName())) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
        } else {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toast == null) {
                        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    } else {
                        toast.setText(msg);
                    }
                    toast.show();
                }
            });
        }
    }

    //显示一个Dialog
    public static void showLoadingDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("数据加载中，请稍候..");
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMax(100);
        dialog.show();
    }

    //取消显示一个Dialog
    public static void cancelLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.cancel();
            dialog = null;
        }
    }

    /**
     * 弹出缓存框
     *
     * @param text 显示内容
     */
    public static AlertDialog createDialog(Context context, String text) {
        AlertDialog mAlertDialog = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_2, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_toast_2_content);
        // 设置TextView的text内容
        try {
            textView.setText(text);
        } catch (Exception e) {
            Log.e("TAG", "textView.setText(text)异常....................................");
        }
        mAlertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        return mAlertDialog;
    }


    /**
     * 判断手机系统的时制 12H/24H
     */
    public static String isTime12_24() {
        String Time12_24 = "";
        if (null != context) {
            boolean is24 = DateFormat.is24HourFormat(context);
            if (is24) {
                Time12_24 = "24";
            } else Time12_24 = "12";
        }
        return Time12_24;
    }

    /**
     * 弹出一个Notification，点击此Notification会跳转到PushListActivity
     */
    private void getVoice() {
        String Currentactivity = MethodUtils.isRunningForeground(context);
        //	if(!Currentactivity.equals("com.bt.elderbracelet.activity.MainActivity")){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null; //= new Notification();
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        }
        if (notification == null) {
            notification = new Notification(R.drawable.ruyi, "如医", System.currentTimeMillis());
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        //自定义声音   声音文件放在ram目录下，没有此目录自己创建一个
        //	notification.sound=Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" +R.raw.mm);
        notification.defaults = Notification.DEFAULT_SOUND;
        Intent intent = new Intent(context, PushListActivity.class);//实例化intent
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, "如医", "有新消息", pi);// 设置事件信息
        manager.notify(1, notification);
        //	}
    }

    /**
     * 判断当前网络是否是wifi网络
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前网络是否是3G网络
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    //判断当前应用程序是在前台运行还是在后台运行
    //但是这个方法返回的还是当前手机界面上运行的Activity的全名
    public static String isRunningForeground(Context context) {
        String packageName = getPackageName(context);
        String topActivityClassName = getTopActivityName(context);
        // System.out.println("packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            System.out.println("---> isRunningForeGround");
        } else {
            System.out.println("---> isRunningBackGround");
        }
        return topActivityClassName;
    }

    //获取当前手机界面上运行的Activity的全名
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager)
                (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    //获取本应用程序的包名
    public static String getPackageName(Context context) {
        String packageName = context.getPackageName();
        return packageName;
    }

    /**
     * 判断字符是否为中文字符
     *
     * @param c 需要进行判断的字符
     * @return 判断结果
     */
    // GENERAL_PUNCTUATION 判断中文的“号
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
    private static final boolean charIsChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static Properties loadConfig(Context context, String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void saveConfig(Context context, String filePath, Properties properties) {
        try {
            File file = new File(filePath);
            file.createNewFile();
            FileOutputStream s = new FileOutputStream(file, false);
            properties.store(s, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断字符串内是否有中文字符，
     *
     * @param strName 需要进行判断的字符串
     * @return 判断结果
     */
    public static final boolean stringHaveChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (charIsChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        // 有存储的SDCard
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static void uploadSport(final Context context, Sport sport) {
        if (sport == null) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("uId", SpHelp.getUserId());
            object.put("date", sport.getDate());
            object.put("step", sport.getStep());
            object.put("distance", sport.getDistance());
            object.put("calorie", sport.getCalorie());
            object.put("sportTime", sport.getSportTime());

            HttpRequest.post(URLConstant.URL_UPLOAD_SPORT, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        SpHelp.saveRecentSynSportTime(BaseUtils.getTodayDate());
                    } else {
                        MethodUtils.showToast(context, "运动数据上传失败，原因和连接服务器相关： " + response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(context, "运动数据上传失败，请检查网络是否良好");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void uploadSleep(final Context context, Sleep sleep) {
        if (sleep == null) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("uId", SpHelp.getUserId());
            object.put("date", sleep.getDate());
            object.put("sleepTime", sleep.getSleepTime());
            object.put("sleepDeepTime", sleep.getSleepDeepTime());

            HttpRequest.post(URLConstant.URL_UPLOAD_SLEEP, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        SpHelp.saveRecentSynSleepTime(BaseUtils.getTodayDate());
                    } else {
                        MethodUtils.showToast(context, "上传睡眠数据失败，原因和连接服务器相关: " + response.optString("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(context, "上传睡眠数据失败，请检查网络是否良好");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void uploadBloodPressure(final Context context, final BloodPressure pressure) {
        if (pressure == null) {
            return;
        }
        JSONObject pressureJson = new JSONObject();
        try {
            pressureJson.put("uId", SpHelp.getUserId());
            pressureJson.put("date", pressure.getPreciseDate());
            pressureJson.put("lowPressure", pressure.getBloodPressureLow());
            pressureJson.put("highPressure", pressure.getBloodPressureHigh());

            HttpRequest.post(URLConstant.URL_UPLOAD_BLOODPRESSURE, pressureJson.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        SpHelp.saveRecentSynPressureTime(BaseUtils.getPreciseDate());
                    } else {
                        MethodUtils.showToast(context, "血压数据上传失败，原因和连接服务器相关" + response.opt("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(context, "血压数据上传失败，请检查网络是否异常");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void uploadHeartRate(final Context context, final HeartRate heartRate) {
        if (heartRate == null) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("uId", SpHelp.getUserId());
            object.put("date", heartRate.getPreciseDate());
            object.put("heartRate", heartRate.getHeartRate());

            HttpRequest.post(URLConstant.URL_UPLOAD_HEART_RATE, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        SpHelp.saveRecentSynHeartRateTime(BaseUtils.getPreciseDate());
                    } else {
                        MethodUtils.showToast(context, "心率数据上传失败，原因和服务器相关" + response.opt("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(context, "心率数据上传失败，请检查网络是否异常");
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void uploadBloodOxygen(final Context context, final BloodOxygen oxygen) {
        if (oxygen == null) {
            return;
        }
        JSONObject pressureJson = new JSONObject();
        try {
            pressureJson.put("uId", SpHelp.getUserId());
            pressureJson.put("date", oxygen.getPreciseDate());
            pressureJson.put("oxygen", oxygen.getBloodOxygen());

            HttpRequest.post(URLConstant.URL_UPLOAD_BLOODOXYGEN, pressureJson.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        SpHelp.saveRecentSynPressureTime(BaseUtils.getPreciseDate());
                    } else {
                        MethodUtils.showToast(context, "血氧数据上传失败，原因和服务器相关：" + response.opt("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(context, "血氧数据上传失败，请检查网络是否异常");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void uploadBloodSugar(final Context context, final BloodSugar sugar) {
        if (sugar == null) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("uId", SpHelp.getUserId())
                    .put("date", sugar.getPreciseDate())
                    .put("beforeMealSugar", sugar.getBloodSugarBefore())
                    .put("afterMealSugar", sugar.getBloodSugarAfter());

            HttpRequest.post(URLConstant.URL_UPLOAD_BLOODSUGAR, object.toString(), new HttpRequest.HttpRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optString("error").equals("0")) {
                        SpHelp.saveRecentSynSugarTime(BaseUtils.getPreciseDate());
                    } else {
                        MethodUtils.showToast(context, "血糖数据上传失败，原因和连接服务器相关：" + response.opt("error_info"));
                    }
                }

                @Override
                public void onFailure() {
                    MethodUtils.showToast(context, "血糖数据上传失败，请检查网络是否良好");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//
//    /**
//     * 判断是否需要同步数据：
//     * 1.如果 recentSynTime 为空，说明之前从来没有同步过数据，则现在肯定要同步数据
//     * 2.如果 recentSynTime 不为空，则之前同步过数据，但是 同步数据的时间 可能是
//     * 2.1 10天前
//     * 2.2 昨天
//     * 2.3 今天(只有这种情况不需要同步数据了)
//     */
//    public boolean checkNeedSynHealthData()
//    {
//        String synTime = SpHelp.getRecentSynHealthTime();
//        if (TextUtils.isEmpty(synTime)) {
//            return true;
//        }
//        try {
//            Date synDate = DateTimeFormatter.YMD.parse(synTime);
//            Date currrentDate = DateTimeFormatter.YMD.parse(BaseUtils.getPreciseDate());
//            if (synDate.before(currrentDate)) {
//                return true;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * @return 检查是否需要同步心率数据
//     */
//    public boolean checkNeedSynHeartRate()
//    {
//        String synTime = SpHelp.getRecentSynHeartRateTime();
//        if (TextUtils.isEmpty(synTime)) {
//            return true;
//        }
//        try {
//            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);
//            Date currrentDate = DateTimeFormatter.YMDHM.parse(BaseUtils.getPreciseDate());
//            if (synDate.before(currrentDate)) {
//                return true;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 检查是否需要同步血压数据
//     */
//    public boolean checkNeedSynBloodPressure()
//    {
//        String synTime = SpHelp.getRecentSynPressureTime();
//        if (TextUtils.isEmpty(synTime)) {
//            return true;
//        }
//        try {
//            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);
//            Date currrentDate = DateTimeFormatter.YMDHM.parse(BaseUtils.getPreciseDate());
//            if (synDate.before(currrentDate)) {
//                return true;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 检查是否需要同步血糖数据
//     */
//    public boolean checkNeedSynBloodSugar()
//    {
//        String synTime = SpHelp.getRecentSynSugarTime();
//        if (TextUtils.isEmpty(synTime)) {
//            return true;
//        }
//        try {
//            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);
//            Date currrentDate = DateTimeFormatter.YMDHM.parse(BaseUtils.getPreciseDate());
//            if (synDate.before(currrentDate)) {
//                return true;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /**
//     * 检查是否需要同步血氧数据
//     */
//    public boolean checkNeedSynBloodOxygen()
//    {
//        String synTime = SpHelp.getRecentSynOxygenTime();
//        if (TextUtils.isEmpty(synTime)) {
//            return true;
//        }
//        try {
//            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);
//            Date currrentDate = DateTimeFormatter.YMDHM.parse(BaseUtils.getPreciseDate());
//            if (synDate.before(currrentDate)) {
//                return true;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    /**
     * 从 Sport 表中 获取需要上传的 运动数据，已经上传过的数据不会再次上传
     */
    public static List<Sport> getSynSport(Context context) {
        ModelDao modelDao = new ModelDao(context);
//        List<Sport> sportList = new ArrayList<>();  //这是所有的健康数据
//
//        sportList.add(new Sport("1","5","1","1","2017/10/30"));
//        sportList.add(new Sport("100","500","89","20","2017/10/31"));
//        sportList.add(new Sport("100","500","89","20","2017/10/29"));
//        sportList.add(new Sport("100","500","89","20","2017/10/30"));
//        sportList.add(new Sport("100","500","89","20","2017/10/31"));
//        sportList.add(new Sport("100","500","89","20","2017/10/29"));

        List<Sport> sportList = modelDao.queryAllSport();  //这是所有的健康数据
        // System.out.println("健康开始大小：" + sportList.size());

        Iterator<Sport> it = sportList.iterator();

        String synTime = SpHelp.getRecentSynSportTime();
        if (TextUtils.isEmpty(synTime)) {
            return sportList;
        }
        try {
            Date synDate = DateTimeFormatter.YMD.parse(synTime);
            while (it.hasNext()) {
                Sport data = it.next();
                Date itemDate = DateTimeFormatter.YMD.parse(data.getDate());
                if (!itemDate.after(synDate)) {
                    it.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("健康结束大小：" + sportList.size());
        return sportList;
    }


    /**
     * 从 Sleep 表中 获取需要上传的 睡眠数据，已经上传过的数据不会再次上传
     */
    public static List<Sleep> getSynSleep(Context context) {
        ModelDao modelDao = new ModelDao(context);
        List<Sleep> sleepList = modelDao.queryAllSleep();  //这是所有的健康数据
        //System.out.println("睡眠开始大小：" + sleepList.size());

        Iterator<Sleep> it = sleepList.iterator();

        String synTime = SpHelp.getRecentSynSleepTime();
        if (TextUtils.isEmpty(synTime)) {
            return sleepList;
        }
        try {
            Date synDate = DateTimeFormatter.YMD.parse(synTime);
            while (it.hasNext()) {
                Sleep sleep = it.next();
                Date itemDate = DateTimeFormatter.YMD.parse(sleep.getDate());
                if (!itemDate.after(synDate)) {
                    it.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("睡眠结束大小：" + sleepList.size());
        return sleepList;
    }


    /**
     * 从 HeartRate 表中 获取需要上传的 数据，已经上传过的数据不会再次上传
     */
    public static List<HeartRate> getSynHeartRate(Context context) {
        ModelDao modelDao = new ModelDao(context);
        List<HeartRate> rateList = modelDao.queryAllHeartRate();

        // System.out.println("心率原始size：" + rateList.size());

        Iterator<HeartRate> it = rateList.iterator();

        String synTime = SpHelp.getRecentSynHeartRateTime();
        if (TextUtils.isEmpty(synTime)) {
            return rateList;
        }
        try {
            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);

            while (it.hasNext()) {
                Date itemDate = DateTimeFormatter.YMDHM.parse(it.next().getPreciseDate());
                if (itemDate.before(synDate)) {
                    it.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // System.out.println("心率结束大小：" + rateList.size());
        return rateList;
    }

    /**
     * 从 BloodSugar 表中 获取需要上传的 数据，已经上传过的数据不会再次上传
     */
    public static List<BloodSugar> getSynBloodSugar(Context context) {
        ModelDao modelDao = new ModelDao(context);
        List<BloodSugar> sugarList = modelDao.queryAllBloodSugar();
        // System.out.println("血糖原始size：" + sugarList.size());

        Iterator<BloodSugar> it = sugarList.iterator();

        String synTime = SpHelp.getRecentSynSugarTime();
        if (TextUtils.isEmpty(synTime)) {
            return sugarList;
        }
        try {
            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);

            while (it.hasNext()) {
                Date itemDate = DateTimeFormatter.YMDHM.parse(it.next().getPreciseDate());
                if (itemDate.before(synDate)) {
                    it.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("血糖结束大小：" + sugarList.size());

        return sugarList;
    }


    /**
     * 从 Bloodpressure 表中 获取需要上传的 数据，已经上传过的数据不会再次上传
     */
    public static List<BloodPressure> getSynBloodPressure(Context context) {
        ModelDao modelDao = new ModelDao(context);
        List<BloodPressure> pressureList = modelDao.queryAllBloodPressure();
        // System.out.println("血压开始大小：" + pressureList.size());

        Iterator<BloodPressure> it = pressureList.iterator();

        String synTime = SpHelp.getRecentSynPressureTime();
        if (TextUtils.isEmpty(synTime)) {
            return pressureList;
        }
        try {
            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);
            while (it.hasNext()) {
                Date itemDate = DateTimeFormatter.YMDHM.parse(it.next().getPreciseDate());
                if (itemDate.before(synDate)) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("血压结束大小：" + pressureList.size());
        return pressureList;
    }


    /**
     * 从 BloodOxygen 表中 获取需要上传的 数据，已经上传过的数据不会再次上传
     */
    public static List<BloodOxygen> getSynBloodOxygen(Context context) {
        ModelDao modelDao = new ModelDao(context);
        List<BloodOxygen> oxygenList = modelDao.queryAllBloodOxygen();
        //  System.out.println("血氧开始大小：" + oxygenList.size());
        Iterator<BloodOxygen> it = oxygenList.iterator();

        String synTime = SpHelp.getRecentSynOxygenTime();
        if (TextUtils.isEmpty(synTime)) {
            return oxygenList;
        }
        try {
            Date synDate = DateTimeFormatter.YMDHM.parse(synTime);
            while (it.hasNext()) {
                Date itemDate = DateTimeFormatter.YMDHM.parse(it.next().getPreciseDate());
                if (itemDate.before(synDate)) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("血氧结束大小：" + oxygenList.size());
        return oxygenList;
    }

    /**
     * 同时开启六个子线程，向服务器同步运动，睡眠，心率，血压，血氧，血糖数据
     */
    public static void synHistotyData(final Context context) {
        //上传运动数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Sport sport : getSynSport(context)) {
                    uploadSport(context, sport);
                }
            }
        }).start();

        //上传睡眠数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Sleep sleep : getSynSleep(context)) {
                    uploadSleep(context, sleep);
                }
            }
        }).start();

        //上传心率数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (HeartRate rate : getSynHeartRate(context)) {
                    uploadHeartRate(context, rate);
                }
            }
        }).start();

        //上传血压数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BloodPressure pressure : getSynBloodPressure(context)) {
                    uploadBloodPressure(context, pressure);
                }
            }
        }).start();

        //上传血氧数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BloodOxygen oxygen : getSynBloodOxygen(context)) {
                    uploadBloodOxygen(context, oxygen);
                }
            }
        }).start();

        //上传血糖数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BloodSugar sugar : getSynBloodSugar(context)) {
                    uploadBloodSugar(context, sugar);
                }
            }
        }).start();

    }
}
