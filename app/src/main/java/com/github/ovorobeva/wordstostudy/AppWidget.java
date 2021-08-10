package com.github.ovorobeva.wordstostudy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import static com.github.ovorobeva.wordstostudy.ConfigureActivity.EVERY_MONDAY;
import static com.github.ovorobeva.wordstostudy.Preferences.LAST;
import static com.github.ovorobeva.wordstostudy.Preferences.NEXT;
import static com.github.ovorobeva.wordstostudy.Preferences.PERIOD;
import static com.github.ovorobeva.wordstostudy.Preferences.WORDS_COUNT;
import static com.github.ovorobeva.wordstostudy.Preferences.clearPrefs;
import static com.github.ovorobeva.wordstostudy.Preferences.deleteWordsColorFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadColorFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadSettingFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadUpdateTimeFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveUpdateTimeToPref;
import static com.github.ovorobeva.wordstostudy.Scheduler.ACTION_SCHEDULED_UPDATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    static final String TAG = "Custom logs";
    private static final Scheduler scheduler = Scheduler.getScheduler();
    //private static Preferences preferences;
    //todo: to save words into preferences and compare if words exist, load them, otherwise get new

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent configIntent = new Intent(context, ConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
   //     AppWidget.preferences = preferences;
        int color = loadColorFromPref(appWidgetId, context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        views.setOnClickPendingIntent(R.id.main_layout, pIntent);

        if (color == Color.BLACK || color == Color.WHITE)
            views.setTextColor(R.id.words_edit_text, color);
        //todo: to fix back button
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static void updateTextAppWidget(Context context, AppWidgetManager appWidgetManager) {
        Calendar last_update = Calendar.getInstance();
        last_update.setTimeInMillis(loadUpdateTimeFromPref(LAST, context));

        Calendar next_update = Calendar.getInstance();
        next_update.setTimeInMillis(loadUpdateTimeFromPref(LAST, context));

        int period = loadSettingFromPref(PERIOD, context);


        if (period == EVERY_MONDAY) {
            next_update.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        }

        next_update.set(Calendar.MINUTE, 0);
        next_update.set(Calendar.HOUR_OF_DAY, 0);

        next_update.add(Calendar.DAY_OF_MONTH, period);

        Log.d(TAG, "updateTextAppWidget: last update was on: " + last_update.getTime());
        Log.d(TAG, "updateTextAppWidget: next update will be on: " + next_update.getTime());

        if (next_update.before(Calendar.getInstance())) {
            scheduler.cancelSchedule();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            int wordsCount = loadSettingFromPref(WORDS_COUNT, context);

            WordsClient wordsClient = WordsClient.getWordsClient();
            wordsClient.getWords(wordsCount, context, appWidgetManager, views);
            next_update.add(Calendar.DAY_OF_MONTH, period);
        }

        next_update.set(Calendar.MILLISECOND, 0);
        next_update.set(Calendar.SECOND, 0);

        saveUpdateTimeToPref(Calendar.getInstance(), LAST, context);
        Log.d(TAG, "updateTextAppWidget: last update is done on: " + loadUpdateTimeFromPref(LAST, context));
        saveUpdateTimeToPref(next_update, NEXT, context);
        Log.d(TAG, "updateTextAppWidget: next update will be done on: " + loadUpdateTimeFromPref(NEXT, context));

        scheduler.scheduleNextUpdate(context, next_update);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

/*        if (preferences == null)
            preferences = Preferences.getPreferences(context);

        Log.d(TAG, "onUpdate: prefs are: " + preferences);
        for (int appWidgetId : appWidgetIds) {
            if (preferences.loadSettingFromPref(PERIOD) == 0 &&
                    preferences.loadSettingFromPref(WORDS_COUNT) == 0) {
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

        scheduler.scheduleNextUpdate(context, preferences);*/
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
            for (int appWidgetId : appWidgetIds) {
                deleteWordsColorFromPref(appWidgetId, context);
            }
        //Todo: to stop updating  deleted widgets, to delete all preferences and to kill the schedule

    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "The first widget is created");
    }

    @Override
    public void onDisabled(Context context) {
            clearPrefs(context);
        if (scheduler != null)
            scheduler.cancelSchedule();
        Log.d(TAG, "The last widget is disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
            Log.d(TAG, "The time to update has come");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
            //todo: why to call onUpdate if we have scheduled update? To remove code from upd
            onUpdate(context, manager, ids);
        }

    }

}