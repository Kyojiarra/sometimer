package com.hegesippe.swifttimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.HashMap;

public final class STWidgetProvider extends AppWidgetProvider {

    static final String ZEROSTRING = "00:00:00";
    static final String REFRESH = "com.hegesippe.swifttimer.REFRESH";
    static final String SCHBUTTON_PRESSED = "com.hegesippe.swifttimer.SCHBUTTON_PRESSED";
    static final String STOP = "com.hegesippe.swifttimer.STOP";
    static final String START = "com.hegesippe.swifttimer.START";
    static final String SETTINGS = "com.hegesippe.swifttimer.SETTINGS";
    static final String TIME_MILLIS = "com.hegesippe.swifttimer.TIME_MILLIS";
    static final String APP_WIDGET_ID = "com.hegesippe.swifttimer.APP_WIDGET_ID";
    static final String DEFAULT_COLOR = "#FF9C27B0";

    private static HashMap<Integer, String> states = new HashMap<>();
    private static SharedPreferences sp;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("L", "onUpdate...");
        for (int appWidgetId : appWidgetIds) {

            if (!states.containsKey(appWidgetId)) states.put(appWidgetId, ZEROSTRING);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent scheduleIntent = new Intent(context, STWidgetProvider.class)
                    .setAction(SCHBUTTON_PRESSED)
                    .putExtra(APP_WIDGET_ID, appWidgetId);
            PendingIntent schedulePendingIntent = PendingIntent
                    .getBroadcast(context, appWidgetId, scheduleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.schBtn, schedulePendingIntent);

            Intent refreshIntent = new Intent(context, STWidgetProvider.class)
                    .setAction(REFRESH)
                    .putExtra(APP_WIDGET_ID, appWidgetId);
            PendingIntent refreshPendingIntent = PendingIntent
                    .getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.updBtn, refreshPendingIntent);

            Intent settingsIntent = new Intent(context, STWidgetProvider.class)
                    .setAction(SETTINGS)
                    .putExtra(APP_WIDGET_ID, appWidgetId);
            PendingIntent settingsPendingIntent = PendingIntent
                    .getBroadcast(context, appWidgetId, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.stgBtn, settingsPendingIntent);

            views.setTextViewText(R.id.timeTextView, states.get(appWidgetId));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.timeTextView, states.get(appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, views);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int appWidgetId = intent.getIntExtra(APP_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        switch (intent.getAction()) {
            case REFRESH:
                Log.d("L", "refreshing " + appWidgetId);
                refreshAppWidget(context, appWidgetId);
                break;
            case SCHBUTTON_PRESSED:
                if (AlarmFactory.isTicking(appWidgetId)) {
                    Log.d("L", "stopping " + appWidgetId);
                    AlarmFactory.cancel(context, appWidgetId);
                    refreshAppWidget(context, appWidgetId);
                }
                else if (sp.getBoolean("pomodoro", false)) {
                    AlarmFactory.pomodoroSchedule(context,
                            intent.getIntExtra(STWidgetProvider.APP_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID),
                            60000 * Integer.parseInt(sp.getString("work_time_minutes", "25")),
                            60000 * Integer.parseInt(sp.getString("break_time_minutes", "5")),
                            1000 * Integer.parseInt(sp.getString("freq_secs", "1"))
                    );
                    RemoteViews v1 = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                    v1.setImageViewResource(R.id.schBtn, R.drawable.ic_stop_white_36dp);
                    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, v1);
                    refreshAppWidget(context, appWidgetId);
                } else {
                    Log.d("L", "launching activity " + appWidgetId);
                    context.startActivity(new Intent(context, ScheduleActivity.class)
                            .putExtra(APP_WIDGET_ID, appWidgetId)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } break;
            case START:
                Log.d("L", "starting " + appWidgetId);
                //setting up alarm
                AlarmFactory.commonSchedule(context, appWidgetId, intent.getLongExtra(TIME_MILLIS, 1),
                        1000 * Integer.parseInt(sp.getString("freq_secs", "1")));
                RemoteViews v1 = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                v1.setImageViewResource(R.id.schBtn, R.drawable.ic_stop_white_36dp);
                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, v1);
                refreshAppWidget(context, appWidgetId);
                break;
            case STOP:
                AlarmFactory.cancel(context, appWidgetId);
                Log.d("L", "stopping " + appWidgetId);
                refreshAppWidget(context, appWidgetId);
                break;
            case SETTINGS:
                Log.d("L", "calling settings " + appWidgetId);
                context.startActivity(new Intent(context, SettingsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        super.onReceive(context, intent);
    }

    private static void refreshAppWidget(Context context, int appWidgetId) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTicking = AlarmFactory.isTicking(appWidgetId);
        states.put(appWidgetId, isTicking ? TimeConversionUtil.getTimeStringFromMilliseconds(
                AlarmFactory.getAlarmTime(appWidgetId) - System.currentTimeMillis() +
                        AlarmFactory.getAlarmStartTime(appWidgetId) + 1000) : ZEROSTRING);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if (!isTicking) views.setImageViewResource(R.id.schBtn, R.drawable.ic_play_arrow_white_36dp);
        views.setTextViewText(R.id.timeTextView, states.get(appWidgetId));
        views.setInt(R.id.linLayout, "setBackgroundColor",
                Color.parseColor(sp.getString("background_color", DEFAULT_COLOR)));
        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }

}
