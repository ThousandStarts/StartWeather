package com.example.startweather;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.startweather.gson.Forecast;
import com.example.startweather.gson.Lifestyle;
import com.example.startweather.gson.Weather;
import com.example.startweather.util.HttpUtil;
import com.example.startweather.util.LogUtil;
import com.example.startweather.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private  TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefresh;

    private String mWeatherId;

    public DrawerLayout drawerLayout;

    private Button navButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //拿到当前的活动，把活动的布局显示在状态栏上，在把状态栏变为透明色
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //初始化控件
        weatherLayout =(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout) findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary);//用主题颜色来代替刷新进度条颜色

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        //bingPicImg.setVisibility(View.GONE);

        //读取数据
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        String bingPic=prefs.getString("bing_pic",null);

        //if(weatherString!=null){
        if(false){
            LogUtil.d("activity_weather:",weatherString);
            Weather weather= Utility.handleWeatherResponse(weatherString);
            //存放城市
            mWeatherId=weather.basic.location;
            showWeatherInfo(weather);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");
            mWeatherId=getIntent().getStringExtra("weather_id");
            LogUtil.d("activity_weather2:",weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        //if(bingPic!=null){
        if(false){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }

        /**
         * 下拉刷新
         */
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        /**
         * 显示城市查询
         */
        navButton.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
             }
        });


    }

    /**
     * 根据天气ID请求城市天气信息
     */

    public void requestWeather(final String weatherId){
        //常规天气数据集合
        String weatherUrl="https://free-api.heweather.com/s6/weather?location="+weatherId+"&key=c3d981c86f1f4167a9f0cd2e47ec0f00";
        LogUtil.d("requestWeather:",weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        //关闭刷新进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                LogUtil.d("onResponse",responseText);

                final  Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            //存储数据
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            //获得刷新城市
                            mWeatherId=weather.basic.location;
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        //关闭刷新进度条
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather){
        //城市
        String cityName=weather.basic.location;
        //当地跟新时间 yyyy-MM-dd hh:mm ,并截取时分
        String updaateTime=weather.update.loc.split(" ")[1];
        //获得当天最高低温度
        String degree=weather.now.tmp;
        //天气状况
        String weatherInfo=weather.now.cond_txt;

        //删除ScrollView中的所有线性布局
        forecastLayout.removeAllViews();
        int i=0;
        //为ScrollView添加线性布局
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=view.findViewById(R.id.data_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView maxText=view.findViewById(R.id.max_text);
            TextView minText=view.findViewById(R.id.min_text);
            //当天数据
            /*if(i++<1){
                degree=forecast.tmp_max+"/"+forecast.tmp_min+"℃";
                //判断是白天还是晚上
                if(TimeComparison(weather.update.loc)){//白天
                    LogUtil.e("白天还是夜晚","白天");
                    weatherInfo=forecast.ctd;
                }else{//晚上
                    LogUtil.e("白天还是夜晚","夜晚");
                    weatherInfo=forecast.ctn;
                }
            }*/
            //日期
            dateText.setText(forecast.date);
            //天气状况(默认白天)
            infoText.setText(forecast.ctd);
            //最高温
            maxText.setText(forecast.tmp_max);
            //最低温
            minText.setText(forecast.tmp_min);
            forecastLayout.addView(view);
        }

        //UI修改
        titleCity.setText(cityName);
        //不好看不加了
        titleUpdateTime.setText(updaateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //暂时固定先
        if(true){
            //AQI
            aqiText.setText("63");
            //PM2.5
            pm25Text.setText("28");
        }

        if(weather.lifestyleList.size()>0){
            comfortText.setText("生活指数类型："+weather.lifestyleList.get(0).txt);
            carWashText.setText("舒适度指数："+weather.lifestyleList.get(1).txt);
            sportText.setText("感冒指数："+weather.lifestyleList.get(2).txt);
        }

        //显示
        weatherLayout.setVisibility(View.VISIBLE);
    }

    //时间比较
    private boolean TimeComparison(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//年-月-日 时-分
        try {
            Date startTime = dateFormat.parse(time.toString());//开始时间
            String End = time.split(" ")[0]+" 18:00";
            Date EndTime = dateFormat.parse(End);//结束时间

            if (startTime.getTime()<EndTime.getTime()){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    //加载必应每日一图
    private void loadBingPic(){
        String requesrBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requesrBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final  String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bingPic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

}
