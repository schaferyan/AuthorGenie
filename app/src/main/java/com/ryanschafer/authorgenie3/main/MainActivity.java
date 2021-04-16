package com.ryanschafer.authorgenie3.main;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.ryanschafer.authorgenie3.NewGoalActivity;
import com.ryanschafer.authorgenie3.R;
import com.ryanschafer.authorgenie3.RecyclerviewFragment;
import com.ryanschafer.authorgenie3.databinding.MainActivityBinding;
import com.ryanschafer.authorgenie3.Model.Goal;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    MainActivityBinding binding;
    MainViewModel mViewModel;
    private static final int NEW_GOAL_ACTIVITY_REQUEST_CODE = 1;

    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 0;
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    public static final String EXTRA_GOAL_DEADLINE = "Goal deadline extra";
    public static final String EXTRA_GOAL_NAME = "Goal name extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);
//        toolbar = binding.toolbar;
//        setSupportActionBar(toolbar);
        mViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MainViewModel.class);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, ProgressInputFragment.newInstance())
                    .commitNow();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recyclerview_fragment_container, RecyclerviewFragment.newInstance())
                    .commitNow();
        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("RESUME", "onResume called in activity, now executing code");
        List<Goal> goalsEnded = mViewModel.getFinishedGoals();
        for (Goal goal : goalsEnded) {
            if (goal.isMet()) {
                Toast.makeText(this, goal.getName() + " completed, congratulations!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "you didn't meet your " + goal.getName(), Toast.LENGTH_LONG).show();
            }
            mViewModel.addGoal(new Goal(goal.getObjective(), goal.getGoalTypeId()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionbar_add) {
            addGoal();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_GOAL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            int amount = data.getIntExtra(NewGoalActivity.EXTRA_AMOUNT, 0);
            int duration = data.getIntExtra(NewGoalActivity.EXTRA_DURATION, 0);
            int goaltype = data.getIntExtra(NewGoalActivity.EXTRA_GOALTYPE, 0);
            Goal goal = new Goal(amount,
                    Goal.TYPE.values()[goaltype],
                    Goal.DURATION.values()[duration]);
            if (mViewModel.addGoal(goal)) {

                Intent notifyIntent = new Intent(this, AlarmReceiver.class);
                notifyIntent.putExtra(EXTRA_GOAL_NAME, goal.getName());
                notifyIntent.putExtra(EXTRA_GOAL_DEADLINE, goal.getDeadline());
                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this,
                        NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.setWindow(AlarmManager.RTC,
                            goal.getDeadline() - AlarmManager.INTERVAL_DAY,
                            AlarmManager.INTERVAL_HALF_DAY,
                            notifyPendingIntent);
                }

            }


        } else {

        }

    }

    private void openSettings() {
    }

    private void viewGoals() {
    }

    public void addGoal() {
        Intent intent = new Intent(this, NewGoalActivity.class);
        startActivityForResult(intent, NEW_GOAL_ACTIVITY_REQUEST_CODE);
    }

    private void createNotificationChannel() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Goal deadline reminder", NotificationManager.IMPORTANCE_HIGH);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setDescription("Reminder to complete goal before timeline ends");
            mNotificationManager.createNotificationChannel(channel);
        }
    }
}