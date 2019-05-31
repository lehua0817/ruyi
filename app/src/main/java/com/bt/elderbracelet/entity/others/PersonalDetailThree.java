package com.bt.elderbracelet.entity.others;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class PersonalDetailThree implements Serializable {

    private String illness;
    private String physique;  //各人体质

    public String getIllness()
    {
        return illness;
    }

    public void setIllness(String illness)
    {
        this.illness = illness;
    }

    public String getPhysique()
    {
        return physique;
    }

    public void setPhysique(String physique)
    {
        this.physique = physique;
    }
}
