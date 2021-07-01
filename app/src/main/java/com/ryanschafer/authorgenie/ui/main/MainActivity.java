package com.ryanschafer.authorgenie.ui.main;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.ryanschafer.authorgenie.background.AlarmReceiver;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.MainActivityBinding;
import com.ryanschafer.authorgenie.ui.about.About;
import com.ryanschafer.authorgenie.ui.addgoal.AddGoalFragment;
import com.ryanschafer.authorgenie.ui.dialogs.ConfirmationDialogFragment;
import com.ryanschafer.authorgenie.background.GoalStatusHandlerThread;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_GOAL_FRAGMENT_KEY = "AddGoalFragment";
    public static final String LAST_USED_KEY = "Time last used in millis";
    MainActivityBinding binding;
    MainViewModel mViewModel;
    private GoalStatusHandlerThread mHandlerThread;
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 0;
    public static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String FIRST_TIME_KEY = "first time use";
    private SharedPreferences mPreferences;
    public static final String prefFileName = "com.ryanschafer.authorgenie3";
    boolean mNotifyInit;
    private AddGoalFragment mAddGoalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainActivityBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);


        mPreferences = getSharedPreferences(prefFileName, MODE_PRIVATE);
        boolean mFirstTime = mPreferences.getBoolean(FIRST_TIME_KEY, true);

        androidx.appcompat.widget.Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MainViewModel.class);



        if (savedInstanceState != null) {
            //Restore the fragment's instance
            mAddGoalFragment = (AddGoalFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, "AddGoalFragment");
            if(mAddGoalFragment!=null){
              showAddGoalFragment();
            }
            else{
                showDashboardFragment();
            }
        }else{
            showDashboardFragment();
        }

        if(mFirstTime){
            showFirstTimeDialog();
            mPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply();
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        scheduleAlarms();



    }



    @Override
    protected void onPause() {
        super.onPause();
        if(mHandlerThread!= null && mHandlerThread.isAlive()) {
            mHandlerThread.interrupt();
        }
        mHandlerThread = null;
        updateLastUsed();
    }

    private void scheduleAlarms() {
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        mNotifyInit = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent,
                PendingIntent.FLAG_NO_CREATE) != null;

        if(!mNotifyInit) {
            PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this,
                    NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                        AlarmManager.INTERVAL_HALF_DAY / 2, notifyPendingIntent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHandlerThread = new GoalStatusHandlerThread("handler-thread", mViewModel, this);
        mHandlerThread.start();


    }

    private void updateLastUsed() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        mPreferences.edit().putLong(LAST_USED_KEY, currentTime).apply();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_about){
           showAbout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container,
                About.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    public void showDashboardFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container,
                MainFragment.newInstance()).commitNow();
    }

    public void showAddGoalFragment() {
        if(mAddGoalFragment == null){
            mAddGoalFragment = AddGoalFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container,
                mAddGoalFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void showFirstTimeDialog(){
        ConfirmationDialogFragment dialog = ConfirmationDialogFragment.newInstance(
                ConfirmationDialogFragment.FIRST_TIME_USER);
        dialog.show(getSupportFragmentManager(), "set a goal?");
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

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mAddGoalFragment != null && mAddGoalFragment.isAdded()) {
            try {
                getSupportFragmentManager().putFragment(outState, ADD_GOAL_FRAGMENT_KEY, mAddGoalFragment);
            } catch (IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

}