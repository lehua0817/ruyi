package com.bt.elderbracelet.entity;

public class SleepClock {

    public int isOpen;
    public int midStartHour;
    public int midStartMinute;
    public int midEndHour;
    public int midEndMinute;
    public int nightStartHour;
    public int nightStartMinute;
    public int nightEndMinute;
    public int nightEndHour;

    /**
     * 默认午睡时间是从中午12:30到14:00
     * 晚上睡觉时间是从晚上21:00到7:00
     */
    public SleepClock() {
        this.midStartHour = 12;
        this.midStartMinute = 30;
        this.midEndHour = 14;
        this.midEndMinute = 0;

        this.nightStartHour = 21;
        this.nightStartMinute = 0;
        this.nightEndHour = 7;
        this.nightEndMinute = 0;
    }
}
