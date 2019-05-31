package com.bt.elderbracelet.entity;

import java.io.Serializable;
import java.util.Arrays;

public class ClockEntity implements Serializable {

    /**
     * 时钟数据实体，关键属性是isOpen和whichDays
     * 用来标志 一周内哪几天 会响闹钟
     */
    public int id;
    public int hour;
    public int minute;

    public boolean isOpen;
    public boolean isRepeatOnce;
    public boolean isShock;
    public boolean isMusic;
    public boolean[] whichDays; //周日，周一，周二，周三，周四，周五，周六

    /**
     * whichDays 默认全为false，则默认一周七天都不提醒
     * isRepeatOnce 默认为false,也默认 是重复提醒
     */
    public ClockEntity()
    {
        super();
        whichDays = new boolean[]{false, false, false, false, false, false, false};
    }


}
