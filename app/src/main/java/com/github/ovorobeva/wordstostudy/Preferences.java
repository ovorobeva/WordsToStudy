package com.github.ovorobeva.wordstostudy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ExpandableListView;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.ovorobeva.wordstostudy.AppWidget.TAG;
import static com.github.ovorobeva.wordstostudy.ConfigureActivity.EVERY_DAY;

public class Preferences implements Parcelable{

    public static final String WORDS_COUNT = "wordscount";
    public static final String PERIOD = "period";
    public static final String NEXT = "next_update";
    public static final String LAST = "last_update";
    public static final String IS_COLOR_CHANGED = "isColorChanged";
    public static final String IS_WORD_COUNT_CHANGED = "isWordCountChanged";
    public static final String IS_PERIOD_CHANGED = "isPeriodChanged";

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
    private final Context context;


    protected Preferences(Parcel in) {
        context = (Context) in.readValue(Context.class.getClassLoader());
    }

    public static void clearPrefs(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.clear();
        saveWordsToPref(context.getResources().getString(R.string.appwidget_text), context);
        prefs.apply();
        Log.d(TAG, "clearPrefs: prefs are cleared");
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

    public static void saveTextFontStyleToPref(Set<String> fontStyles, int id, Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putStringSet(PREF_PREFIX_KEY + id + "_fontStyle", fontStyles);
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
            return prefs.getInt(PREF_PREFIX_KEY + id + "_color", context.getResources().getColor(R.color.white));
        else return context.getResources().getColor(R.color.white);

    }

    public static Set<String> loadTextFontStyleFromPref(int id, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            return prefs.getStringSet(PREF_PREFIX_KEY + id + "_fontStyle", new HashSet<>());
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
        int defaultValue;
        if (parameter.equals(WORDS_COUNT)) defaultValue = DEFAULT_COUNT;
        else defaultValue = EVERY_DAY;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
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