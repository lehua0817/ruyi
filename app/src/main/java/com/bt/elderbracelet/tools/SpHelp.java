package com.bt.elderbracelet.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.bonten.ble.application.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by yao on 2016/3/10.
 */
public class SpHelp implements Serializable {

    /**
     * SharePreference中要保存大量的数据，其中涉及到很多的关键字
     * 这个类就是列出所有的关键字以及 封装了向MyApplication.sp 存入和获取数据的方法
     */

    /**
     * 系统中保存到的用户Id
     */
    public static final String USER_ID = "user_id";

    public static void saveUserId(String id) {
        MyApplication.sp.edit().putString(USER_ID, id).commit();
    }

    public static String getUserId() {
        return MyApplication.sp.getString(USER_ID, "");
    }

    /**
     * 系统中保存到的用户头像
     */
    public static final String USER_PHOTO = "user_photo";

    public static void saveUserPhoto(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            MyApplication.sp.edit().putString(USER_PHOTO, null).commit();
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] encodeByteArray = Base64.encode(baos.toByteArray(), Base64.DEFAULT);
        String str = new String(encodeByteArray);
        MyApplication.sp.edit().putString(USER_PHOTO, str).commit();
    }

    public static Bitmap getUserPhoto() {
        String str = MyApplication.sp.getString(USER_PHOTO, null);
        if (!TextUtils.isEmpty(str)) {
            byte[] decodeByteArray = Base64.decode(str.getBytes(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodeByteArray, 0, decodeByteArray.length);
            return bitmap;
        } else {
            return null;
        }


    }


    /**
     * 保存蓝牙设备名字;
     */
    public static final String SAVE_DEVICE_NAME = "save_device_name";

    public static void saveDeviceName(String name) {
        MyApplication.sp.edit().putString(SAVE_DEVICE_NAME, name).commit();
    }

    public static String getDeviceName() {
        return MyApplication.sp.getString(SAVE_DEVICE_NAME, "");
    }

    /**
     * 保存蓝牙地址;
     */
    public static final String SAVE_DEVICE_MAC = "save_device_mac";

    public static void saveDeviceMac(String mac) {
        MyApplication.sp.edit().putString(SAVE_DEVICE_MAC, mac).commit();
    }

    public static String getDeviceMac() {
        return MyApplication.sp.getString(SAVE_DEVICE_MAC, "");
    }

    /**
     * 按返回键进入后台运行
     */
    public static final String JOIN_BACKGROUND = "join_background";

    public static void saveJoinGround(boolean flag) {
        MyApplication.sp.edit().putBoolean(JOIN_BACKGROUND, flag).commit();
    }

    public static boolean getJoinGround() {
        return MyApplication.sp.getBoolean(JOIN_BACKGROUND, false);
    }


    /**
     * 上次同步心率数据的时间
     */
    public static final String RECENT_SYN_HEARTRATE_TIME = "recent_syn_heartrate_time";

    public static void saveRecentSynHeartRateTime(String time) {
        MyApplication.sp.edit().putString(RECENT_SYN_HEARTRATE_TIME, time).commit();
    }

    public static String getRecentSynHeartRateTime() {
        return MyApplication.sp.getString(RECENT_SYN_HEARTRATE_TIME, null);
    }

    /**
     * 上次同步血压数据的时间
     */
    public static final String RECENT_SYN_PRESSURE_TIME = "recent_syn_pressure_time";

    public static void saveRecentSynPressureTime(String time) {
        MyApplication.sp.edit().putString(RECENT_SYN_PRESSURE_TIME, time).commit();
    }

    public static String getRecentSynPressureTime() {
        return MyApplication.sp.getString(RECENT_SYN_PRESSURE_TIME, null);
    }

    /**
     * 上次同步血氧数据的时间
     */
    public static final String RECENT_SYN_OXYGEN_TIME = "recent_syn_oxygen_time";

    public static void saveRecentSynOxygenTime(String time) {
        MyApplication.sp.edit().putString(RECENT_SYN_OXYGEN_TIME, time).commit();
    }

    public static String getRecentSynOxygenTime() {
        return MyApplication.sp.getString(RECENT_SYN_OXYGEN_TIME, null);
    }

    /**
     * 上次同步血糖数据的时间
     */
    public static final String RECENT_SYN_SUGAR_TIME = "recent_syn_sugar_time";

    public static void saveRecentSynSugarTime(String time) {
        MyApplication.sp.edit().putString(RECENT_SYN_SUGAR_TIME, time).commit();
    }

    public static String getRecentSynSugarTime() {
        return MyApplication.sp.getString(RECENT_SYN_SUGAR_TIME, null);
    }


    /**
     * 上次同步运动数据的时间
     */
    public static final String RECENT_SYN_SPORT_TIME = "recent_synchronize_sport_time";

    public static void saveRecentSynSportTime(String time) {
        MyApplication.sp.edit().putString(RECENT_SYN_SPORT_TIME, time).commit();
    }

    public static String getRecentSynSportTime() {
        return MyApplication.sp.getString(RECENT_SYN_SPORT_TIME, null);
    }


    /**
     * 上次同步睡眠数据的时间
     */
    public static final String RECENT_SYN_SLEEP_TIME = "recent_synchronize_sleep_time";

    public static void saveRecentSynSleepTime(String time) {
        MyApplication.sp.edit().putString(RECENT_SYN_SLEEP_TIME, time).commit();
    }

    public static String getRecentSynSleepTime() {
        return MyApplication.sp.getString(RECENT_SYN_SLEEP_TIME, null);
    }


    //各组闹钟设置
    public static final String CLOCK_ONE = "clock_one";
    public static final String CLOCK_TWO = "clock_two";
    public static final String CLOCK_THREE = "clock_three";
    public static final String CLOCK_FOUR = "clock_four";
    public static final String CLOCK_FIVE = "clock_five";


    public static String getClockKey(int position) {
        String clockKey = null;
        switch (position) {
            case 0:
                clockKey = SpHelp.CLOCK_ONE;
                break;
            case 1:
                clockKey = SpHelp.CLOCK_TWO;
                break;
            case 2:
                clockKey = SpHelp.CLOCK_THREE;
                break;
            case 3:
                clockKey = SpHelp.CLOCK_FOUR;
                break;
            case 4:
                clockKey = SpHelp.CLOCK_FIVE;
                break;
            default:
                break;
        }
        return clockKey;
    }


    //各组服药提醒设置
    public static final String MEDICINE_CLOCK_ONE = "medicine_clock_one";
    public static final String MEDICINE_CLOCK_TWO = "medicine_clock_two";
    public static final String MEDICINE_CLOCK_THREE = "medicine_clock_three";
    public static final String MEDICINE_CLOCK_FOUR = "medicine_clock_four";
    public static final String MEDICINE_CLOCK_FIVE = "medicine_clock_five";


    public static String getMedicineClockKey(int position) {
        String medicineClockKey = null;
        switch (position) {
            case 0:
                medicineClockKey = SpHelp.MEDICINE_CLOCK_ONE;
                break;
            case 1:
                medicineClockKey = SpHelp.MEDICINE_CLOCK_TWO;
                break;
            case 2:
                medicineClockKey = SpHelp.MEDICINE_CLOCK_THREE;
                break;
            case 3:
                medicineClockKey = SpHelp.MEDICINE_CLOCK_FOUR;
                break;
            case 4:
                medicineClockKey = SpHelp.MEDICINE_CLOCK_FIVE;
                break;
            default:
                break;
        }
        return medicineClockKey;
    }


    //各组睡眠提醒设置
    public static final String SLEEP_CLOCK_ONE = "sleep_clock_one";
    public static final String SLEEP_CLOCK_TWO = "sleep_clock_two";
    public static final String SLEEP_CLOCK_THREE = "sleep_clock_three";
    public static final String SLEEP_CLOCK_FOUR = "sleep_clock_four";
    public static final String SLEEP_CLOCK_FIVE = "sleep_clock_five";


    public static String getSleepClockKey(int position) {
        String sleepClockKey = null;
        switch (position) {
            case 0:
                sleepClockKey = SpHelp.SLEEP_CLOCK_ONE;
                break;
            case 1:
                sleepClockKey = SpHelp.SLEEP_CLOCK_TWO;
                break;
            case 2:
                sleepClockKey = SpHelp.SLEEP_CLOCK_THREE;
                break;
            case 3:
                sleepClockKey = SpHelp.SLEEP_CLOCK_FOUR;
                break;
            case 4:
                sleepClockKey = SpHelp.SLEEP_CLOCK_FIVE;
                break;
            default:
                break;
        }
        return sleepClockKey;
    }

    //久坐提醒只需要设置一组，则只要一个关键字就可以了
    public static final String SIT_REMIND = "sit_remind";

    public static final String FD_REMIND = "fd_remind";
    public static final int FD_REMIND_OPEN = 1;
    public static final int FD_REMIND_CLOSE = 0;


    public static void saveFdRemind(int open) {
        MyApplication.sp.edit().putInt(FD_REMIND, open).commit();
    }

    public static int getFdRemind() {
        return MyApplication.sp.getInt(FD_REMIND, 0);
    }


    /**
     * 将自定义的数据类型保存到SharePreference中
     */

    public static void saveObject(String key, Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] originalBytes = baos.toByteArray();
            byte[] dealedBytes = Base64.encode(originalBytes, 0);
            String content = new String(dealedBytes);
            MyApplication.sp.edit().putString(key, content).commit();

        } catch (IOException e) {
/*
            System.out.println(Log.getStackTraceString(e));
*/
            e.printStackTrace();
        }

    }

    public static Object getObject(String key) {
        Object object = null;
        String content = MyApplication.sp.getString(key, null);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        try {
            byte[] dealedBytes = content.getBytes();
            byte[] originalBytes = Base64.decode(dealedBytes, 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(originalBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            object = ois.readObject();
        } catch (IOException e) {
            System.out.println(Log.getStackTraceString(e));
        } catch (ClassNotFoundException e) {
            System.out.println(Log.getStackTraceString(e));
        }
        return object;
    }

    //保存PersonalDetailOne数据
    public static final String PERSONAL_DETAIL_ONE = "personal_detail_one";

    public static void savePersonalDetailOne(String detail) {
        MyApplication.sp.edit().putString(PERSONAL_DETAIL_ONE, detail).commit();
    }

    public static String getPersonalDetailOne() {
        return MyApplication.sp.getString(PERSONAL_DETAIL_ONE, null);
    }

    //保存PersonalDetailTwo数据
    public static final String PERSONAL_DETAIL_TWO = "personal_detail_two";

    public static void savePersonalDetailTwo(String detail) {
        MyApplication.sp.edit().putString(PERSONAL_DETAIL_TWO, detail).commit();
    }

    public static String getPersonalDetailTwo() {
        return MyApplication.sp.getString(PERSONAL_DETAIL_TWO, null);
    }


    //保存PersonalDetailThree数据
    public static final String PERSONAL_DETAIL_THREE = "personal_detail_three";

    public static void savePersonalDetailThree(String detail) {
        MyApplication.sp.edit().putString(PERSONAL_DETAIL_THREE, detail).commit();
    }

    public static String getPersonalDetailThree() {
        return MyApplication.sp.getString(PERSONAL_DETAIL_THREE, null);
    }


    /**
     * 来电提醒开关
     */
    public static final String TELEPHONE_CALL_REMIND = "TelephoneCallRemind";

    public static void savePhoneCallRemind(boolean enable) {
        MyApplication.sp.edit().putBoolean(TELEPHONE_CALL_REMIND, enable).commit();
    }

    public static boolean getPhoneCallRemind() {
        return MyApplication.sp.getBoolean(TELEPHONE_CALL_REMIND, false);
    }

    /**
     * 短信提醒开关
     */
    public static final String TELEPHONE_MSG_REMIND = "TelephoneMsgRemind";

    public static void savePhoneMsgRemind(boolean enable) {
        MyApplication.sp.edit().putBoolean(TELEPHONE_MSG_REMIND, enable).commit();
    }

    public static boolean getPhoneMsgRemind() {
        return MyApplication.sp.getBoolean(TELEPHONE_MSG_REMIND, false);
    }


//    /**
//     * 短信提醒开关
//     */
//    public static final String BUTTON_CLICK_TIME = "button_click_time";
//
//    public static void saveButtonClickTime(long time)
//    {
//        MyApplication.sp.edit().putLong(BUTTON_CLICK_TIME, time).commit();
//    }
//
//    public static long getPhoneMsgRemind()
//    {
//        return MyApplication.sp.getBoolean(TELEPHONE_MSG_REMIND, false);
//    }


}
