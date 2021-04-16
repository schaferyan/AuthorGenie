package com.ryanschafer.authorgenie3.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.ryanschafer.authorgenie3.R;

import java.text.DateFormat;

public class AlarmReceiver extends BroadcastReceiver {


    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 0;
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final String EXTRA_GOAL_DEADLINE = "Goal deadline extra";
    public static final String EXTRA_GOAL_NAME = "Goal name extra";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        String goalName = intent.getStringExtra(EXTRA_GOAL_NAME);
        long goalDeadline = intent.getLongExtra(EXTRA_GOAL_DEADLINE, 0);
        deliverNotification(context, goalName, goalDeadline);
    }

    private void deliverNotification(Context context, String name, long deadline) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notify_deadline)
                .setContentTitle("Time is running out!")
                .setContentText("You have until " + DateFormat.getInstance().format(deadline) +
                        " to finish your " + name)
                .setContentIntent(pendingContentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}