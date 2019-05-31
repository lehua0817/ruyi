package com.bt.elderbracelet.entity;

import java.io.Serializable;

public class Sport implements Serializable {

    /**
     *  id 号有什么用？
     *  id 号不是人为设定的，而是将这条数据，保存到数据库中，数据库会自动给
     *  这条数据加上一个编号，然后我们从数据库中查询数据时，就把这个编号设为这条数据的id号
     *
     *  因为我们设置删除该数据的条件是根据数据库编号来删除，所以 现在拿到了id号，
     *  id号就是编号，就可以删除数据
     */

    private int id;
    private String step;          //步数
    private String distance;       //运动总距离
    private String calorie;        //卡路里
    private String sportTime;        //运动时间
    private String date;    //日期     日期形式为 2017-08-01



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getStep()
    {
        return step;
    }

    public void setStep(String step)
    {
        this.step = step;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getCalorie()
    {
        return calorie;
    }

    public void setCalorie(String calorie)
    {
        this.calorie = calorie;
    }

    public String getSportTime()
    {
        return sportTime;
    }

    public void setSportTime(String sportTime)
    {
        this.sportTime = sportTime;
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
        return "Sport{" +
                "id=" + id +
                ", step='" + step + '\'' +
                ", distance='" + distance + '\'' +
                ", calorie='" + calorie + '\'' +
                ", sportTime='" + sportTime + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public Sport(String step, String distance, String calorie, String sportTime, String date)
    {
        this.step = step;
        this.distance = distance;
        this.calorie = calorie;
        this.sportTime = sportTime;
        this.date = date;
    }

    public Sport()
    {
    }
}
