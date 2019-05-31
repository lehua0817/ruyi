package com.bt.elderbracelet.entity.others;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by pendragon on 17-4-17.
 */

public class PushMessage implements Serializable {


    private String id;
    private String title;
    private String brief;
    private String time;
    private String data = "";
    private boolean isCollected ;
    private boolean isLooked ;
    private String titleColor ;
    private int titleStrong ;

    public String getTitleColor()
    {
        return titleColor;
    }

    public void setTitleColor(String titleColor)
    {
        this.titleColor = titleColor;
    }

    public int getTitleStrong()
    {
        return titleStrong;
    }

    public void setTitleStrong(int titleStrong)
    {
        this.titleStrong = titleStrong;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getBrief()
    {
        return brief;
    }

    public void setBrief(String brief)
    {
        this.brief = brief;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public boolean isCollected()
    {
        return isCollected;
    }

    public void setCollected(boolean collected)
    {
        isCollected = collected;
    }

    public boolean isLooked()
    {
        return isLooked;
    }

    public void setLooked(boolean looked)
    {
        isLooked = looked;
    }
}
