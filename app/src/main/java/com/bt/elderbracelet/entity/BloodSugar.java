package com.bt.elderbracelet.entity;

/**
 * Created by Administrator on 2017/9/8.
 */

public class BloodSugar {

    private int id;
    private String preciseDate;
    private String bloodSugarBefore;//餐前血糖
    private String bloodSugarAfter;//餐后血糖

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

    public String getBloodSugarBefore()
    {
        return bloodSugarBefore;
    }

    public void setBloodSugarBefore(String bloodSugarBefore)
    {
        this.bloodSugarBefore = bloodSugarBefore;
    }

    public String getBloodSugarAfter()
    {
        return bloodSugarAfter;
    }

    public void setBloodSugarAfter(String bloodSugarAfter)
    {
        this.bloodSugarAfter = bloodSugarAfter;
    }

    @Override
    public String toString()
    {
        return "BloodSugar{" +
                "id=" + id +
                ", preciseDate='" + preciseDate + '\'' +
                ", bloodSugarBefore='" + bloodSugarBefore + '\'' +
                ", bloodSugarAfter='" + bloodSugarAfter + '\'' +
                '}';
    }
}
