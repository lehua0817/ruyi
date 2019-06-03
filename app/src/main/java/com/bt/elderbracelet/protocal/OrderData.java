package com.bt.elderbracelet.protocal;

import java.util.Calendar;

public class OrderData {

    // 给手环推送的消息类型
    public static final int NOTIFICATION_PHONE = 0;   //电话
    public static final int NOTIFICATION_SMS = 1;     //短信
    public static final int NOTIFICATION_WECHAT = 2;  //微信
    public static final int NOTIFICATION_QQ = 3;      //QQ


    public static boolean open = true; // 闹钟开关

    public static final String AIR_UPDATE_DEVICE = "air_update_device";//空中升级指令
    public static final String RESTRAT_DEVICE_ORDER = "restrat_device_order";//重启设备指令
    public static final String GET_DEVICE_MAC_ORDER = "get_device_mac_order";//获取Mac地址指令
    public static final String GET_DEVICE_INFORMATION_ORDER = "get_device_information_order";//获取手环信息指令
    public static final String SHAKE_ORDER = "shake_order";//手环震动指令
    public static final String SYN_TIME_ORDER = "syn_time_order";//同步时间指令
    public static final String SYN_SPORT_ORDER = "syn_sport_order";//同步运动数据指令
    public static final String SET_TIME_24_ORDER = "set_time_24_order";// 设置时间格式为24小时
    public static final String SET_TIME_12_ORDER = "set_time_12_order";// 设置时间格式为12小时
    public static final String LOW_POWER_REMIND_ORDER = "low_power_remind_order"; //低电量提醒
    public static final String PHONE_CALL_REMIND_ORDER = "phone_call_remind_order"; //来电提醒
    public static final String PHONE_MESSAGE_REMIND_ORDER = "phone_message_remind_order"; //短信提醒
    public static final String GET_SPORT_BIG_DATA = "get_sport_big_data"; //获取运动大数据
    public static final String GET_SLEEP_BIG_DATA = "get_sleep_big_data"; //获取睡眠大数据


    /**
     * 获取久坐提醒指令
     */
    public static byte[] getSedentaryOrder(boolean open,
                                           boolean[] whichDays,
                                           boolean repeat,
                                           int startHour,
                                           int startMinute,
                                           int endHour,
                                           int endMinute,
                                           int keepSit)
    {
        byte[] data = new byte[20];
        data[0] = (byte) 0x20;
        data[1] = (byte) 0x02;
        data[2] = (byte) 0x07;
        if (open) {
            data[3] = (byte) (0x01);       //设置久坐提醒开关
        } else {
            data[3] = (byte) (0x00);
        }
        data[4] = getRepeatByte(whichDays, repeat);

        data[5] = (byte) startHour;      //第一个时间段开始的"hour"
        data[6] = (byte) startMinute;      //第一个时间段开始的"minute"
        data[7] = (byte) endHour;      //第一个时间段结束的"hour"
        data[8] = (byte) endMinute;      //第一个时间段结束的"minute"

        data[9] = (byte) 0x00;      //第二个时间段开始的"hour"
        data[10] = (byte) 0x00;      //第二个时间段开始的"minute"
        data[11] = (byte) 0x00;      //第二个时间段结束的"hour"
        data[12] = (byte) 0x00;      //第二个时间段结束的"minute"

        data[13] = (byte) 0x00;      //第三个时间段开始的"hour"
        data[14] = (byte) 0x00;      //第三个时间段开始的"minute"
        data[15] = (byte) 0x00;      //第三个时间段结束的"hour"
        data[16] = (byte) 0x00;      //第三个时间段结束的"minute"

        data[17] = (byte) keepSit;      //久坐10分钟则要提醒

        return data;

    }

