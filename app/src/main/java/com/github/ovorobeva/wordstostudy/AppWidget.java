package com.github.ovorobeva.wordstostudy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

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
import static com.github.ovorobeva.wordstostudy.Preferences.saveUpdateTimeToPref;
import static com.github.ovorobeva.wordstostudy.Preferences.saveWordsToPref;
import static com.github.ovorobeva.wordstostudy.Scheduler.ACTION_SCHEDULED_UPDATE;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ConfigureActivity ConfigureActivity}
 */
public class AppWidget extends AppWidgetProvider {

    static final String TAG = "Custom logs";
    private static final Scheduler scheduler = Scheduler.getScheduler();
    public static boolean isTextUpdate;
    //todo: to fix colors when first changed
    //todo: to fix first words update
    //todo: to fix some words like que by its frequency in the service

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent configIntent = new Intent(context, ConfigureActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.main_layout, pIntent);

        if (!isTextUpdate) {
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

            if (isWordCountChanged == 1 && isPeriodChanged == 0) {
                updateTextAppWidget(context, appWidgetManager, views, true);
                //       Toast.makeText(context, R.string.wordsCountChangedMsg, Toast.LENGTH_SHORT).show();

            }

            if (isPeriodChanged == 1)
                updateTextAppWidget(context, appWidgetManager, views, false);
        } else {
            isTextUpdate = false;
            updateTextAppWidget(context, appWidgetManager, views, false);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    static void updateTextAppWidget(Context context, AppWidgetManager appWidgetManager, RemoteViews views, boolean isAdditional) {

        WordsClient wordsClient = WordsClient.getWordsClient();
        if (isAdditional) {
            int savedWordsCount = (loadWordsFromPref(context).length() - loadWordsFromPref(context).replace(" - ", "").length()) / 3;
            if (savedWordsCount < loadSettingFromPref(WORDS_COUNT, context)) {
                int delta = loadSettingFromPref(WORDS_COUNT, context) - savedWordsCount;
                wordsClient.getWords(delta, context, appWidgetManager, views, isAdditional);
            } else {

                StringBuilder currentWords = new StringBuilder();
                currentWords.append(loadWordsFromPref(context));

                StringBuilder words = new StringBuilder();
                int count = 0;
                for (int i = 0; count < loadSettingFromPref(WORDS_COUNT, context); i++){
                    if (currentWords.charAt(i) == '\n') count++;
                    words.append(currentWords.charAt(i));
                }

                Log.d(TAG, "onResponse: words are: " + words);

                views.setTextViewText(R.id.words_edit_text, words);
            }
        } else {

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

            int wordsCount = loadSettingFromPref(WORDS_COUNT, context); //3

            views.setTextViewText(R.id.words_edit_text, "words");
            wordsClient.getWords(wordsCount, context, appWidgetManager, views, false); //3 new words, update is complete
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
    }}

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        //todo: to fix: Widget doesn't update when phone is rebooted

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
            isTextUpdate = true;
            onUpdate(context, manager, ids);
        }

    }

}