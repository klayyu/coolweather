package com.coolweather.android;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.Country;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int  LEVEL_PROVINCE=0;
    public static final int  LEVEL_CITY=1;
    public static final int  LEVEL_COUNTRY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private List<Province> provincesList;
    private List<City> cityList;
    private List<Country> countryList;
    private Province selectProvince;
    private City selectCity;
    private int curLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=view.findViewById(R.id.title_text);
        backButton=view.findViewById(R.id.back_button);
        listView=view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(curLevel == LEVEL_PROVINCE)
                {
                    selectProvince=provincesList.get(position);
                    queryCitys();
                }
                else if(curLevel == LEVEL_CITY)
                {
                    selectCity=cityList.get(position);
                    queryCountrys();
                }
                else if(curLevel == LEVEL_COUNTRY)
                {
                    String weatherId = countryList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        //getActivity().finish();
                    }
                    else if(getActivity() instanceof WeatherActivity)
                    {
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curLevel == LEVEL_CITY)
                {
                    queryProvinces();
                }
                else if(curLevel == LEVEL_COUNTRY)
                {
                    queryCitys();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces()
    {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provincesList= DataSupport.findAll(Province.class);
        if(provincesList.size()>0)
        {
            dataList.clear();
            for(Province province:provincesList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel=LEVEL_PROVINCE;
        }
        else
        {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }



    private void queryCitys()
    {
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList= DataSupport.where("provinceid = ?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size()>0)
        {
            dataList.clear();
            for(City city:cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel=LEVEL_CITY;
        }
        else
        {
            int provinceId = selectProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceId;
            queryFromServer(address,"city");
        }
    }
    private void queryCountrys()
    {
        titleText.setText(selectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList= DataSupport.where("cityid=?",String.valueOf(selectCity.getId())).find(Country.class);
        if(countryList.size()>0)
        {
            dataList.clear();
            for(Country country:countryList)
            {
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            curLevel=LEVEL_COUNTRY;
        }
        else
        {
            int provinceId = selectProvince.getProvinceCode();
            int cityId=selectCity.getCityCode();

            String address="http://guolin.tech/api/china/"+provinceId+"/"+cityId;
            queryFromServer(address,"country");
        }
    }
    private void queryFromServer(String address,final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type))
                {
                    result= Utility.handleProvinceResponse(responseText);
                }
                else if("city".equals(type))
                {
                    result= Utility.handleCityResponse(responseText,selectProvince.getId());
                }
                else if("country".equals(type))
                {
                    result= Utility.handleCountryResponse(responseText,selectCity.getId());
                }
                if(result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type))
                            {
                                queryProvinces();
                            }
                            else if("city".equals(type))
                            {
                                queryCitys();
                            }
                            else if("country".equals(type))
                            {
                                queryCountrys();
                            }
                        }
                    });
                }
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog == null)
        {
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}
