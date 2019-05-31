package com.bt.elderbracelet.entity;

/**
 * Created by Administrator on 2017/10/19.
 */

public class Sleep {
    private int id;
    private String sleepDeepTime;     //深度睡眠时间
    private String sleepTime;      //睡眠总时间

    private String date;    //日期     日期形式为 2017-08-01

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getSleepDeepTime()
    {
        return sleepDeepTime;
    }

    public void setSleepDeepTime(String sleepDeepTime)
    {
        this.sleepDeepTime = sleepDeepTime;
    }

    public String getSleepTime()
    {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime)
    {
        this.sleepTime = sleepTime;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }


    @Override
    public String toString()
    {
        return "Sleep{" +
                "id=" + id +
                ", sleepDeepTime='" + sleepDeepTime + '\'' +
                ", sleepTime='" + sleepTime + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
