package com.ryanschafer.authorgenie.background;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.ryanschafer.authorgenie.datamodel.NotificationRepository;
import com.ryanschafer.authorgenie.ui.main.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    SharedPreferences defaultPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(defaultPref.getBoolean("Reminders", true)) {
            NotificationRepository repository =
                    new NotificationRepository((Application) context.getApplicationContext());
            repository.sendGoalNotifications(context);
        }
    }
}