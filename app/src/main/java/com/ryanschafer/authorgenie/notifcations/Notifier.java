package com.ryanschafer.authorgenie.notifcations;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.goals.Goal;
import com.ryanschafer.authorgenie.goals.GoalDao;
import com.ryanschafer.authorgenie.goals.GoalDatabase;
import com.ryanschafer.authorgenie.goals.GoalNotification;
import com.ryanschafer.authorgenie.goals.GoalRepository;
import com.ryanschafer.authorgenie.goals.NotificationRepository;
import com.ryanschafer.authorgenie.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Notifier {
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String NOTIFICATION_GROUP_ID_GOAL_REMINDERS = "goal reminders";
    private static final String LAST_USED_KEY = "Time last used in millis";


    public static void sendReminders(Context context){

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        GoalNotification[] notifications = buildReminders(context);

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
                            .setTimeoutAfter(TimeUnit.DAYS.toMillis(1/2))
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }
        }
        private static GoalNotification[] buildReminders(Context context){
            GoalRepository repository = new GoalRepository((Application) context.getApplicationContext());
            SharedPreferences mPreferences = context.getSharedPreferences(MainActivity.prefFileName, Context.MODE_PRIVATE);
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            long lastUsed = mPreferences.getLong(LAST_USED_KEY, currentTime);
            GoalNotification[] notifications = new GoalNotification[7];
            final List<Goal> currentGoals = repository.getGoalsAsList();
            String title;
            String message;
            for(Goal goal : currentGoals){
                int typeId = goal.getGoalTypeId();
                switch (typeId){
                    case Goal.DAY_WORD:
                    case Goal.DAY_TIME:
                        if(currentTime - lastUsed > TimeUnit.HOURS.toMillis(2)) {
                            title = "Have you written today?";
                            message = "Don't forget to enter your progress!";
                            notifications[0] = new GoalNotification(title, message);
                        }
                        break;
                    case Goal.WEEK_WORD:
                    case Goal.WEEK_TIME:
                        if(goal.getDeadline() - currentTime < TimeUnit.DAYS.toMillis(1)
                                && !goal.isMet()){
                            title = "Time is running out to meet your weekly" + goal.getDuration().toString() + "goal...";
                            message = "But you can do it! We believe in you!";
                            notifications[goal.getGoalTypeId()] = new GoalNotification(title, message);
                        }
                        break;
                    case Goal.MONTH_TIME:
                    case Goal.MONTH_WORD:
                        if(goal.getDeadline() - currentTime < TimeUnit.DAYS.toMillis(1)
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
            return notifications;
        }
}
