package com.bt.elderbracelet.entity;

/**
 * Created by Administrator on 2017/9/8.
 */

public class BloodOxygen {
    private int id;
    private String preciseDate;
    private String bloodOxygen;

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

    public String getBloodOxygen()
    {
        return bloodOxygen;
    }

    public void setBloodOxygen(String bloodOxygen)
    {
        this.bloodOxygen = bloodOxygen;
    }


    @Override
    public String toString()
    {
        return "BloodOxygen{" +
                "id=" + id +
                ", preciseDate='" + preciseDate + '\'' +
                ", bloodOxygen='" + bloodOxygen + '\'' +
                '}';
    }
}
