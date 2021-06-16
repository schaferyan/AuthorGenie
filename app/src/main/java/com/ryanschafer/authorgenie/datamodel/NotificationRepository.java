package com.ryanschafer.authorgenie.datamodel;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.ryanschafer.authorgenie.background.GoalNotification;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NotificationRepository {
    GoalDao goalDao;
    NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 0;
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final String NOTIFICATION_GROUP_ID_GOAL_REMINDERS = "goal reminders";
    SharedPreferences mPreferences;

    public NotificationRepository(Application application){
        GoalDatabase db = GoalDatabase.getInstance(application);
        goalDao = db.goalDao();
        mPreferences = application.getSharedPreferences(MainActivity.prefFileName, Context.MODE_PRIVATE);
    }


    public void sendGoalNotifications(Context context){
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        long lastUsed = mPreferences.getLong(MainActivity.LAST_USED_KEY, currentTime);
        final List<Goal> currentGoals = new ArrayList<>();
        GoalNotification[] notifications = new GoalNotification[7];
        GoalDatabase.dbWriteExecutor.execute(() -> {
            currentGoals.addAll(goalDao.getCurrentGoalsAsList());
            Calendar calendar1 = Calendar.getInstance();
            long currentTime1 = calendar1.getTimeInMillis();

            String title;
            String message;
            for(Goal goal : currentGoals){
                Log.d("LOOPING","looping through goals");
                int typeId = goal.getGoalTypeId();
                switch (typeId){
                    case Goal.DAY_WORD:
                    case Goal.DAY_TIME:
                        if(currentTime1 - lastUsed > TimeUnit.DAYS.toMillis(1)/2) {
                            title = "Have you written today?";
                            message = "Don't forget to enter your progress!";
                            notifications[0] = new GoalNotification(title, message);
                        }
                        break;
                    case Goal.WEEK_WORD:
                    case Goal.WEEK_TIME:
                        if(goal.getDeadline() - currentTime1 < TimeUnit.DAYS.toMillis(1)
                                && !goal.isMet()){
                            title = "Time is running out to meet your weekly" + goal.getDuration().toString() + "goal...";
                            message = "But you can do it! We believe in you!";
                            notifications[goal.getGoalTypeId()] = new GoalNotification(title, message);
                        }
                        break;
                    case Goal.MONTH_TIME:
                    case Goal.MONTH_WORD:
                        if(goal.getDeadline() - currentTime1 < TimeUnit.DAYS.toMillis(1)
                                && !goal.isMet()){
                            title = "Time is running out to meet your " + goal.getDuration().toString() + "goal...";
                            message = "Show that typewriter what you're made of!";
                            notifications[goal.getGoalTypeId()] = new GoalNotification(title, message);
                        }
                        break;
                    default:
                        break;

                }

            }

            mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            for(GoalNotification notification : notifications) {
                if(notification!=null) {
                    Intent contentIntent = new Intent(context, MainActivity.class);
                    PendingIntent pendingContentIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notify_deadline)
                            .setContentTitle(notification.getTitle())
                            .setContentText(notification.getMessage())
                            .setContentIntent(pendingContentIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setGroup(NOTIFICATION_GROUP_ID_GOAL_REMINDERS)
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        });
    }

}
