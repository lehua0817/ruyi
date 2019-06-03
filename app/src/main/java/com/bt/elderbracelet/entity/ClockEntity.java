package com.bt.elderbracelet.entity;

import java.io.Serializable;

public class ClockEntity implements Serializable {

    /**
     * 时钟数据实体，关键属性是isOpen和whichDays
     * 用来标志 一周内哪几天 会响闹钟
     */
    public int id;
    public int hour;
    public int minute;

    public int isOpen;
    public int enableMonday;
    public int enableTuesday;
    public int enableWednesday;
    public int enableThursday;
    public int enableFriday;
    public int enableSaturday;
    public int enableSunday;
    public String content;
    public boolean isSingle;

}