    /**
     * 获取 闹钟设置 的指令
     */
    public static byte[] getClockOrder(int index, boolean open, boolean[] whichDays, boolean repeat, int hour, int minute)
    {
        byte[] data = new byte[20];
        data[0] = (byte) 0x20;
        data[1] = (byte) 0x02;
        data[2] = (byte) 0x04;

        data[3] = (byte) (index);       //设置闹钟序号
        if (open) {                       //设置闹钟开关
            data[4] = (byte) (0x01);
        } else {
            data[4] = (byte) (0x00);
        }
        data[5] = getRepeatByte(whichDays, repeat); //设置闹钟的重复位
        data[6] = (byte) hour;      //设置闹钟的“小时”位
        data[7] = (byte) minute;      //设置闹钟的“分钟”位
        data[8] = (byte) (0x01);      //闹钟标签位，也是固定的

        return data;
    }

    /**
     * 获取 服药提醒 的指令
     */
    public static byte[] getMedicineClockOrder(int index, boolean open, boolean[] whichDays, boolean repeat, int hour, int minute)
    {
        byte[] data = new byte[20];
        data[0] = (byte) 0x20;
        data[1] = (byte) 0x02;
        data[2] = (byte) 0x04;

        data[3] = (byte) index;            //设置序号
        if (open) {
            data[4] = (byte) (0x01);      //设置服药提醒开关
        } else {
            data[4] = (byte) (0x00);
        }
        data[5] = getRepeatByte(whichDays, repeat); //设置闹钟的重复位
        data[6] = (byte) hour;      //设置闹钟的“小时”位
        data[7] = (byte) minute;      //设置闹钟的“分钟”位
        data[8] = (byte) (0x00);      //服药提醒标签位
        return data;
    }

    /**
     * 获取 睡眠提醒 的指令
     */
    public static byte[] getSleepClockOrder(int index,
                                            boolean open,
                                            boolean[] whichDays,
                                            boolean repeat,
                                            int hour,
                                            int minute,
                                            int endHour,
                                            int endMinute)
    {
        byte[] data = new byte[20];
        data[0] = (byte) 0x20;
        data[1] = (byte) 0x02;
        data[2] = (byte) 0x08;

        data[3] = (byte) index;            //设置序号
        if (open) {
            data[4] = (byte) (0x01);      //设置服药提醒开关
        } else {
            data[4] = (byte) (0x00);
        }
        data[5] = getRepeatByte(whichDays, repeat); //设置闹钟的重复位
        data[6] = (byte) hour;      //设置计划入睡小时
        data[7] = (byte) minute;    //设置计划入睡分钟
        data[8] = (byte) endHour;      //设置计划醒来小时
        data[9] = (byte) endMinute;    //设置计划醒来分钟
        return data;
    }

    /* @param whichDays whichDays[0]  代表周日是否重复
     *                  whichDays[1]  周一
     *                  whichDays[2]  周二
     *                  whichDays[3]  周三
     *                  whichDays[4]  周四
     *                  whichDays[5]  周五
     *                  whichDays[6]  周六
     *                  repeat  每周是否重复
     */
    public static byte getRepeatByte(boolean[] whichDays, boolean repeatOnce)
    {
        byte[] bytes = new byte[8];
        byte set_repeat = 0;
        if (whichDays[0]) {       // 0 代表周日
            bytes[0] = 0x01;
        } else {
            bytes[0] = 0x00;
        }
        if (whichDays[1]) {    //1 代表周一
            bytes[1] = 0x01;
        } else {
            bytes[1] = 0x00;
        }
        if (whichDays[2]) {   //2 代表周二
            bytes[2] = 0x01;
        } else {
            bytes[2] = 0x00;
        }
        if (whichDays[3]) {  //3 代表周三
            bytes[3] = 0x01;
        } else {
            bytes[3] = 0x00;
        }
        if (whichDays[4]) {  //4 代表周四
            bytes[4] = 0x01;
        } else {
            bytes[4] = 0x00;
        }
        if (whichDays[5]) {  //5 代表周五
            bytes[5] = 0x01;
        } else {
            bytes[5] = 0x00;
        }
        if (whichDays[6]) {    // 6 周六
            bytes[6] = 0x01;
        } else {
            bytes[6] = 0x00;
        }
        if (repeatOnce) {   // 7 代表每周是否重复
            bytes[7] = 0x01;
        } else {
            bytes[7] = 0x00;
        }
        for (int i = 0; i < bytes.length; i++) {
            set_repeat = (byte) (set_repeat + bytes[i] * Math.pow(2, i));
        }
        return set_repeat;
    }

