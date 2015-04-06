package com.example.polytech.orientatewatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Utility {

    public static double latitude;
    public static double longitude;
    public static String nextPageToken = null;
    public static String actualRange;
    public static String actualTrspt;
    public static String actualType;
    public static final String noResult = "Aucun résultat";

    public static String getPreferredRange(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_range_key),
                context.getString(R.string.pref_range_default));
    }

}

