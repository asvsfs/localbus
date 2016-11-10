package amir.app.business;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import amir.app.business.models.Customer;
import amir.app.business.models.Token;

/**
 * Created by amin on 11/01/2016.
 */

public class config {
    public static Token token;
    public static Customer customer;

    public static void setValue(Context context, String key, String value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(key, value).commit();
    }

    public static void setValue(Context context, String key, boolean value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(key, value).commit();
    }

    public static String getValue(Context context, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, "");
    }

    public static boolean getValueAsBool(Context context, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(key, false);
    }

}


