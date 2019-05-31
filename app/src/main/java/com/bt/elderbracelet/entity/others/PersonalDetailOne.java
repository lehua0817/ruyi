package com.bt.elderbracelet.entity.others;


import java.io.Serializable;

/**
 * Created by pendragon on 17-4-14.
 */

public class PersonalDetailOne implements Serializable {

    private String nation;      //民族
    private String education;   //教育背景
    private String occupation; //从事职业
    private String address;
    private boolean watchHealthTv;

    public String getNation()
    {
        return nation;
    }

    public void setNation(String nation)
    {
        this.nation = nation;
    }

    public String getEducation()
    {
        return education;
    }

    public void setEducation(String education)
    {
        this.education = education;
    }

    public String getOccupation()
    {
        return occupation;
    }

    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public boolean isWatchHealthTv()
    {
        return watchHealthTv;
    }

    public void setWatchHealthTv(boolean watchHealthTv)
    {
        this.watchHealthTv = watchHealthTv;
    }

    @Override
    public String toString()
    {
        return "PersonalDetailOne{" +
                "nation='" + nation + '\'' +
                ", education='" + education + '\'' +
                ", occupation='" + occupation + '\'' +
                ", address='" + address + '\'' +
                ", watchHealthTv=" + watchHealthTv +
                '}';
    }
}
