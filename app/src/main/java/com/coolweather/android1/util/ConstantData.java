package com.coolweather.android1.util;

public class ConstantData {
    private static final String weatherKEy="f9e001f5c0804882baf719898c0c9493";
    public static final String requestBingPicUrl="http://guolin.tech/api/bing_pic";
    public static String getRequestWeatherUrl(String weatherId)
    {
        return "http://guolin.tech/api/weather?cityid="+weatherId+"&key="+ ConstantData.weatherKEy;
    }
}
