package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String PREFS_NAME = "com.github.ovorobeva.wordstostudy.NewAppWidget";
    // /data/user/0/com.github.ovorobeva.wordstostudy/shared_prefs/com.github.ovorobeva.wordstostudy.NewAppWidget.xml
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int DEFAULT_COUNT = 3;
    private final Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    public void savePeriodToPref(int period) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + "period", period);
        prefs.apply();
    }


    public void saveIdToPref(int mAppWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        if (loadIdFromPref() == 0 || mAppWidgetId == 0) {
            prefs.putInt(PREF_PREFIX_KEY + "id", mAppWidgetId);
        }
        prefs.apply();
    }
    public void saveWordsCountToPref(int count) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + "wordscount", count);
        prefs.apply();
    }
    public int loadPeriodFromPref() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME + "period", 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY, 0);
    }

    //Here is the example how to use prefs according to the definite widget id
  /*  static int loadPeriodFromPref(Context context, int mAppWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + mAppWidgetId, EVERY_DAY);
    }*/
    public int loadWordsCountFromPref() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY + "wordscount", DEFAULT_COUNT);
    }

    public int loadIdFromPref() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY + "id", 0);
    }


}
