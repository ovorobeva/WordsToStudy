package com.github.ovorobeva.wordstostudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.github.ovorobeva.wordstostudy.Words.getWords;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    private static final String ACTION_SCHEDULED_UPDATE = "android.appwidget.action.ACTION_SCHEDULED_UPDATE";
    //todo: to make "words" constant in string.xml
    static private List<String> text = new LinkedList<>();
    private PendingIntent service = null;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object

        getWords(context, text);
        Log.d(AppWidget.class.getCanonicalName() + ".updateAppWidget", "Getword called: New text value for the widget ID " + appWidgetId + " is: " + text);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.appwidget_text, text.toString());
        Log.d(AppWidget.class.getCanonicalName() + ".updateAppWidget", "New text set to the widget ID " + appWidgetId + ". The new word is: " + text);


        // Opens the config activity by click on widget
        Intent configIntent = new Intent(context, ConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        views.setOnClickPendingIntent(R.id.main_layout, pIntent);
        //todo: to fix back button


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.d(AppWidget.class.getCanonicalName() + ".updateAppWidget", "Widget ID " + appWidgetId + " is updated");

    }
//todo: to fix the schedule update. Didnt called until the next update
    private static void _scheduleNextUpdate(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Substitute AppWidget for whatever you named your AppWidgetProvider subclass
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar schedule = Calendar.getInstance();
        //Here is the example how to use prefs for the every widget
       // int period = ConfigureActivity.loadPeriodFromPref(context, appWidgetId);
        int period = ConfigureActivity.loadPeriodFromPref(context);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, schedule.getTimeInMillis() + period, pendingIntent);
        Log.d(AppWidget.class.getCanonicalName() + "._scheduleNextUpdate", "Schedule for updating is set. The next update will be done in " + period + "ms");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);


            Log.d(AppWidget.class.getCanonicalName() + ".onUpdate", "Update completed for widget ID " + appWidgetId);
        }
        _scheduleNextUpdate(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        //Todo: to stop updating of deleted widgets, to delete all preferences and to kill the schedule
        for (int appWidgetId : appWidgetIds) {
            ConfigureActivity.deletePeriodFromPref(context, appWidgetId);
            Log.d(AppWidget.class.getCanonicalName() + ".onDeleted", "Widget ID " + appWidgetId + " is deleted");
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(AppWidget.class.getCanonicalName() + ".onEnabled", "The first widget is created");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(AppWidget.class.getCanonicalName() + ".onDisabled", "The last widget is disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            Log.d(AppWidget.class.getCanonicalName() + ".onReceive", "The time to update has come");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
            //todo: why to call onUpdate if we have scheduled update? To remove code from upd
            onUpdate(context, manager, ids);
        }

        super.onReceive(context, intent);
    }

}