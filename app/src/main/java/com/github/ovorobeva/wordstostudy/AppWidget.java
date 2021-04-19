package com.github.ovorobeva.wordstostudy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    //todo: to make "words" constant in string.xml
    private static final String ACTION_SCHEDULED_UPDATE = "android.appwidget.action.ACTION_SCHEDULED_UPDATE";
    static private List<String> text;
    static private boolean isFirstUpdate = true;
    Words words;
    Schedule schedule;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
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

    void updateTextForWidget(Context context, AppWidgetManager appWidgetManager,
                             int appWidgetId) {
        // Construct the RemoteViews object

        text = words.getWords();
        Log.d(AppWidget.class.getCanonicalName() + ".updateAppWidget", "Getword called: New text value for the widget ID " + appWidgetId + " is: " + text);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.appwidget_text, text.toString());
        Log.d(AppWidget.class.getCanonicalName() + ".updateAppWidget", "New text set to the widget ID " + appWidgetId + ". The new word is: " + text);
        //todo: to fix back button


        // Instruct the widget manager to update the widget
        schedule.scheduleNextUpdate(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.d(AppWidget.class.getCanonicalName() + ".updateAppWidget", "Widget ID " + appWidgetId + " is updated");

    }
//todo: to fix the schedule update. Didn't called until the next update

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (isFirstUpdate) {
            words = new Words(context);
            schedule = new Schedule();
            isFirstUpdate = false;
        } else {
            // There may be multiple widgets active, so update all of them
            for (int appWidgetId : appWidgetIds) {
                updateTextForWidget(context,appWidgetManager, appWidgetId);
                updateAppWidget(context, appWidgetManager, appWidgetId);
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                Log.d(AppWidget.class.getCanonicalName() + ".onUpdate", "Update completed for widget ID " + appWidgetId);
            }
        }
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
//        schedule.cancelSchedule();
 //       words = null;
   //     System.gc();
        Log.d(AppWidget.class.getCanonicalName() + ".onDisabled", "The last widget is disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            Log.d(AppWidget.class.getCanonicalName() + ".onReceive", "The time to update has come");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
            //todo: why to call onUpdate if we have scheduled update? To remove code from upd
            for (int id : ids) {
                updateTextForWidget(context, manager, id);
            }
        }

    }

}