package com.bt.elderbracelet.entity;

import java.io.Serializable;

public class HeartRate implements Serializable {

    private int id;
    private String heartRate;
    private String preciseDate;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getHeartRate()
    {
        return heartRate;
    }

    public void setHeartRate(String heartRate)
    {
        this.heartRate = heartRate;
    }

    public String getPreciseDate()
    {
        return preciseDate;
    }

    public void setPreciseDate(String preciseDate)
    {
        this.preciseDate = preciseDate;
    }

    @Override
    public String toString()
    {
        return "HeartRate{" +
                "id=" + id +
                ", heartRate='" + heartRate + '\'' +
                ", preciseDate='" + preciseDate + '\'' +
                '}';
    }
}
