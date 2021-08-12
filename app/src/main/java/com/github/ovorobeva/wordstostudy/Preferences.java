package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ExpandableListView;

import java.util.Calendar;

import static com.github.ovorobeva.wordstostudy.ConfigureActivity.EVERY_DAY;

public class Preferences implements Parcelable {

    public static final String WORDS_COUNT = "wordscount";
    public static final String PERIOD = "period";
    public static final String NEXT = "next_update";
    public static final String LAST = "last_update";
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Preferences> CREATOR = new Parcelable.Creator<Preferences>() {
        @Override
        public Preferences createFromParcel(Parcel in) {
            return new Preferences(in);
        }

        @Override
        public Preferences[] newArray(int size) {
            return new Preferences[size];
        }
    };
    private static final String PREFS_NAME = "com.github.ovorobeva.wordstostudy.NewAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final int DEFAULT_COUNT = 3;
    private static final Object OBJECT = new Object();
    private static Preferences preferences;
    private final Context context;

/*
    private Preferences(Context context) {
        this.context = context;
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.clear();
        prefs.apply();
    }*/
    //todo: to collapse all the saves

    protected Preferences(Parcel in) {
        context = (Context) in.readValue(Context.class.getClassLoader());
    }

/*    public static Preferences getPreferences(Context context) {
        if (preferences != null)
            return preferences;

        synchronized (OBJECT) {
            if (preferences == null)
                preferences = new Preferences(context);
            return preferences;
        }
    }*/

    public static void clearPrefs(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.clear();
        prefs.apply();
    }

    public static boolean arePrefsEmpty(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (!(prefs.contains(PREF_PREFIX_KEY + "period")
                || prefs.contains(PREF_PREFIX_KEY + "wordscount")
                || prefs.contains(PREF_PREFIX_KEY + "last_update")
                || prefs.contains(PREF_PREFIX_KEY + "next_update")))
            return true;
        return false;
    }

    public static void saveSettingToPref(int value, String parameter, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + parameter, value);
        prefs.apply();

    }


    public static void saveUpdateTimeToPref(Calendar schedule, String type, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + type, schedule.getTimeInMillis());
        prefs.apply();
    }


    public static void saveWordsColorToPref(int color, int id, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + id + "_color", color);
        prefs.apply();
    }

    public static void saveWordsToPref(String words, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY  + "_words", words);
        prefs.apply();
    }

    public static int loadColorFromPref(int id, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (prefs.contains(PREF_PREFIX_KEY + id + "_color"))
            return prefs.getInt(PREF_PREFIX_KEY + id + "_color", Color.WHITE);
        else return 1;

    }

    public static String loadWordsFromPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            return prefs.getString(PREF_PREFIX_KEY  + "_words", String.valueOf(R.class.getResource("appwidget_text")));
    }

    public static long loadUpdateTimeFromPref(String type, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_KEY + type, Calendar.getInstance().getTimeInMillis());
    }

    public static int loadSettingFromPref(String parameter, Context context) {
        int defaultValue = 0;
        if (parameter.equals("wordscount")) defaultValue = DEFAULT_COUNT;
        else defaultValue = EVERY_DAY;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY + parameter, defaultValue);

    }

    public static void deleteWordsColorFromPref(int id, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + id + "_color");
        prefs.apply();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(context);
    }
}