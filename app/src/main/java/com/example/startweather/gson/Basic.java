package com.example.startweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("cid")
    public String cidid;
    public String location;
    @SerializedName("parent_city")
    public String cityName;
    public String admin_area;
    public String cnty;
    public String lat;
    public String lon;
    public String tz;
}
