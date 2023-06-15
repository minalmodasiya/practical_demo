package com.example.minalpracticalcamera;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_UPDATE_SWITCH = "MyAppWidgetProvider.UPDATE_SWITCH";
    public static final String EXTRA_SWITCH_ON = "MyAppWidgetProvider.EXTRA_SWITCH_ON";
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (ACTION_UPDATE_SWITCH.equals(action)) {
            int id = intent.getIntExtra("appWidgetId", 0);
            if (id != 0) {
                updateAppWidgetSwitch(context, intent, id);
            }
        }
        super.onReceive(context, intent);
    }

    private void updateAppWidgetSwitch(Context context, Intent intent, int appWidgetId) {
        boolean switchOn = intent.getBooleanExtra(EXTRA_SWITCH_ON, false);
        // take some action based on the switch being clicked

        RemoteViews views = new RemoteViews (context.getPackageName(), R.layout.app_widget_layout_switch);
        // normal RemoteViews stuff
        // use switchOn var to set your switch state

        // make new on click pending intent
        Intent intentSwitch = new Intent(ACTION_UPDATE_SWITCH);
        intentSwitch.putExtra("appWidgetId", R.id.appwidget_switch);
        intentSwitch.putExtra(EXTRA_SWITCH_ON, !switchOn); // new state
        intentSwitch.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_switch, pendingIntent);

        // update widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}

