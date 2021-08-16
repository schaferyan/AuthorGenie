package com.ryanschafer.authorgenie.background;

import android.app.Application;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.WorkerThread;
import androidx.preference.PreferenceManager;

import com.ryanschafer.authorgenie.goals.NotificationRepository;
import com.ryanschafer.authorgenie.notifcations.Notifier;

import java.util.logging.Handler;

public class AlarmReceiver extends BroadcastReceiver {
    SharedPreferences defaultPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(defaultPref.getBoolean("Reminders", true)) {
//            TODO call this method from another thread
            Runnable runnable = () -> Notifier.sendReminders(context);
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}