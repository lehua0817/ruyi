package com.bt.elderbracelet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DBNAME = "bracelet.db";

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, 15);
    }

    /**
     * 注册表
     */
    public static final String RegisterTable = "registerTable";

    public static String registerId = "_id";
    public static String name = "name";
    public static String phoneNum = "phoneNum";
    public static String province = "province";
    public static String city = "city";
    public static String area = "area";
    public static String age = "age";
    public static String sex = "sex";
    public static String height = "height";
    public static String weight = "weight";
    public static String stepDistance = "stepDistance";
    public static String urgentContactName = "urgentContactName";
    public static String urgentContactPhone = "urgentContactPhone";
    public static String serviceId = "serviceId";

    public static String art = "art";
    public static String sportsRate = "sportsRate";
    public static String diet = "diet";
    public static String smoke = "smoke";
    public static String drink = "drink";
    public static String allergic = "allergic";
    public static String illness = "illness";


    /**
     * 血压表
     */
    public static final String BloodPressureTable = "bloodPressureTable";

    public static String bloodPressureId = "_id";
    public static String bloodPressureDate = "preciseDate";
    public static String bloodPressureHigh = "bloodPressureHigh";
    public static String bloodPressureLow = "bloodPressureLow";

    /**
     * 血糖表
     */
    public static final String BloodSugarTable = "bloodSugarTable";

    public static String bloodSugarId = "_id";
    public static String bloodSugarDate = "preciseDate";
    public static String bloodSugarBefore = "bloodSugarBefore";
    public static String bloodSugarAfter = "bloodSugarAfter";

    /**
     * 心率表
     */
    public static final String HeartRateTable = "heartRateTable";

    public static String heartRateId = "_id";
    public static String heartRateDate = "preciseDate";
    public static String heartRate = "heartRate";

    /**
     * 血氧表
     */
    public static final String BloodOxygenTable = "bloodOxygenTable";

    public static String bloodOxygenId = "_id";
    public static String bloodOxygenDate = "preciseDate";
    public static String bloodOxygen = "bloodOxygen";


    /**
     * 运动数据表
     */
    public static final String SportTable = "sportTable";

    public static String sportId = "_id";
    public static String sportDate = "date";

    public static String step = "step";
    public static String distance = "distance";
    public static String calorie = "calorie";
    public static String sportTime = "sportTime";


    /**
     * 睡眠数据表
     */
    public static final String SleepTable = "sleepTable";

    public static String sleepId = "_id";
    public static String sleepDate = "date";

    public static String sleepTime = "sleepTime";
    public static String sleepDeepTime = "sleepDeepTime";



    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        String registerSql = "create table if not exists " + RegisterTable + "(" +
                "_id integer primary key autoincrement," +
                "uid text," +
                "name text," +
                "phoneNum text," +
                "province text," +
                "city text," +
                "area text," +
                "age text," +
                "sex text," +
                "height text," +
                "weight text," +
                "stepDistance text," +
                "urgentContactName text," +
                "urgentContactPhone text," +
                "serviceId text," +
                "art text," +
                "sportsRate text," +
                "diet text," +
                "smoke text," +
                "drink text," +
                "allergic text," +
                "illness text" + ")";
        db.execSQL(registerSql);

        String bloodPressureSql = "create table if not exists " + BloodPressureTable + "(" +
                "_id integer primary key autoincrement," +
                "preciseDate text," +
                "bloodPressureHigh text," +
                "bloodPressureLow text" +
                ")";
        db.execSQL(bloodPressureSql);

        String bloodSugarSql = "create table if not exists " + BloodSugarTable + "(" +
                "_id integer primary key autoincrement," +
                "preciseDate text," +
                "bloodSugarBefore text," +
                "bloodSugarAfter text" +
                ")";
        db.execSQL(bloodSugarSql);

        String heartRateSql = "create table if not exists " + HeartRateTable + "(" +
                "_id integer primary key autoincrement," +
                "preciseDate text," +
                "heartRate text" +
                ")";
        db.execSQL(heartRateSql);

        String bloodOxygenSql = "create table if not exists " + BloodOxygenTable + "(" +
                "_id integer primary key autoincrement," +
                "preciseDate text," +
                "bloodOxygen text" +
                ")";
        db.execSQL(bloodOxygenSql);


        String sportSql = "create table if not exists " + SportTable + "(" +
                "_id integer primary key autoincrement," +
                "date text," +
                "step text," +
                "distance text," +
                "calorie text," +
                "sportTime text" + ")";
        db.execSQL(sportSql);

        String sleepSql = "create table if not exists " + SleepTable + "(" +
                "_id integer primary key autoincrement," +
                "date text," +
                "sleepTime text," +
                "sleepDeepTime text" + ")";
        db.execSQL(sleepSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
