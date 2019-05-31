package com.bt.elderbracelet.entity.others;

import com.bt.elderbracelet.entity.BloodOxygen;
import com.bt.elderbracelet.entity.BloodPressure;
import com.bt.elderbracelet.entity.HeartRate;

/**
 * Created by Administrator on 2016/9/25.
 */
public class Event {
    public boolean update_step = false;
    public boolean update_distance = false;
    public boolean update_caloria = false;
    public boolean update_sport_time = false;
    public boolean update_sleep = false;
    public BloodPressure pressure = null;
    public BloodOxygen oxygen = null;
    public HeartRate heartRate = null;
    public boolean update_blood_sugar = false;
    public boolean update_clock = false;
    public boolean update_sleep_remind = false;
    public boolean update_keep_sit = false;
    public boolean update_syn_time = false;
    public String mac = "";
    public String msg = "";

}
