package com.example.startweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    @SerializedName("cond_code_d")
    public String ccd;
    @SerializedName("cond_code_n")
    public String ccn;
    @SerializedName("cond_txt_d")
    public String ctd;
    @SerializedName("cond_txt_n")
    public String ctn;
    public String date;
    public String hum;
    public String mr;
    public String ms;
    public String pcpn;
    public String pop;
    public String pres;
    public String sr;
    public String ss;
    public String tmp_max;
    public String tmp_min;
    public String uv_index;
    public String vis;
    public String wind_deg;
    public String wind_dir;
    public String wind_sc;
    public String wind_spd;
}
