package com.bt.elderbracelet.entity.others;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by pendragon on 17-4-14.
 */

public class PersonalDetailTwo implements Serializable {

    private String hobby;   //兴趣爱好
    private String sport; //运动情况
    private String diet;
    private String smoke;
    private String drink;
    private String allergic;  //过敏情况

    public String getHobby()
    {
        return hobby;
    }

    public void setHobby(String hobby)
    {
        this.hobby = hobby;
    }

    public String getSport()
    {
        return sport;
    }

    public void setSport(String sport)
    {
        this.sport = sport;
    }

    public String getDiet()
    {
        return diet;
    }

    public void setDiet(String diet)
    {
        this.diet = diet;
    }

    public String getSmoke()
    {
        return smoke;
    }

    public void setSmoke(String smoke)
    {
        this.smoke = smoke;
    }

    public String getDrink()
    {
        return drink;
    }

    public void setDrink(String drink)
    {
        this.drink = drink;
    }

    public String getAllergic()
    {
        return allergic;
    }

    public void setAllergic(String allergic)
    {
        this.allergic = allergic;
    }

    @Override
    public String toString()
    {
        return "PersonalDetailTwo{" +
                "hobby='" + hobby + '\'' +
                ", sport='" + sport + '\'' +
                ", diet='" + diet + '\'' +
                ", smoke='" + smoke + '\'' +
                ", drink='" + drink + '\'' +
                ", allergic='" + allergic + '\'' +
                '}';
    }
}
