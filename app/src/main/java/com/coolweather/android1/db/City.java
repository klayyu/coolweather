package com.coolweather.android1.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int provinceId;
    private int cityCode;
    public int getId(){return id;}
    public void setId(int id){this.id=id;}
    public String getCityName(){return cityName;}
    public void setCityName(String name){this.cityName=name;}
    public int getProvinceId(){return provinceId;}
    public void setProvinceId(int pid){this.provinceId=pid;}
    public int getCityCode(){return cityCode;}
    public void setCityCode(int code){this.cityCode=code;}
}
