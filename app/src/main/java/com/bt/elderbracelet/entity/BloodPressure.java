package com.bt.elderbracelet.entity;

/**
 * Created by Administrator on 2017/9/8.
 */

public class BloodPressure {

    private int id;
    private String preciseDate;
    private String bloodPressureHigh;//高压
    private String bloodPressureLow;//低压

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getPreciseDate()
    {
        return preciseDate;
    }

    public void setPreciseDate(String preciseDate)
    {
        this.preciseDate = preciseDate;
    }

    public String getBloodPressureHigh()
    {
        return bloodPressureHigh;
    }

    public void setBloodPressureHigh(String bloodPressureHigh)
    {
        this.bloodPressureHigh = bloodPressureHigh;
    }

    public String getBloodPressureLow()
    {
        return bloodPressureLow;
    }

    public void setBloodPressureLow(String bloodPressureLow)
    {
        this.bloodPressureLow = bloodPressureLow;
    }

    @Override
    public String toString()
    {
        return "BloodPressure{" +
                "id=" + id +
                ", preciseDate='" + preciseDate + '\'' +
                ", bloodPressureHigh='" + bloodPressureHigh + '\'' +
                ", bloodPressureLow='" + bloodPressureLow + '\'' +
                '}';
    }
}
