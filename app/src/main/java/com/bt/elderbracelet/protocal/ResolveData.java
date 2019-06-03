//package com.bt.elderbracelet.protocal;
//
//import android.content.Context;
//
//import com.bonten.ble.application.MyApplication;
//import com.bt.elderbracelet.data.ModelDao;
//import com.bt.elderbracelet.entity.BloodOxygen;
//import com.bt.elderbracelet.entity.BloodPressure;
//import com.bt.elderbracelet.entity.Sleep;
//import com.bt.elderbracelet.entity.Sport;
//import com.bt.elderbracelet.entity.HeartRate;
//import com.bt.elderbracelet.entity.others.Event;
//import com.bt.elderbracelet.tools.BaseUtils;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//import de.greenrobot.event.EventBus;
//
///**
// * 数据解析类
// *
// * @author Administrator
// */
//
//public class ResolveData {
//
//    private Context mcontext;
//    private ModelDao modelDao;
//
//
//    public ResolveData(Context context)
//    {
//        mcontext = context;
//        modelDao = new ModelDao(context);
//    }
//
//    /**
//     * 数据解析
//     * 解析普通蓝牙通信命令
//     * 如 睡眠设置，久坐提醒等
//     */
//    public void decodeCommonData(byte[] cmdData)
//    {
//
//        if (null != cmdData && cmdData[0] == (byte) 0x20) {
//            //解析Mac地址数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x01 && cmdData[2] == 0x01) {
//                System.out.println("获取Mac地址成功");
//                byte[] data = BaseUtils.getSubByteArray(cmdData, 3, 6);
//
//                Event event = new Event();
//                event.mac = BaseUtils.byteArrayToString(data);
//                EventBus.getDefault().post(event);
//
//            }
//            //设置时间指令返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x02 && cmdData[2] == 0x01 && cmdData[3] == 0x01) {
//                System.out.println("时间设置成功");
//                Event event = new Event();
//                event.update_syn_time = true;
//                EventBus.getDefault().post(event);
//            }
//
//            //设置用户基本信息 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x02 && cmdData[2] == 0x02 && cmdData[3] == 0x01) {
//                System.out.println("用户信息设置成功");
//            }
//
//            //设置闹钟 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x02 && cmdData[2] == 0x04 && cmdData[3] == 0x01) {
//                Event event = new Event();
//                event.update_clock = true;
//                EventBus.getDefault().post(event);
//            }
//            //设置久坐提醒 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x02 && cmdData[2] == 0x07 && cmdData[3] == 0x01) {
//                System.out.println("设置久坐提醒成功");
//                Event event = new Event();
//                event.update_keep_sit = true;
//                EventBus.getDefault().post(event);
//            }
//            //设置睡眠提醒 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x02 && cmdData[2] == 0x08 && cmdData[3] == 0x01) {
//                System.out.println("设置睡眠提醒成功");
//                Event event = new Event();
//                event.update_sleep_remind = true;
//                EventBus.getDefault().post(event);
//            }
//            //设置低电量提醒 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x02 && cmdData[2] == 0x0a && cmdData[3] == 0x01) {
//                System.out.println("设置低电量提醒成功");
//            }
//            //设置来电提醒 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x04 && cmdData[2] == 0x01 && cmdData[3] == 0x01) {
//                System.out.println("设置来电提醒成功");
//            }
//            //设置短信提醒 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x04 && cmdData[2] == 0x02 && cmdData[3] == 0x01) {
//                System.out.println("设置短信提醒成功");
//            }
//
//            //设置心率血氧开关 返回数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x05 && cmdData[2] == 0x01 && cmdData[3] == 0x01) {
//                System.out.println("设置心率血氧成功");
//            }
//            //解析实时运动数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x01) {
//                System.out.println("运动数据来了");
//
//                int step = BaseUtils.fourBytesToInt(BaseUtils.getSubByteArray(cmdData, 3, 4));
//                int distance = BaseUtils.fourBytesToInt(BaseUtils.getSubByteArray(cmdData, 7, 4));
//                int calories = BaseUtils.fourBytesToInt(BaseUtils.getSubByteArray(cmdData, 11, 4));
//                int sportTime = BaseUtils.twoBytesToInt(BaseUtils.getSubByteArray(cmdData, 15, 2));
//
//                Sport sport = new Sport();
//                sport.setDate(BaseUtils.getTodayDate());
//                sport.setStep(String.valueOf(step));
//                sport.setDistance(String.valueOf(distance));
//                sport.setCalorie(String.valueOf(calories));
//                sport.setSportTime(String.valueOf(sportTime));
//
//                boolean flag = false;  //代表数据库中是否已经有今天的数据记录
//
//                ArrayList<Sport> sportList = modelDao.queryAllSport();
//                if (sportList != null && sportList.size() > 0) {
//                    for (int i = 0; i < sportList.size(); i++) {
//                        if (sportList.get(i).getDate().equals(BaseUtils.getTodayDate())) {
//                            sport.setId(sportList.get(i).getId());
//                            flag = true;
//                            break;
//                        }
//                    }
//                }
//                if (flag) {
//                    modelDao.updateSport(sport, BaseUtils.getTodayDate());
//                } else {
//                    modelDao.insertSport(sport);
//                }
//
//                Event event = new Event();
//                event.update_step = true;
//                event.update_distance = true;
//                event.update_caloria = true;
//                event.update_sport_time = true;
//                EventBus.getDefault().post(event);
//            }
//            //解析实时心率血氧血压数据
//            //这条指令一次性发过来心率，血压，血氧三个数据，到底那个
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x02) {
//                int heartRateNum = cmdData[3] & 0xff;
//                int oxygenNum = cmdData[4] & 0xff;
//                int pressureHigh = cmdData[5] & 0xff;
//                int pressureLow = cmdData[6] & 0xff;
//
//                Event event = null;
//                if (heartRateNum != 0) {
//                    System.out.println("心率来了");
//                    HeartRate heartRate = new HeartRate();
//                    heartRate.setHeartRate(String.valueOf(heartRateNum));
//                    heartRate.setPreciseDate(BaseUtils.getPreciseDate());
//
//                    event = new Event();
//                    event.heartRate = heartRate;
//
//                    EventBus.getDefault().post(event);
//                }
//
//                if (oxygenNum != 0) {
//                    System.out.println("血氧来了");
//                    BloodOxygen bloodOxygen = new BloodOxygen();
//                    bloodOxygen.setBloodOxygen(String.valueOf(oxygenNum));
//                    bloodOxygen.setPreciseDate(BaseUtils.getPreciseDate());
//
//                    event = new Event();
//                    event.oxygen = bloodOxygen;
//                    EventBus.getDefault().post(event);
//                }
//
//                if (pressureHigh != 0 && pressureLow != 0) {
//                    System.out.println("血压来了");
//                    BloodPressure pressure = new BloodPressure();
//                    pressure.setBloodPressureHigh(String.valueOf(pressureHigh));
//                    pressure.setBloodPressureLow(String.valueOf(pressureLow));
//                    pressure.setPreciseDate(BaseUtils.getPreciseDate());
//                    modelDao.insertBloodPressure(pressure);
//
//                    event = new Event();
//                    event.pressure = pressure;
//                    EventBus.getDefault().post(event);
//                }
//
//            }
//
//            //解析计步大数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x03) {
//                System.out.println(BaseUtils.byteArrayToString(cmdData));
//            }
//            //发送计步大数据成功
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0xff && cmdData[3] == 0x01) {
//                System.out.println("计步大数据同步成功");
//            }
//
//            //解析睡眠大数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x04 && cmdData[3] == 0x00) {
//                MyApplication.sleepTime = getSleepTime(cmdData);
//                System.out.println("总睡眠时间 ：" + MyApplication.sleepTime);
//                System.out.println("总睡眠时间 ：" + BaseUtils.byteArrayToString(cmdData));
//                System.out.println(MyApplication.sleepTime);
//            }
//
//            //解析睡眠子数据1
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x04 && cmdData[3] == 0x01) {
//                MyApplication.sleepDeepTime1 = getDeepSleepDate(cmdData);
//                System.out.println("深睡时间1 ："+ MyApplication.sleepDeepTime1);
//                System.out.println("深睡时间1 ："+ BaseUtils.byteArrayToString(cmdData));
//            }
//            //解析睡眠子数据2
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x04 && cmdData[3] == 0x02) {
//                MyApplication.sleepDeepTime2 = getDeepSleepDate(cmdData);
//                System.out.println("深睡时间2 ："+ MyApplication.sleepDeepTime1);
//                System.out.println("深睡时间2 ："+ BaseUtils.byteArrayToString(cmdData));
//
//            }
//            //解析睡眠子数据3
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == 0x04 && cmdData[3] == 0x03) {
//                MyApplication.sleepDeepTime3 = getDeepSleepDate(cmdData);
//                System.out.println("深睡时间3："+ MyApplication.sleepDeepTime1);
//                System.out.println("深睡时间3 ："+ BaseUtils.byteArrayToString(cmdData));
//            }
//
//            //解析睡眠大数据
//            if (cmdData[0] == 0x20 && cmdData[1] == 0x07 && cmdData[2] == (byte) 0xfe && cmdData[3] == 0x01) {
//                System.out.println("睡眠大数据同步成功");
//                Sleep sleep = new Sleep();
//                sleep.setDate(BaseUtils.getYesterdayDate());
//                sleep.setSleepTime(String.valueOf(MyApplication.sleepTime));
//                sleep.setSleepDeepTime(String.valueOf(MyApplication.sleepDeepTime1
//                        + MyApplication.sleepDeepTime2
//                        + MyApplication.sleepDeepTime3));
//
//
//
//                boolean flag = false;
//
//                ArrayList<Sleep> sleepList = modelDao.queryAllSleep();
//                for (int i = 0; i < sleepList.size(); i++) {
//                    if (sleepList.get(i).getDate().equals(BaseUtils.getYesterdayDate())) {
//                        sleep.setId(sleepList.get(i).getId());
//                        flag = true;
//                        break;
//                    }
//                }
//
//                if (flag) {
//                    modelDao.updateSleep(sleep, BaseUtils.getYesterdayDate());
//                } else {
//                    modelDao.insertSleep(sleep);
//                }
//                MyApplication.sleepTime = 0;
//                MyApplication.sleepDeepTime1 = 0;
//                MyApplication.sleepDeepTime2 = 0;
//                MyApplication.sleepDeepTime3 = 0;
//                Event event = new Event();
//                event.update_sleep = true;
//                EventBus.getDefault().post(event);
//            }
//        }
//    }
//
//    public static int getDeepSleepDate(byte[] source)
//    {
//        int deepTime = 0;
//        if (source != null && source.length > 0 && source[0] == (byte) 0x20 && source[1] == (byte) 0x07 && source[2] == (byte) 0x04) {
//            for (int i = 4; i < source.length; i = i + 2) {
//                if (source[i] == (byte) 0x40) {
//                    deepTime += source[i + 1] & 0xff;
//                }
//            }
//        }
//        return deepTime;
//    }
//
//    public static int getSleepTime(byte[] source)
//    {
//        int sleepTime = 0;
//        if (source != null && source.length > 0 && source[0] == (byte) 0x20 && source[1] == (byte) 0x07 && source[2] == (byte) 0x04) {
//            int sleepYear = source[4] & 0xff;
//            int sleepMonth = source[5] & 0xff;
//            int sleepDay = source[6] & 0xff;
//            int sleepHour = source[7] & 0xff;
//            int sleepMinute = source[8] & 0xff;
//
//            System.out.println("入睡时间是：" +(sleepYear+2000)+"年"+(sleepMonth)+"月"
//            +sleepDay+"日"+sleepHour+"时"+sleepMinute+"分");
//
//            Calendar calendarSleep = Calendar.getInstance();
//            calendarSleep.set(Calendar.YEAR, sleepYear + 2000);
//            calendarSleep.set(Calendar.MONTH, sleepMonth);
//            calendarSleep.set(Calendar.DAY_OF_MONTH, sleepDay);
//            calendarSleep.set(Calendar.HOUR_OF_DAY, sleepHour);
//            calendarSleep.set(Calendar.MINUTE, sleepMinute);
//
//            int wakeYear = source[9] & 0xff;
//            int wakeMonth = source[10] & 0xff;
//            int wakeDay = source[11] & 0xff;
//            int wakeHour = source[12] & 0xff;
//            int wakeMinute = source[13] & 0xff;
//
//            System.out.println("醒来时间是：" +(wakeYear+2000)+"年"+(wakeMonth)+"月"
//                    +wakeDay+"日"+wakeHour+"时"+wakeMinute+"分");
//
//            Calendar calendarWake = Calendar.getInstance();
//            calendarWake.set(Calendar.YEAR, wakeYear + 2000);
//            calendarWake.set(Calendar.MONTH, wakeMonth);
//            calendarWake.set(Calendar.DAY_OF_MONTH, wakeDay);
//            calendarWake.set(Calendar.HOUR_OF_DAY, wakeHour);
//            calendarWake.set(Calendar.MINUTE, wakeMinute);
//
//            long time = calendarWake.getTime().getTime() - calendarSleep.getTime().getTime();
//            sleepTime = (int) (time / 1000 / 60);
//        }
//        return sleepTime;
//    }
//
//
//}