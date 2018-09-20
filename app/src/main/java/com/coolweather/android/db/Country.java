package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

public class Country extends DataSupport {
    private int id;
    private String countryName;
    private int cityId;
    private String weatherId;

    public int getId(){return id;}
    public void setId(int id){this.id=id;}
    public String getCountryName(){return countryName;}
    public void setCountryName(String name){this.countryName=name;}
    public int getCityId(){return cityId;}
    public void setCityId(int cid){this.cityId=cid;}
    public String getWeatherId(){return weatherId;}
    public void setWeatherId(String wid){this.weatherId=wid;}
}
