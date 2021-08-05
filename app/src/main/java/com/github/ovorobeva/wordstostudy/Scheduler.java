package com.github.ovorobeva.wordstostudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class Scheduler {
    static final String ACTION_SCHEDULED_UPDATE = "android.appwidget.action.ACTION_SCHEDULED_UPDATE";
    private static final String TAG = "Custom logs";
    private static final Object OBJECT = new Object();
    private static Scheduler scheduler;
    private static Boolean isCancelled = false;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    private Scheduler() {
    }

    ;

    public static Scheduler getScheduler() {
        if (scheduler != null)
            return scheduler;

        synchronized (OBJECT) {
            if (scheduler == null)
                scheduler = new Scheduler();
            return scheduler;
        }
    }

    public void scheduleNextUpdate(Context context, Preferences preferences) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);

        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        int period = preferences.loadSettingFromPref(Preferences.PERIOD);


        Calendar schedule = Calendar.getInstance();

        preferences.saveUpdateTimeToPref(schedule, Preferences.LAST);

        if (period == ConfigureActivity.EVERY_MONDAY)
            schedule.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        schedule.set(Calendar.MILLISECOND, 0);
        schedule.set(Calendar.SECOND, 0);
        schedule.set(Calendar.MINUTE, 0);
        schedule.set(Calendar.HOUR_OF_DAY, 0);


//todo: to replace the second parameter to the current midnight
        schedule.add(Calendar.DATE, period + 1);
        alarmManager.set(AlarmManager.RTC, schedule.getTimeInMillis(), pendingIntent);

        Log.d(TAG, "scheduleNextUpdate: Next update will be done on " + schedule.getTime());
    }

    public void cancelSchedule() {
        alarmManager.cancel(pendingIntent);
        isCancelled = true;
        Log.d(TAG, "cancelSchedule: Schedule is cancelled");
    }

    public boolean isCancelled() {
        return isCancelled;
    }

}
