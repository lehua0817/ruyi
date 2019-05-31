package com.bt.elderbracelet.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.entity.BloodSugar;
import com.bt.elderbracelet.entity.Sleep;
import com.bt.elderbracelet.entity.Sport;
import com.bt.elderbracelet.entity.HeartRate;
import com.bt.elderbracelet.entity.Register;

import java.util.ArrayList;
import java.util.List;


public class ModelDao {
    private DBOpenHelper helper;

    public ModelDao(Context context)
    {
        helper = new DBOpenHelper(context);
    }

    //注意：本地数据库中，每个表最多保留7条数据

    /**
     * 1.注册表
     * 2.血压表
     * 3.血糖表
     * 4.血氧表
     * 5.心率表
     * 6.睡眠表
     * 7.运动表
     * 8.健康数据表
     */

    ////////////  实体Register----表registerTal /////////
    public long insertRegister(Register register)
    {
        List<Register> registerList = queryAllRegister();
        //删除大于7条的数据
        if (registerList.size() == 7) {
            deleteRegister(registerList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.name, register.getName());
        values.put(DBOpenHelper.phoneNum, register.getNum());
        values.put(DBOpenHelper.province, register.getProvince());
        values.put(DBOpenHelper.city, register.getCity());
        values.put(DBOpenHelper.area, register.getArea());
        values.put(DBOpenHelper.age, register.getAge());
        values.put(DBOpenHelper.sex, register.getSex());
        values.put(DBOpenHelper.height, register.getHeight());
        values.put(DBOpenHelper.weight, register.getWeight());
        values.put(DBOpenHelper.stepDistance, register.getStepDistance());
        values.put(DBOpenHelper.urgentContactName, register.getUrgentContactName());
        values.put(DBOpenHelper.urgentContactPhone, register.getUrgentContactPhone());
        values.put(DBOpenHelper.serviceId, register.getServiceId());
        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.RegisterTable, DBOpenHelper.name, values);
        db.close();
        return rowId;
    }

