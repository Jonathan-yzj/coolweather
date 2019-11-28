package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    private int id;
    private String countyName;              //记录县的名字
    private String weatherId;               //记录县对应的天气
    private String cityId;                  //记录当前县所属市的ID

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }
    public void setCountyName(String countyName){
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }
    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCityId() {
        return cityId;
    }
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
