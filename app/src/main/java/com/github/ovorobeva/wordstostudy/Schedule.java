package com.github.ovorobeva.wordstostudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class Schedule {
    private static final String ACTION_SCHEDULED_UPDATE = "android.appwidget.action.ACTION_SCHEDULED_UPDATE";

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public void scheduleNextUpdate(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Substitute AppWidget for whatever you named your AppWidgetProvider subclass
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(ACTION_SCHEDULED_UPDATE);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar schedule = Calendar.getInstance();
        int period = ConfigureActivity.loadPeriodFromPref(context);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, schedule.getTimeInMillis() + period, pendingIntent);
        Log.d(AppWidget.class.getCanonicalName() + ".scheduleNextUpdate", "Schedule for updating is set. The next text update will be done in " + period + "ms");
    }
    public void cancelSchedule(){
        alarmManager.cancel(pendingIntent);
    }
}
