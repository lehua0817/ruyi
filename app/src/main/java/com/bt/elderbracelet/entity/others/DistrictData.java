package com.bt.elderbracelet.entity.others;

/**
 * Created by zen on 16-3-26.
 */
public class DistrictData {

    /**
     * Id : 1
     * DisName : 东城区
     * CityID : 1
     * DisSort : null
     */

    private int Id;
    private String DisName;
    private int CityID;
    private Object DisSort;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getDisName() {
        return DisName;
    }

    public void setDisName(String DisName) {
        this.DisName = DisName;
    }

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int CityID) {
        this.CityID = CityID;
    }

    public Object getDisSort() {
        return DisSort;
    }

    public void setDisSort(Object DisSort) {
        this.DisSort = DisSort;
    }
}
