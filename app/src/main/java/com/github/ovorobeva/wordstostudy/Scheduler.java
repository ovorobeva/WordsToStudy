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

    public void scheduleNextUpdate(Context context, Calendar schedule) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);

        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC, schedule.getTimeInMillis(), pendingIntent);

        Log.d(TAG, "scheduleNextUpdate: Next update will be done on " + schedule.getTime());
    }

    public void cancelSchedule() {
        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);
        Log.d(TAG, "cancelSchedule: Schedule is cancelled");
    }

}
