package com.ryanschafer.authorgenie3.background;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ryanschafer.authorgenie3.datamodel.NotificationRepository;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NOTIFICATIONS","broadcast received");
        NotificationRepository repository =
                new NotificationRepository((Application) context.getApplicationContext());
        repository.sendGoalNotifications(context);
    }
}