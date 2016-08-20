package com.hegesippe.swifttimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Администратор on 08.09.2015.
 */
public class AlarmFactory {

    private static HashMap<Integer, Alarm> map = new HashMap<>();
    private static AlarmManager am;

    public static void commonSchedule(Context context, int appWidgetId, long time, long frequency) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        map.put(appWidgetId, new Alarm(time, System.currentTimeMillis()));
        Log.d("L", "scheduling... " + appWidgetId);
        //main alarm
        am.setExact(AlarmManager.RTC_WAKEUP, time + System.currentTimeMillis(),
                PendingIntent.getBroadcast(context, appWidgetId, new Intent(context, AlarmReceiver.class)
                        .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId), 0));
        //refreshing alarm
        am.setRepeating(
                AlarmManager.RTC, System.currentTimeMillis(), frequency,
                PendingIntent.getBroadcast(
                        context, appWidgetId,
                        new Intent(context, STWidgetProvider.class)
                                .setAction(STWidgetProvider.REFRESH)
                                .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId)
                        , 0
                )
        );

    }

    public static void pomodoroSchedule(Context context, int appWidgetId, long work_time, long break_time, long frequency) {
        //main pomodoro alarm (30min by default)
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        map.put(appWidgetId, new Alarm(work_time + break_time, System.currentTimeMillis()));
        Log.d("L", "scheduling... " + appWidgetId);
        am.setExact(AlarmManager.RTC_WAKEUP, work_time + break_time + System.currentTimeMillis(),
                PendingIntent.getBroadcast(context, appWidgetId, new Intent(context, AlarmReceiver.class)
                        .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId), 0));
        am.setRepeating(
                AlarmManager.RTC, System.currentTimeMillis(), frequency,
                PendingIntent.getBroadcast(
                        context, appWidgetId,
                        new Intent(context, STWidgetProvider.class)
                                .setAction(STWidgetProvider.REFRESH)
                                .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId)
                        , 0
                )
        );
        //"end of work" pomodoro alarm (25min by default)
        am.setExact(AlarmManager.RTC_WAKEUP, work_time + System.currentTimeMillis(),
                PendingIntent.getBroadcast(context, -appWidgetId, new Intent(context, AlarmReceiver.class)
                .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId)
                .putExtra("end_of_work_timer", true)
                        , 0));
    }

    public static long getAlarmStartTime(int appWidgetId) {
        return map.get(appWidgetId).getStartTime();
    }

    public static long getAlarmTime(int appWidgetId) {
        return map.get(appWidgetId).getTime();
    }

    public static boolean isTicking(int appWidgetId) {
        return map.containsKey(appWidgetId);
    }

    public static void cancel(Context context, int appWidgetId) {
        am.cancel(PendingIntent.getBroadcast(context, appWidgetId,
                new Intent(context, AlarmReceiver.class), 0));
        am.cancel(PendingIntent.getBroadcast(context, -appWidgetId,
                new Intent(context, AlarmReceiver.class), 0));
        am.cancel(PendingIntent.getBroadcast(context, appWidgetId,
                new Intent(context, STWidgetProvider.class)
                        .setAction(STWidgetProvider.REFRESH), 0));
        map.remove(appWidgetId);
    }

}
