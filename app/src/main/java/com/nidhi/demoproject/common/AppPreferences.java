package com.nidhi.demoproject.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by nidhi on 8/17/2017.
 */

public class AppPreferences {

    private static SharedPreferences sharedPreferences;

    private static SharedPreferences.Editor editor;

    private static final AppPreferences ourInstance = new AppPreferences();

    public static AppPreferences getInstance(Context context) {
        if (sharedPreferences==null){

            sharedPreferences = context.getSharedPreferences("DemoProject",Context.MODE_PRIVATE);

            if(editor == null){

                editor = sharedPreferences.edit();
            }

        }
        return ourInstance;
    }

    private AppPreferences() {
    }

    public void addStringToStore(String key,String value){
       if(editor!=null) {
           editor.putString(key, value);
           editor.commit();
       }
    }

    public String getStringFromStore(String key) {
        String value = "";
        if (sharedPreferences != null) {
            value = sharedPreferences.getString(key, "");
        }
        return value;
    }
    //int
    public void addIntToStore(String key,int value){
        if(editor!=null){
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public int getIntFromStore(String key){
        int value = 0;
        if(sharedPreferences!=null){
            value = sharedPreferences.getInt(key,0);
        }
        return value;
    }

    //long
    public void addLongToStore(String key,long value){
        if(editor!=null){
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public long getLongFromStore(String key){
        long value = 0;
        if(sharedPreferences!=null){
            value = sharedPreferences.getLong(key,0L);
        }
        return value;
    }

    //boolean

    public void addBooleanToStore(String key,boolean value){
        if(editor!=null){
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public boolean getBooleanFromStore(String key){
        boolean value = false;
        if(sharedPreferences!=null){
            value = sharedPreferences.getBoolean(key,false);
        }
        return value;
    }
}