    /**
     * 定义了手环所有的功能，每个功能使用不同的字节数组数据
     */
    public static byte[] getCommonOrder(String orderString)
    {
        byte[] data = new byte[20];
        switch (orderString) {
            case RESTRAT_DEVICE_ORDER://发送 重启设备指令
                System.out.println("我要发送重启指令了");
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x00;
                data[2] = (byte) 0x01;
                break;
            case AIR_UPDATE_DEVICE://空中升级指令
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x00;
                data[2] = (byte) 0x02;
                break;
            case GET_DEVICE_MAC_ORDER:   //获取手环信息指令
                System.out.println("我要发送 获取Mac地址指令了");
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x01;
                data[2] = (byte) 0x01;
                break;
            case GET_DEVICE_INFORMATION_ORDER://获取手环信息指令
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x01;
                data[2] = (byte) 0x02;
                break;
            case SHAKE_ORDER://手环震动指令
                data = new byte[20];
                data[0] = (byte) 0x54;
                data[1] = (byte) 0x31;
                break;
            case SYN_TIME_ORDER:  //同步时间指令
                System.out.println("我要发送同步时间指令了");
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x02;
                data[2] = (byte) 0x01;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);

                data[3] = (byte) (year - 2000);
                data[4] = (byte) month;
                data[5] = (byte) day;
                data[6] = (byte) hour;
                data[7] = (byte) minute;
                data[8] = (byte) second;
                break;
            case SET_TIME_24_ORDER:  //设置时间格式为24小时
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x02;
                data[2] = (byte) 0x02;
                data[3] = (byte) 0x00;      //设置性别 男
                data[4] = (byte) 0x00;      //设置年龄
                data[5] = (byte) 0x00;      //设置身高，单位厘米
                data[6] = (byte) 0x00;      //设置体重，单位Kg
                data[7] = (byte) 0x00;      //设置语言，这个是固定值，不能人为修改
                data[8] = (byte) 0x00;      //设置小时制，00 这是24小时制
                data[9] = (byte) 0x00;      //设置公/英里
                break;
            case SET_TIME_12_ORDER:  //设置时间格式为12小时
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x02;
                data[2] = (byte) 0x02;
                data[3] = (byte) 0x00;      //设置性别 男
                data[4] = (byte) 0x00;      //设置年龄
                data[5] = (byte) 0x00;      //设置身高，单位厘米
                data[6] = (byte) 0x00;      //设置体重，单位Kg
                data[7] = (byte) 0x00;      //设置语言，这个是固定值，不能人为修改
                data[8] = (byte) 0x01;      //设置小时制，01 这是12小时制
                data[9] = (byte) 0x00;      //设置公/英里
                break;
            case LOW_POWER_REMIND_ORDER://低电量提醒指令
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x02;
                data[2] = (byte) 0x0a;
                data[3] = (byte) 0x01;
                break;
            case PHONE_CALL_REMIND_ORDER:// 来电提醒指令
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x04;
                data[2] = (byte) 0x01;
                break;
            case PHONE_MESSAGE_REMIND_ORDER://短信提醒指令
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x04;
                data[2] = (byte) 0x02;
                break;
            case GET_SPORT_BIG_DATA: //获取运动大数据
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x07;
                data[2] = (byte) 0x03;
                break;
            case SYN_SPORT_ORDER: //手动同步运动大数据
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x07;
                data[2] = (byte) 0x01;
                break;

            case GET_SLEEP_BIG_DATA://获取睡眠大数据
                System.out.println("我要发送 获取睡眠大数据 指令了");
                data = new byte[20];
                data[0] = (byte) 0x20;
                data[1] = (byte) 0x07;
                data[2] = (byte) 0x04;
                break;
            default:
                break;
        }

        return data;
    }


}
