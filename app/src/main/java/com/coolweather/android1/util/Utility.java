package com.coolweather.android1.util;

import android.text.TextUtils;

import com.coolweather.android1.db.City;
import com.coolweather.android1.db.Country;
import com.coolweather.android1.db.Province;
import com.coolweather.android1.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {
    public static boolean handleProvinceResponse(String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++)
                {
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++)
                {
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    City city=new City();
                    city.setCityName(provinceObject.getString("name"));
                    city.setCityCode(provinceObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountryResponse(String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++)
                {
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    Country country=new Country();
                    country.setCountryName(provinceObject.getString("name"));
                    country.setWeatherId(provinceObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String responseText)
    {
        try {
            JSONObject jsonObject=new JSONObject(responseText);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContetn=jsonArray.get(0).toString();
            return new Gson().fromJson(weatherContetn,Weather.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
