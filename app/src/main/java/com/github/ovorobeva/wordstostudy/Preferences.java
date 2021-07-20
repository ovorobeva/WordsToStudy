package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static final String ID = "id";
    public static final String WORDS_COUNT = "wordscount";
    public static final String PERIOD = "period";
    private static final String PREFS_NAME = "com.github.ovorobeva.wordstostudy.NewAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int DEFAULT_COUNT = 3;
    private static final Object OBJECT = new Object();
    private static Preferences preferences;
    private final Context context;

    private Preferences(Context context) {
        this.context = context;
    }


    public static Preferences getPreferences(Context context) {
        if (preferences != null)
            return preferences;

        synchronized (OBJECT) {
            if (preferences == null)
                preferences = new Preferences(context);
            return preferences;
        }
    }

    public void savePeriodToPref(int period) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + "period", period);
        prefs.apply();
    }

    public void saveIdToPref(int mAppWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        if (loadFromPref(ID) == 0 || mAppWidgetId == 0) {
            prefs.putInt(PREF_PREFIX_KEY + "id", mAppWidgetId);
        }
        prefs.apply();
    }

    public void saveWordsCountToPref(int count) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + "wordscount", count);
        prefs.apply();
    }

    public void saveWordsColorToPref(int color, int id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + id + " color", color);
        prefs.apply();
    }

    public int loadColorFromPref(int id) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (prefs.contains(PREF_PREFIX_KEY + id + " color"))
            return prefs.getInt(PREF_PREFIX_KEY + id + " color", 1);
        else return 1;

    }

    public int loadFromPref(String parameter) {
        int defaultValue = 0;
        if (parameter.equals("wordscount")) defaultValue = DEFAULT_COUNT;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY + parameter, defaultValue);

    }

}
