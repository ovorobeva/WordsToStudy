package com.github.ovorobeva.wordstostudy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import static com.github.ovorobeva.wordstostudy.Preferences.PERIOD;
import static com.github.ovorobeva.wordstostudy.Preferences.WORDS_COUNT;
import static com.github.ovorobeva.wordstostudy.Scheduler.ACTION_SCHEDULED_UPDATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    static final String TAG = "Custom logs";
    private static Preferences preferences;
    private final Scheduler scheduler = Scheduler.getScheduler();
    private boolean isScheduledUpdate = false;
    private boolean isFirstUpdate = true;
    //todo: to save words into preferences and compare if words exist, load them, otherwise get new

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Preferences preferences) {
        Intent configIntent = new Intent(context, ConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        AppWidget.preferences = preferences;
        int color = preferences.loadColorFromPref(appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        views.setOnClickPendingIntent(R.id.main_layout, pIntent);

        if (color == Color.BLACK || color == Color.WHITE)
            views.setTextColor(R.id.words_edit_text, color);
        //todo: to fix back button
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void updateTextAppWidget(Context context, AppWidgetManager appWidgetManager) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        int wordsCount = preferences.loadFromPref(WORDS_COUNT);

        WordsClient wordsClient = WordsClient.getWordsClient();
        wordsClient.getWords(wordsCount, context, appWidgetManager, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        if (preferences == null)
            preferences = Preferences.getPreferences(context);

        Log.d(TAG, "onUpdate: prefs are: " + preferences);
        for (int appWidgetId : appWidgetIds) {
            if (preferences.loadFromPref(PERIOD) == 0 &&
                    preferences.loadFromPref(WORDS_COUNT) == 0) {
                isScheduledUpdate = false;
                break;
            }
            if (isScheduledUpdate || isFirstUpdate) {
                updateTextAppWidget(context, appWidgetManager);
                isScheduledUpdate = false;
                isFirstUpdate = false;
            }
            updateAppWidget(context, appWidgetManager, appWidgetId, preferences);
            Log.d(TAG, "Update completed for widget ID " + appWidgetId);
        }
        //todo: to make reSchedule after setting new value: cancel schedule if there were any change in a period
        //todo: to fix: there are no new words when adding a new widget
        //todo: to fix. Widget updates when one more widget is added
        //todo: to fix: Widget updates when phone is rebooted
        //todo: to fix: Separate settings from updating
        //todo: to fix: time of update is not midnight
        //todo: to fix: once configure activity was not called when the second widget was added and deleted then

        scheduler.scheduleNextUpdate(context, preferences);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            preferences.deleteWordsColorFromPref(appWidgetId);
        }
        //Todo: to stop updating  deleted widgets, to delete all preferences and to kill the schedule

    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "The first widget is created");
    }

    @Override
    public void onDisabled(Context context) {
        if (preferences != null) {
            preferences.savePeriodToPref(0);
            preferences.saveWordsCountToPref(0);
        }
        isFirstUpdate = true;
        //  scheduler.cancelSchedule();
        Log.d(TAG, "The last widget is disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            Log.d(TAG, "The time to update has come");
            isScheduledUpdate = true;
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
            //todo: why to call onUpdate if we have scheduled update? To remove code from upd
            onUpdate(context, manager, ids);
        }

    }

}