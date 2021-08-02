package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

public class Preferences implements Parcelable {

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

    public void saveWordsCountToPref(int count) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + "wordscount", count);
        prefs.apply();
    }

    public void saveWordsColorToPref(int color, int id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + id + "_color", color);
        prefs.apply();
    }

    public int loadColorFromPref(int id) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (prefs.contains(PREF_PREFIX_KEY + id + " color"))
            return prefs.getInt(PREF_PREFIX_KEY + id + "_color", 1);
        else return 1;

    }

    public int loadFromPref(String parameter) {
        int defaultValue = 0;
        if (parameter.equals("wordscount")) defaultValue = DEFAULT_COUNT;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        //todo: to make default not an every day. How to place null instead?
        return prefs.getInt(PREF_PREFIX_KEY + parameter, defaultValue);

    }


    public void deleteWordsColorFromPref(int color, int id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + id + "_color");
        prefs.apply();
    }



    protected Preferences(Parcel in) {
        context = (Context) in.readValue(Context.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(context);
    }

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
}