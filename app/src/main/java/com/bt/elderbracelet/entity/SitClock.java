package com.bt.elderbracelet.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/7.
 */

public class SitClock implements Serializable{
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;

    public int sitMinute;
    public boolean isOpen;
}
