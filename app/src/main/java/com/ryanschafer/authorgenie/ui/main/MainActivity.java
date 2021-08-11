package com.ryanschafer.authorgenie.ui.main;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.ryanschafer.authorgenie.background.AlarmReceiver;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.MainActivityBinding;
import com.ryanschafer.authorgenie.ui.about.About;
import com.ryanschafer.authorgenie.ui.addgoal.AddGoalFragment;
import com.ryanschafer.authorgenie.background.GoalStatusHandlerThread;
import com.ryanschafer.authorgenie.ui.wordprocessor.ScrollingEditTextActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String ADD_GOAL_FRAGMENT_KEY = "AddGoalFragment";
    private static final String LAST_USED_KEY = "Time last used in millis";
    private static final String GRAPH_FRAGMENT_KEY = "GraphFragment";
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String FIRST_TIME_KEY = "first time use";
    private static final String WORDS_COUNTED_NAME = "words_counted_in_word_counter";
    private static final String WORDS_COUNTED_KEY = WORDS_COUNTED_NAME;
    private static final String SETTINGS_FRAGMENT_KEY = "SettingsFragment";

    private MainActivityBinding binding;
    private MainViewModel mViewModel;
    private GoalStatusHandlerThread mHandlerThread;
    private NotificationManager mNotificationManager;

    private SharedPreferences mPreferences;
    public static final String prefFileName = "com.ryanschafer.authorgenie4";
    boolean mNotifyInit;
    private AddGoalFragment mAddGoalFragment;
    private GraphFragment mGraphFragment;
    private SettingsFragment mSettingsFragment;

    boolean mFirstTime;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){

                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        if (intent != null) {
                            int words = intent.getIntExtra(WORDS_COUNTED_NAME, 0);
                            mPreferences.edit().putInt(WORDS_COUNTED_KEY, words).apply();
                        }

                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

            ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//                mlp.leftMargin = insets.left;
//                mlp.bottomMargin = insets.bottom;
//                mlp.rightMargin = insets.right;
                mlp.topMargin = insets.top;
                v.setLayoutParams(mlp);

                return windowInsets;
            });


        mPreferences = getSharedPreferences(prefFileName, MODE_PRIVATE);
        mFirstTime = mPreferences.getBoolean(FIRST_TIME_KEY, true);

        androidx.appcompat.widget.Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MainViewModel.class);




        if (savedInstanceState != null) {
            //Restore the fragment's instance
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            mAddGoalFragment = (AddGoalFragment) supportFragmentManager
                    .getFragment(savedInstanceState, ADD_GOAL_FRAGMENT_KEY);
            mGraphFragment = (GraphFragment) supportFragmentManager
                    .getFragment(savedInstanceState, GRAPH_FRAGMENT_KEY);
            mSettingsFragment = (SettingsFragment) supportFragmentManager
                    .getFragment(savedInstanceState, SETTINGS_FRAGMENT_KEY);

        }


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        updateLastUsed();
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
                        AlarmManager.INTERVAL_HALF_DAY/2, notifyPendingIntent);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showDashboardFragment();
        if(mAddGoalFragment!=null){
            showAddGoalFragment();
        }
        else if(mGraphFragment != null){
            showProgress();
        }
        else if(mSettingsFragment != null){
            showSettings();
        }
        if(mFirstTime){
            mPreferences.edit().putBoolean(FIRST_TIME_KEY, false).apply();
            showAddGoalFragment();
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
        if(item.getItemId() == R.id.action_write){
            launchWriter();
        }
        if(item.getItemId() == R.id.action_progress){
            showProgress();
        }
        if (item.getItemId() == R.id.action_settings){
            showSettings();
        }

        return super.onOptionsItemSelected(item);
    }




    public void showAbout() {
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
    private void showProgress() {
        if(mGraphFragment == null){
            mGraphFragment = GraphFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container,
                mGraphFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }
    private void showSettings() {
        if(mSettingsFragment == null){
            mSettingsFragment = SettingsFragment.newInstance();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container,
                mSettingsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
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
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if(mAddGoalFragment != null && mAddGoalFragment.isAdded()) {
            try {
                supportFragmentManager.putFragment(outState, ADD_GOAL_FRAGMENT_KEY, mAddGoalFragment);
            } catch (IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }else if(mGraphFragment != null && mGraphFragment.isAdded()){
            try {
                supportFragmentManager.putFragment(outState, GRAPH_FRAGMENT_KEY, mGraphFragment);
            } catch (IllegalStateException | NullPointerException e) {
                e.printStackTrace();
            }
        }else if(mSettingsFragment != null && mSettingsFragment.isAdded()){
            try {
                supportFragmentManager.putFragment(outState, SETTINGS_FRAGMENT_KEY, mSettingsFragment);
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
        }
    }

    private void launchWriter() {
        Intent intent = new Intent(MainActivity.this, ScrollingEditTextActivity.class);
        launcher.launch(intent);
    }

}