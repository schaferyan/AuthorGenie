package com.ryanschafer.authorgenie.background;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Task;
import com.ryanschafer.authorgenie.notifcations.Notifier;

public class AlarmReceiver extends BroadcastReceiver {
    SharedPreferences defaultPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(defaultPref.getBoolean("Reminders", true)) {
//            TODO call this method from another thread
            final PendingResult pendingResult = goAsync();
            NotificationsTask asyncTask = new NotificationsTask(pendingResult, context);
            asyncTask.execute();
        }
    }

    private static class NotificationsTask extends AsyncTask<Void, Void, Void>{

        private final PendingResult pendingResult;
        private  final Application application;

        private NotificationsTask(PendingResult pendingResult, Context context) {
            this.pendingResult = pendingResult;
            this.application = (Application) context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Notifier.sendReminders(application);
            return null;
        }


        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            pendingResult.finish();
        }
    }
}