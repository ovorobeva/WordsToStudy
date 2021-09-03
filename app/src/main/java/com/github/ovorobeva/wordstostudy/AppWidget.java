package com.github.ovorobeva.wordstostudy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.util.Calendar;

import static com.github.ovorobeva.wordstostudy.ConfigureActivity.EVERY_MONDAY;
import static com.github.ovorobeva.wordstostudy.Preferences.IS_COLOR_CHANGED;
import static com.github.ovorobeva.wordstostudy.Preferences.IS_PERIOD_CHANGED;
import static com.github.ovorobeva.wordstostudy.Preferences.IS_WORD_COUNT_CHANGED;
import static com.github.ovorobeva.wordstostudy.Preferences.LAST;
import static com.github.ovorobeva.wordstostudy.Preferences.NEXT;
import static com.github.ovorobeva.wordstostudy.Preferences.PERIOD;
import static com.github.ovorobeva.wordstostudy.Preferences.WORDS_COUNT;
import static com.github.ovorobeva.wordstostudy.Preferences.clearPrefs;
import static com.github.ovorobeva.wordstostudy.Preferences.deleteWordsColorFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadColorFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadSettingFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadUpdateTimeFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.loadWordsFromPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveSettingToPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveUpdateTimeToPref;
import static com.github.ovorobeva.wordstostudy.Scheduler.ACTION_SCHEDULED_UPDATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    static final String TAG = "Custom logs";
    private static final Scheduler scheduler = Scheduler.getScheduler();
    //todo: to save words into preferences and compare if words exist, load them, otherwise get new

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent configIntent = new Intent(context, ConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.main_layout, pIntent);

        String words = loadWordsFromPref(context);
        if (words == null)
            words = context.getResources().getString(R.string.appwidget_text);

        views.setTextViewText(R.id.words_edit_text, words);

            int isColorChanged = loadSettingFromPref(IS_COLOR_CHANGED, context);
            int isWordCountChanged = loadSettingFromPref(IS_WORD_COUNT_CHANGED, context);
            int isPeriodChanged = loadSettingFromPref(IS_PERIOD_CHANGED, context);
            Log.d(TAG, "updateAppWidget: isColorChanged = " + isColorChanged);
            Log.d(TAG, "updateAppWidget: isWordCountChanged = " + isWordCountChanged);
            Log.d(TAG, "updateAppWidget: isPeriodChanged = " + isPeriodChanged);

            if (isColorChanged == 1) {
                int color = loadColorFromPref(appWidgetId, context);
                views.setTextColor(R.id.words_edit_text, color);
            }

            if (isWordCountChanged == 1 && isPeriodChanged == 0)
                Toast.makeText(context, R.string.wordsCountChangedMsg, Toast.LENGTH_SHORT).show();

            if (isPeriodChanged == 1)
                AppWidget.updateTextAppWidget(context, appWidgetManager); //todo: doesn't work because of this line

        //todo: to fix back button
        //todo: to fix first update before click
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    static void updateTextAppWidget(Context context, AppWidgetManager appWidgetManager) {
        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTimeInMillis(loadUpdateTimeFromPref(LAST, context));
        Log.d(TAG, "updateTextAppWidget: loaded last update is: " + lastUpdate.getTime());//now

        Calendar nextUpdate = Calendar.getInstance();
        nextUpdate.setTimeInMillis(loadUpdateTimeFromPref(NEXT, context));
        Log.d(TAG, "updateTextAppWidget: loaded next update is: " + nextUpdate.getTime());//now

        int period = loadSettingFromPref(PERIOD, context);
        Log.d(TAG, "updateTextAppWidget: period is: " + period + " days");//1

        if (nextUpdate.before(Calendar.getInstance()) || nextUpdate.equals(Calendar.getInstance())) { //equals = true
            Log.d(TAG, "updateTextAppWidget: do update");
            scheduler.cancelSchedule();//cancelled
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            int wordsCount = loadSettingFromPref(WORDS_COUNT, context); //3

            WordsClient wordsClient = WordsClient.getWordsClient();

            views.setTextViewText(R.id.words_edit_text, "words");
            wordsClient.getWords(wordsCount, context, appWidgetManager, views); //3 new words, update is complete
            lastUpdate = Calendar.getInstance(); //lastUpdate = now
            saveUpdateTimeToPref(lastUpdate, LAST, context); //lu = now saved
        }
        Log.d(TAG, "updateTextAppWidget: last update was on: " + lastUpdate.getTime());

        nextUpdate = lastUpdate;

        if (period == EVERY_MONDAY) {
            nextUpdate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        nextUpdate.set(Calendar.MINUTE, 0);
        nextUpdate.set(Calendar.HOUR_OF_DAY, 0);
        nextUpdate.set(Calendar.MILLISECOND, 0);
        nextUpdate.set(Calendar.SECOND, 0);

        nextUpdate.add(Calendar.DAY_OF_MONTH, period);

        saveUpdateTimeToPref(nextUpdate, NEXT, context);

        Log.d(TAG, "updateTextAppWidget: next update will be on: " + nextUpdate.getTime());
        scheduler.scheduleNextUpdate(context, nextUpdate);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        //todo: to fix: Widget updates when phone is rebooted
        //todo: to fix: time of update is not midnight
        //todo: to fix: once configure activity was not called when the second widget was added and deleted then

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
            for (int appWidgetId : appWidgetIds) {
                deleteWordsColorFromPref(appWidgetId, context);
                Log.d(TAG, "onDeleted: widget " + appWidgetId + " is deleted");
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
          //  int[] ids = manager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
            //todo: why to call onUpdate if we have scheduled update? To remove code from upd
           // onUpdate(context, manager, ids);
            updateTextAppWidget(context, manager);
        }

    }

}