package com.hegesippe.swifttimer;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Администратор on 24.08.2015.
 */

//TODO: add feedback to clicks in layout xml

public class ScheduleActivity extends Activity implements View.OnClickListener {

    private String timeString;
    private TextView mTimeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        final int appWidgetId = getIntent().getIntExtra(STWidgetProvider.APP_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mTimeView = (TextView) findViewById(R.id.timeTextView);
        findViewById(R.id.numpad0).setOnClickListener( this );
        findViewById(R.id.numpad1).setOnClickListener( this );
        findViewById(R.id.numpad2).setOnClickListener( this );
        findViewById(R.id.numpad3).setOnClickListener( this );
        findViewById(R.id.numpad4).setOnClickListener( this );
        findViewById(R.id.numpad5).setOnClickListener( this );
        findViewById(R.id.numpad6).setOnClickListener( this );
        findViewById(R.id.numpad7).setOnClickListener( this );
        findViewById(R.id.numpad8).setOnClickListener( this );
        findViewById(R.id.numpad9).setOnClickListener( this );
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(getApplicationContext(), STWidgetProvider.class)
                        .setAction(STWidgetProvider.START)
                        .putExtra(STWidgetProvider.APP_WIDGET_ID, appWidgetId)
                        .putExtra(STWidgetProvider.TIME_MILLIS, TimeConversionUtil
                                .convertStringToMilliseconds(timeString)));
                finish();
            }
        });
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        timeString = "000000";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        timeString += (String) ((Button) v).getText();
        timeString = timeString.substring(1);
        mTimeView.setText(String.format("%02d", Integer.parseInt(timeString.substring(0, 2))) + ":"
                + String.format("%02d", Integer.parseInt(timeString.substring(2, 4))) + ":"
                + String.format("%02d", Integer.parseInt(timeString.substring(4, 6))));
    }
}