    public int deleteRegister(int id)
    {
        int count = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.RegisterTable, "_id=" + id, null);
        db.close();
        return count;
    }

    public ArrayList<Register> queryAllRegister()
    {
        ArrayList<Register> registers = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.RegisterTable, null);
        registers = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                Register register = new Register();
                register.setId(c.getInt(c.getColumnIndex(DBOpenHelper.registerId)));
                register.setName(c.getString(c.getColumnIndex(DBOpenHelper.name)));
                register.setProvince(c.getString(c.getColumnIndex(DBOpenHelper.province)));
                register.setCity(c.getString(c.getColumnIndex(DBOpenHelper.city)));
                register.setArea(c.getString(c.getColumnIndex(DBOpenHelper.area)));
                register.setNum(c.getString(c.getColumnIndex(DBOpenHelper.phoneNum)));
                register.setAge(c.getString(c.getColumnIndex(DBOpenHelper.age)));
                register.setSex(c.getString(c.getColumnIndex(DBOpenHelper.sex)));
                register.setHeight(c.getString(c.getColumnIndex(DBOpenHelper.height)));
                register.setWeight(c.getString(c.getColumnIndex(DBOpenHelper.weight)));
                register.setStepDistance(c.getString(c.getColumnIndex(DBOpenHelper.stepDistance)));
                register.setUrgentContactName(c.getString(c.getColumnIndex(DBOpenHelper.urgentContactName)));
                register.setUrgentContactPhone(c.getString(c.getColumnIndex(DBOpenHelper.urgentContactPhone)));
                register.setServiceId(c.getString(c.getColumnIndex(DBOpenHelper.serviceId)));
                registers.add(register);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return registers;
    }

    public long updateRegister(Register register, int id)
    {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.registerId, register.getId());
        values.put(DBOpenHelper.name, register.getName());
        values.put(DBOpenHelper.phoneNum, register.getNum());
        values.put(DBOpenHelper.province, register.getProvince());
        values.put(DBOpenHelper.city, register.getCity());
        values.put(DBOpenHelper.area, register.getArea());
        values.put(DBOpenHelper.age, register.getAge());
        values.put(DBOpenHelper.sex, register.getSex());
        values.put(DBOpenHelper.height, register.getHeight());
        values.put(DBOpenHelper.weight, register.getWeight());
        values.put(DBOpenHelper.stepDistance, register.getStepDistance());
        values.put(DBOpenHelper.urgentContactName, register.getUrgentContactName());
        values.put(DBOpenHelper.urgentContactPhone, register.getUrgentContactPhone());
        values.put(DBOpenHelper.serviceId, register.getServiceId());
        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.update(DBOpenHelper.RegisterTable, values, "_id=?", new String[]{id + ""});
        System.out.println("更新注册**********rowId" + rowId);
        db.close();
        return rowId;
    }


    public Register getLastRegister()
    {
        ArrayList<Register> userList = queryAllRegister();
        int size = userList.size();
        if (size == 0) {
            return null;
        }
        return userList.get(size - 1);
    }

    //////////  实体BloodPressure------表BloodPressureTable   ////////////////
    public long insertBloodPressure(BloodPressure pressure)
    {
        List<BloodPressure> pressureList = queryAllBloodPressure();
        //删除大于7条的数据
        if (pressureList.size() == 7) {
            deleteBloodPressure(pressureList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.bloodPressureDate, pressure.getPreciseDate());
        values.put(DBOpenHelper.bloodPressureHigh, pressure.getBloodPressureHigh());
        values.put(DBOpenHelper.bloodPressureLow, pressure.getBloodPressureLow());
        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.BloodPressureTable, DBOpenHelper.bloodPressureDate, values);
        db.close();
        return rowId;
    }

    public int deleteBloodPressure(int id)
    {
        int count = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.BloodPressureTable, "_id=" + id, null);
        db.close();
        return count;
    }

    public ArrayList<BloodPressure> queryAllBloodPressure()
    {
        ArrayList<BloodPressure> pressureList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.BloodPressureTable, null);
        pressureList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                BloodPressure pressure = new BloodPressure();
                pressure.setId(c.getInt(c.getColumnIndex(DBOpenHelper.bloodPressureId)));
                pressure.setPreciseDate(c.getString(c.getColumnIndex(DBOpenHelper.bloodPressureDate)));
                pressure.setBloodPressureHigh(c.getString(c.getColumnIndex(DBOpenHelper.bloodPressureHigh)));
                pressure.setBloodPressureLow(c.getString(c.getColumnIndex(DBOpenHelper.bloodPressureLow)));
                pressureList.add(pressure);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return pressureList;
    }

    public BloodPressure getLastBloodPressure()
    {
        ArrayList<BloodPressure> pressureList = queryAllBloodPressure();
        if (pressureList == null) {
            return null;
        }
        int size = pressureList.size();
        return pressureList.get(size - 1);
    }

    public BloodPressure queryPressureByDate(String date)
    {
        BloodPressure pressure = null;
        ArrayList<BloodPressure> pressureList = queryAllBloodPressure();
        int countHigh = 0;
        int countLow = 0;
        int n = 0;
        if (pressureList != null && pressureList.size() > 0) {
            for (BloodPressure p : pressureList) {
                if (p.getPreciseDate().substring(0, 10).equals(date)) {
                    countHigh += Integer.valueOf(p.getBloodPressureHigh());
                    countLow += Integer.valueOf(p.getBloodPressureLow());
                    n++;
                }
            }
            pressure = new BloodPressure();
            pressure.setBloodPressureHigh(String.valueOf(countHigh / n));
            pressure.setBloodPressureLow(String.valueOf(countLow / n));
            pressure.setPreciseDate(date);
        }
        return pressure;
    }


    //////////  实体BloodSugar------表BloodSugarTable   ////////////////
    public long insertBloodSugar(BloodSugar sugar)
    {
        List<BloodSugar> sugarList = queryAllBloodSugar();
        //删除大于7条的数据
        if (sugarList.size() == 7) {
            deleteBloodSugar(sugarList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.bloodSugarDate, sugar.getPreciseDate());
        values.put(DBOpenHelper.bloodSugarBefore, sugar.getBloodSugarBefore());
        values.put(DBOpenHelper.bloodSugarAfter, sugar.getBloodSugarAfter());

        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.BloodSugarTable, DBOpenHelper.bloodSugarDate, values);
        db.close();
        return rowId;
    }


    public int deleteBloodSugar(int id)
    {
        int count = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.BloodSugarTable, "_id=" + id, null);
        db.close();
        return count;
    }


    public ArrayList<BloodSugar> queryAllBloodSugar()
    {
        ArrayList<BloodSugar> sugarList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.BloodSugarTable, null);
        sugarList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                BloodSugar sugar = new BloodSugar();
                sugar.setId(c.getInt(c.getColumnIndex(DBOpenHelper.bloodSugarId)));
                sugar.setPreciseDate(c.getString(c.getColumnIndex(DBOpenHelper.bloodSugarDate)));
                sugar.setBloodSugarBefore(c.getString(c.getColumnIndex(DBOpenHelper.bloodSugarBefore)));
                sugar.setBloodSugarAfter(c.getString(c.getColumnIndex(DBOpenHelper.bloodSugarAfter)));
                sugarList.add(sugar);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return sugarList;
    }

    public BloodSugar getLastBloodSuger()
    {
        ArrayList<BloodSugar> sugarList = queryAllBloodSugar();
        int size = sugarList.size();
        if (size == 0) {
            return null;
        }
        return sugarList.get(size - 1);
    }

    public BloodSugar querySugarByDate(String date)
    {
        BloodSugar sugar = null;
        ArrayList<BloodSugar> sugarList = queryAllBloodSugar();
        int countBefore = 0;
        int countAfter = 0;
        int n = 0;
        if (sugarList != null && sugarList.size() > 0) {
            for (BloodSugar s : sugarList) {
                if (s.getPreciseDate().substring(0, 10).equals(date)) {
                    countBefore += Integer.valueOf(s.getBloodSugarBefore());
                    countAfter += Integer.valueOf(s.getBloodSugarAfter());
                    n++;
                }
            }
            sugar = new BloodSugar();
            sugar.setBloodSugarBefore(String.valueOf(countBefore / n));
            sugar.setBloodSugarAfter(String.valueOf(countAfter / n));
            sugar.setPreciseDate(date);
        }
        return sugar;
    }


    //////////  实体BloodOxygen------表BloodOxygenTable   ////////////////


    /**
     * 在往BloodOxygen表中插入数据时，如果现在数据是7条，
     * 则删除最老的一条记录，再插入一条新数据，从而保证BloodOxygen表中的数据条数不会超过7条
     */
    public long insertBloodOxygen(BloodOxygen oxygen)
    {
        List<BloodOxygen> oxygenList = queryAllBloodOxygen();
        //删除大于7条的数据
        if (oxygenList.size() == 7) {
            deleteBloodOxygen(oxygenList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.bloodOxygenDate, oxygen.getPreciseDate());
        values.put(DBOpenHelper.bloodOxygen, oxygen.getBloodOxygen());
        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.BloodOxygenTable, DBOpenHelper.bloodOxygenDate, values);
        db.close();
        return rowId;
    }

    public int deleteBloodOxygen(int id)
    {
        int count = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.BloodOxygenTable, "_id=" + id, null);
        db.close();
        return count;
    }

    public ArrayList<BloodOxygen> queryAllBloodOxygen()
    {
        ArrayList<BloodOxygen> oxygenList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.BloodOxygenTable, null);
        oxygenList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                BloodOxygen oxygen = new BloodOxygen();
                oxygen.setId(c.getInt(c.getColumnIndex(DBOpenHelper.bloodOxygenId)));
                oxygen.setPreciseDate(c.getString(c.getColumnIndex(DBOpenHelper.bloodOxygenDate)));
                oxygen.setBloodOxygen(c.getString(c.getColumnIndex(DBOpenHelper.bloodOxygen)));
                oxygenList.add(oxygen);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return oxygenList;
    }

    /**
     * @return 获取BloodOxygen表中最近的一条记录，在显示界面时常常用到
     */
    public BloodOxygen getLastOxygen()
    {
        ArrayList<BloodOxygen> oxygenList = queryAllBloodOxygen();
        int size = oxygenList.size();
        if (size == 0) {
            return null;
        }
        return oxygenList.get(size - 1);
    }

    public BloodOxygen queryOxygenByDate(String date)
    {
        BloodOxygen oxygen = null;
        ArrayList<BloodOxygen> oxygenList = queryAllBloodOxygen();
        int count = 0;
        int n = 0;
        if (oxygenList != null && oxygenList.size() > 0) {
            for (BloodOxygen o : oxygenList) {
                if (o.getPreciseDate().substring(0, 10).equals(date)) {
                    count += Integer.valueOf(o.getBloodOxygen());
                    n++;
                }
            }
            oxygen = new BloodOxygen();
            oxygen.setBloodOxygen(String.valueOf(count / n));
            System.out.println(String.valueOf(count / n));
            oxygen.setBloodOxygen("Oxygen  :" + count / n);

            oxygen.setPreciseDate(date);
        }
        return oxygen;
    }

    //////////  实体HeartRate------表HeartRateTable   ////////////////
    public long insertHeartRate(HeartRate heartRate)
    {
        List<HeartRate> rateList = queryAllHeartRate();
        //删除大于7条的数据
        if (rateList.size() == 7) {
            deleteHeartRate(rateList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.heartRateDate, heartRate.getPreciseDate());
        values.put(DBOpenHelper.heartRate, heartRate.getHeartRate());
        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.HeartRateTable, DBOpenHelper.heartRateDate, values);
        db.close();
        return rowId;
    }


    public int deleteHeartRate(int id)
    {
        int count = -1;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.HeartRateTable, "_id=" + id, null);
        db.close();
        return count;
    }

    public ArrayList<HeartRate> queryAllHeartRate()
    {
        ArrayList<HeartRate> heartRateList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.HeartRateTable, null);
        heartRateList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                HeartRate heartRate = new HeartRate();
                heartRate.setId(c.getInt(c.getColumnIndex(DBOpenHelper.heartRateId)));
                heartRate.setPreciseDate(c.getString(c.getColumnIndex(DBOpenHelper.heartRateDate)));
                heartRate.setHeartRate(c.getString(c.getColumnIndex(DBOpenHelper.heartRate)));
                heartRateList.add(heartRate);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return heartRateList;
    }

    public HeartRate getLastHeartRate()
    {
        ArrayList<HeartRate> rateList = queryAllHeartRate();
        int size = rateList.size();
        if (size == 0) {
            return null;
        }
        return rateList.get(size - 1);
    }

    /**
     * 查询某一天所有的心率数据，然后将这些心率数据求平均值，得到
     * 一个综合的心率数据，而且这个综合心率数据的日期不是“yyyy-MM-dd HH:mm”形式，
     * 而是"yyyy-MM-dd" ,因为传的参数类型就是 ”yyyy-MM-dd“
     */
    public HeartRate queryHeartRateByDate(String date)
    {
        HeartRate rate = null;
        ArrayList<HeartRate> rateList = queryAllHeartRate();
        int count = 0;
        int n = 0;
        if (rateList != null && rateList.size() > 0) {
            for (HeartRate r : rateList) {
                if (r.getPreciseDate().substring(0, 10).equals(date)) {
                    count += Integer.valueOf(r.getHeartRate());
                    n++;
                }
            }
            rate = new HeartRate();
            rate.setHeartRate(String.valueOf(count / n));
            System.out.println(count / n);
            System.out.println(String.valueOf(count / n));
            rate.setPreciseDate(date);
        }
        return rate;
    }

    /**
     * 运动数据表
     */
    public long insertSport(Sport data)
    {
        List<Sport> sportList = queryAllSport();
        //删除大于7条的数据
        if (sportList.size() == 7) {
            deleteSport(sportList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.sportDate, data.getDate());
        values.put(DBOpenHelper.step, data.getStep());
        values.put(DBOpenHelper.distance, data.getDistance());
        values.put(DBOpenHelper.calorie, data.getCalorie());
        values.put(DBOpenHelper.sportTime, data.getSportTime());

        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.SportTable, DBOpenHelper.sportDate, values);
        db.close();
        return rowId;
    }

    public int deleteSport(int id)
    {
        int count;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.sportDate, "_id=" + id, null);
        db.close();
        return count;
    }

    public long updateSport(Sport data, String date)
    {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.sportId, data.getId());
        values.put(DBOpenHelper.sportDate, data.getDate());
        values.put(DBOpenHelper.step, data.getStep());
        values.put(DBOpenHelper.distance, data.getDistance());
        values.put(DBOpenHelper.calorie, data.getCalorie());
        values.put(DBOpenHelper.sportTime, data.getSportTime());

        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.update(DBOpenHelper.SportTable, values, "date=?", new String[]{date});
        db.close();
        return rowId;
    }

    public ArrayList<Sport> queryAllSport()
    {
        ArrayList<Sport> dataList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.SportTable, null);
        dataList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                Sport data = new Sport();
                data.setId(c.getInt(c.getColumnIndex(DBOpenHelper.sportId)));
                data.setDate(c.getString(c.getColumnIndex(DBOpenHelper.sportDate)));
                data.setStep(c.getString(c.getColumnIndex(DBOpenHelper.step)));
                data.setDistance(c.getString(c.getColumnIndex(DBOpenHelper.distance)));
                data.setCalorie(c.getString(c.getColumnIndex(DBOpenHelper.calorie)));
                data.setSportTime(c.getString(c.getColumnIndex(DBOpenHelper.sportTime)));

                dataList.add(data);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return dataList;
    }

    public Sport getLastSport()
    {
        ArrayList<Sport> dataList = queryAllSport();
        int size = dataList.size();
        if (size == 0) {
            return null;
        }
        return dataList.get(size - 1);
    }

    public Sport querySportByDate(String date)
    {
        Sport sport = null;
        ArrayList<Sport> sportList = queryAllSport();
        if (sportList != null && sportList.size() > 0) {
            for (Sport sp : sportList) {
                if (sp.getDate().equals(date)) {
                    sport = sp;
                    break;
                }
            }
        }
        return sport;
    }


    /**
     * 睡眠数据表
     */
    public long insertSleep(Sleep sleep)
    {
        List<Sleep> sleepList = queryAllSleep();
        //删除大于7条的数据
        if (sleepList.size() == 7) {
            deleteSleep(sleepList.get(0).getId());
        }

        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.sleepDate, sleep.getDate());

        values.put(DBOpenHelper.sleepDeepTime, sleep.getSleepDeepTime());
        values.put(DBOpenHelper.sleepTime, sleep.getSleepTime());

        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.insert(DBOpenHelper.SleepTable, DBOpenHelper.sleepDate, values);
        db.close();
        return rowId;
    }

    public int deleteSleep(int id)
    {
        int count;
        SQLiteDatabase db = helper.getWritableDatabase();
        count = db.delete(DBOpenHelper.SleepTable, "_id=" + id, null);
        db.close();
        return count;
    }

    public long updateSleep(Sleep data, String date)
    {
        long rowId = -1;
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.sleepId, data.getId());
        values.put(DBOpenHelper.sleepDate, data.getDate());

        values.put(DBOpenHelper.sleepDeepTime, data.getSleepDeepTime());
        values.put(DBOpenHelper.sleepTime, data.getSleepTime());

        SQLiteDatabase db = helper.getWritableDatabase();
        rowId = db.update(DBOpenHelper.SleepTable, values, "date=?", new String[]{date});
        db.close();
        return rowId;
    }

    public ArrayList<Sleep> queryAllSleep()
    {
        ArrayList<Sleep> dataList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBOpenHelper.SleepTable, null);
        dataList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                Sleep data = new Sleep();
                data.setId(c.getInt(c.getColumnIndex(DBOpenHelper.sleepId)));
                data.setDate(c.getString(c.getColumnIndex(DBOpenHelper.sleepDate)));

                data.setSleepTime(c.getString(c.getColumnIndex(DBOpenHelper.sleepTime)));
                data.setSleepDeepTime(c.getString(c.getColumnIndex(DBOpenHelper.sleepDeepTime)));

                dataList.add(data);
            }
        }
        db.close();
        if (c != null) {
            c.close();
        }
        return dataList;
    }

    public Sleep getLastSleep()
    {
        ArrayList<Sleep> dataList = queryAllSleep();
        int size = dataList.size();
        if (size == 0) {
            return null;
        }
        return dataList.get(size - 1);
    }


    public Sleep querySleepByDate(String date)
    {
        Sleep sleep = null;
        ArrayList<Sleep> sleepList = queryAllSleep();
        if (sleepList != null && sleepList.size() > 0) {
            for (Sleep sp : sleepList) {
                if (sp.getDate().equals(date)) {
                    sleep = sp;
                    break;
                }
            }
        }
        return sleep;
    }

}
