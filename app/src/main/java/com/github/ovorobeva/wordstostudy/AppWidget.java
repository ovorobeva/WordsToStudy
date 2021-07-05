package com.github.ovorobeva.wordstostudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.github.ovorobeva.wordstostudy.Words.setWords;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    private static final String ACTION_SCHEDULED_UPDATE = "android.appwidget.action.ACTION_SCHEDULED_UPDATE";
    private static final String TAG = "Custom logs";
    static final String PREFS_NAME = "com.github.ovorobeva.wordstostudy.NewAppWidget";
    public static final String PREFERENCES = "Preferences";

    private static Preferences preferences;

    //todo: to make "words" constant in string.xml
    static private List<String> text = new LinkedList<>();
    Scheduler scheduler;
    private PendingIntent service = null;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Opens the config activity by click on widget
        Intent configIntent = new Intent(context, ConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        configIntent.putExtra(PREFERENCES, (Parcelable) preferences);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        views.setOnClickPendingIntent(R.id.main_layout, pIntent);
        //todo: to fix back button


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void updateTextAppWidget(Context context, AppWidgetManager appWidgetManager,
                                    int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        setWords(views, preferences);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            if (preferences.loadIdFromPref() == 0 &&
                    preferences.loadPeriodFromPref() == 0 &&
                    preferences.loadWordsCountFromPref() == 0) break;
            if (appWidgetId == preferences.loadIdFromPref())
                updateTextAppWidget(context, appWidgetManager, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d(TAG, "Update completed for widget ID " + appWidgetId);
        }
        scheduler = Scheduler.getScheduler();
        scheduler.scheduleNextUpdate(context, preferences);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        //Todo: to stop updating of deleted widgets, to delete all preferences and to kill the schedule

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        preferences = new Preferences(context);
        Log.d(TAG, "The first widget is created");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
            preferences.saveIdToPref(0);
            preferences.savePeriodToPref(0);
            preferences.saveWordsCountToPref(0);
        Log.d(TAG, "The last widget is disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            Log.d(TAG, "The time to update has come");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
            //todo: why to call onUpdate if we have scheduled update? To remove code from upd
            onUpdate(context, manager, ids);
        }

        super.onReceive(context, intent);
    }

}