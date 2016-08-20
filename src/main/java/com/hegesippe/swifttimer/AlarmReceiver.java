package com.hegesippe.swifttimer;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Администратор on 05.09.2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean("notif", false))
            fireNotification(context);
        if (sp.getBoolean("vibration", false))
            fireVibration(context);
        if (sp.getBoolean("sound", false))
            fireSound(context);
        int appWidgetId = intent.getIntExtra(STWidgetProvider.APP_WIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d("go", "Tadam!");
        if(!(sp.getBoolean("pomodoro", false) && intent.getBooleanExtra("end_of_work_timer", false))) {
                context.sendBroadcast(new Intent(context, STWidgetProvider.class)
                                .setAction(STWidgetProvider.STOP)
                                .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId)
                );
                Log.d("L", "Broadcast sent " + appWidgetId + ", Pomodoro mode is " + sp.getBoolean("pomodoro", false));
                Log.d("L", "end_of_work_timer is  " + intent.getBooleanExtra("end_of_work_timer", false));
            }
    }

    protected void fireVibration(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000, 1000};
        v.vibrate(pattern, -1);
    }

    protected void fireNotification(Context context) {
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_timer_white_48dp)
                        .setContentTitle("Time is up!")
                        .setContentText("SwiftTimer notification");
    mNotifyMgr.notify(1, mBuilder.build());
    }
    protected void fireSound(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(context, notification);
        mp.start();
    }
}
