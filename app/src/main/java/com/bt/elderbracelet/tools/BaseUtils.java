package com.bt.elderbracelet.tools;

/**
 * Created by Administrator on 2017/8/26.
 */

import android.content.Context;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 本项目人为定义了两个工具类，BaseUtils 和MethodUtils
 * 其中 BaseUtils 定义了很多操作字节，数字，字符串,日期的方法
 * 而 MethodUtils 定义了其他的很多方法
 */
public class BaseUtils {
    private Context context;

    public BaseUtils(Context mContext)
    {
        context = mContext;
    }

    //操作字节

    /**
     * 将int类型转化为 一个长度为4的字节数组
     */
    public static byte[] intToByteArray(int i)
    {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);   //高位在byte[0]位，低位在byte[3]位
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 取字节数组其中的4个字节转化为 int类型
     * 手环传递过来的数据byte[] 中，其中用4个字节表示总步数，4个字节表示消耗卡路里数
     * 这个方法就是将 4个字节转化为 int类型
     */
    public static int fourBytesToInt(byte[] source)
    {
        int num = (source[3] & 0xff)
                + ((source[2] & 0xff) << 8)
                + ((source[1] & 0xff) << 16)
                + ((source[0] & 0xff) << 24);
        return num;
    }


    public static int twoBytesToInt(byte[] source)
    {
        int num = (source[1] & 0xff)
                + ((source[0] & 0xff) << 8);
        return num;
    }


    /**
     * 将一个byte转化为两个16进制数表示的字符串
     */
    public static String byteToHexString(byte b)
    {
        StringBuilder builder = new StringBuilder("");
        int temper = b & 0xff;
        String str = Integer.toHexString(temper);
        if (str.length() < 2) {
            builder.append("0");
        }
        return builder.append(str).toString();
    }

    /**
     * 将一个byte数组转化为字符串
     */
    public static String byteArrayToString(byte[] bytes)
    {
        StringBuilder builder = new StringBuilder("");
        for (byte b : bytes) {
            builder.append(byteToHexString(b)).append(":");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * 从原始数值的 begin 位置 截取length长度的数组
     */
    public static byte[] getSubByteArray(byte[] oldDada, int begin, int length)
    {
        byte[] newData = new byte[length];
        if ((begin + length) <= oldDada.length) {
            for (int i = 0; i < length; i++) {
                newData[i] = oldDada[i + begin];
            }
        }
        return newData;
    }


    //操作数字


    //将一个整数格式化为至少2位十进制整数 譬如 int n = 5; 结果为05
    @SuppressWarnings("all")
    public static String format(int n)
    {
        return String.format("%02d", n);
    }

    /**
     * 如timeConversion(12,15)，返回字符串 12:15
     */
    public static String timeConversion(int hour, int minth)
    {

        String time = null;
        String hours = "0";
        String minths = "0";
        if (hour < 10) {
            hours = hours + hour;
        } else {
            hours = String.valueOf(hour);
        }
        if (minth < 10) {
            minths = minths + minth;
        } else {
            minths = String.valueOf(minth);
        }
        time = hours + ":" + minths;
        return time;
    }

    /**
     * 获取某个日期前一天的日期
     * 参数形式 ： “2017-08-24”
     * 返回数据形式 ： “2017-08-23”
     */
    public static String getPreDate(String currentDate)
    {
        Date date = null;
        try {
            date = DateTimeFormatter.YMD.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            date = calendar.getTime();
            return DateTimeFormatter.YMD.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取某个日期后一天的日期
     * 参数形式 ： “2017-08-24”
     * 返回数据形式 ： “2017-08-23”
     */
    public static String getNextDate(String currentDate)
    {
        Date date = null;
        try {
            date = DateTimeFormatter.YMD.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            date = calendar.getTime();
            return DateTimeFormatter.YMD.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取昨天的日期
     */
    public static String getYesterdayDate()
    {
        Date date = new Date();
        String dateString = DateTimeFormatter.YMD.format(date);
        return getPreDate(dateString);
    }

    /**
     * 将当前日期变成"2017-08-28" 形式
     */
    public static String getTodayDate()
    {
        return DateTimeFormatter.YMD.format(new Date());
    }

    /**
     * 获取当前精确时间， 形式为“yyyy-MM-dd HH:mm”
     */
    public static String getPreciseDate()
    {
        return DateTimeFormatter.YMDHM.format(new Date());
    }


    public static String deleteChar(String sourceString, char chElemData)
    {
        String deleteString = "";
        for (int i = 0; i < sourceString.length(); i++) {
            if (sourceString.charAt(i) != chElemData) {
                deleteString += sourceString.charAt(i);
            }
        }
        return deleteString;
    }

}
