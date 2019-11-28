package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private int id;                     //每个实体类都应该有的字段
    private String provinveName;        //记录省的名字
    private int provinceCode;           //记录省的代号

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getProvinveName(){
        return provinveName;
    }
    public void setProvinveName(String provinveName){
        this.provinveName = provinveName;
    }
    public int getProvinceCode(){
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
