package com.example.startweather.util;

import android.util.Log;

public class LogUtil {
    private static final int VERBOSE=1;
    private static final int DEBUG=2;
    private static final int INFO=3;
    private static final int WARN=4;
    private static final int ERROR=5;
    private static final int NOTHING=6;
    private static final int level=VERBOSE;

    public static void v(String tag,String mag){
        if(level<=VERBOSE){
            Log.v(tag,mag);
        }
    }

    public static void d(String tag,String mag){
        if(level<=DEBUG){
            Log.d(tag,mag);
        }
    }

    public static void i(String tag,String mag){
        if(level<=INFO){
            Log.i(tag,mag);
        }
    }

    public static void w(String tag,String mag){
        if(level<=WARN){
            Log.w(tag,mag);
        }
    }

    public static void e(String tag,String mag){
        if(level<=ERROR){
            Log.e(tag,mag);
        }
    }

}
