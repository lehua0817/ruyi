package com.bt.elderbracelet.entity.others;

/**
 * Created by zen on 16-3-26.
 */
public class CityData {

    /**
     * CityID : 1
     * name : 北京市
     * ProID : 1
     * CitySort : 1
     */

    private int CityID;
    private String name;
    private int ProID;
    private int CitySort;

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int CityID) {
        this.CityID = CityID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProID() {
        return ProID;
    }

    public void setProID(int ProID) {
        this.ProID = ProID;
    }

    public int getCitySort() {
        return CitySort;
    }

    public void setCitySort(int CitySort) {
        this.CitySort = CitySort;
    }
}
