package com.github.ovorobeva.wordstostudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class Scheduler {
    private static final String ACTION_SCHEDULED_UPDATE = "android.appwidget.action.ACTION_SCHEDULED_UPDATE";
    private static final String TAG = "Custom logs";
    private static Scheduler scheduler;
    private static final Object OBJECT = new Object();


    private Scheduler(){};

    public static Scheduler getScheduler() {
        if (scheduler != null)
            return scheduler;

        synchronized (OBJECT) {
            if (scheduler == null)
                scheduler = new Scheduler();
            return scheduler;
        }
    }
    public void scheduleNextUpdate(Context context) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar schedule = Calendar.getInstance();
        int period = ConfigureActivity.loadPeriodFromPref(context);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, schedule.getTimeInMillis() + period, pendingIntent);
        Log.d(TAG, "Schedule for updating is set. The next update will be done in " + period + "ms");
    }
}
