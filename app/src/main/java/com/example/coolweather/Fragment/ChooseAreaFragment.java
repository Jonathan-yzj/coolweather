package com.example.coolweather.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolweather.R;
import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HTTPUtil;
import com.example.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /*省列表*/
    private  List<Province> provinceList;
    /*市列表*/
    private  List<City> cityList;
    /*县列表*/
    private  List<County> countyList;


    /*选中的省份*/
    private  Province selectedProvince;
    /*选中的城市*/
    private  City selectedCity;
    /*当前选中的级别*/
    private  int currentLevel;


    //onCreateView（）方法中先是获取到了一些控件的实例，然后初始化了ArrayAdapter，并将它设置为ListView的适配器
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        //获取到了一些控件的实例
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //然后初始化了ArrayAdapter，并将它设置为ListView的适配器
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_expandable_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    //onActivityCreated()方法中给ListView和button设置了点击事件，到这里初始化完成
    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //listview的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        //button的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        //queryProvinces（）方法从此处开始加载省级数据
        queryProvinces();
    }

    /*查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询*/
        private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province :provinceList){
                dataList.add(province.getProvinveName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            //调用LitePal的查询接口从数据库中读取省级数据
            String address = "http://guolin.tech/api/china";
            //然后调用queryFromServer()方法从服务器上查询数据
            queryFromServer(address,"province");
        }
    }

    /*查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询*/
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinveName());
        Log.e("asd","----------------sss:"+String.valueOf(selectedProvince.getId()));
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        Log.e("asd","----------------cityList.size:"+cityList.size());
        if (cityList.size() > 0){
            dataList.clear();
            for (City city :cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            Log.e("asd","----------------provinceCode:"+provinceCode);
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /*查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询*/
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county :countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");
        }
    }

    /*根据传入的地址和类型从服务器上查询省市县的数据*/
    private void queryFromServer(String address,final String type) {
        showProgressDialog();
        //调用HTTPUtil中的sendOkHttpRequert（）方法向服务器发送请求，响应数据会调到onResponse（）方法中
        HTTPUtil.sendOkHttpRequert(address, new Callback() {
            //响应数据会调到onResponse（）方法中
            @Override
            public void onResponse(Call call, Response response)throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                //调用Utility中的handleProvinceResponse（）方法解析和处理服务器返回的数据，并储存到数据库中
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.hendleCityResponse(responseText,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountResponse(responseText,selectedCity.getId());
                }
                if (result){
                    //再次调用queryProvinces()方法，由于该方法牵扯到了UI操作因此必须在主线程中调用，所以用到了runOnUiThread（）方法实现从子线程切换到主线程
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            //根据级别判断调用以下哪种方法
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    /*显示进度对话框*/
    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /*关闭进度对话框*/
    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }


}
