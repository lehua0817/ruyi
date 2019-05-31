package com.bt.elderbracelet.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/10/7.
 */

public class SitClock implements Serializable {
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;

    public int sitTime;
    public boolean isOpen;
    public boolean isRepeatOnce;
    public boolean[] whichDays; //周日，周一，周二，周三，周四，周五，周六

    public SitClock()
    {
        super();
        whichDays = new boolean[]{false, false, false, false, false, false, false};
    }


    @Override
    public String toString()
    {
        return "SitClock{" +
                "startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", sitTime=" + sitTime +
                ", isOpen=" + isOpen +
                ", isRepeatOnce=" + isRepeatOnce +
                ", whichDays=" + Arrays.toString(whichDays) +
                '}';
    }
}
