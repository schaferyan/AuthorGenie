package com.ryanschafer.authorgenie.background;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.ryanschafer.authorgenie.datamodel.Goal;
import com.ryanschafer.authorgenie.ui.main.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GoalStatusHandlerThread extends HandlerThread {

    Handler handler;
    final MainViewModel mViewModel;
    final Context context;

    public GoalStatusHandlerThread(String name, MainViewModel viewModel, Context context) {
        super(name);
        mViewModel = viewModel;
        this.context = context;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long midnight = calendar.getTimeInMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = null;
                    List<Goal> goals = mViewModel.getGoalsAsList();
                    for (Goal goal : goals) {
                        if (goal.getDeadline() <= Calendar.getInstance().getTimeInMillis()) {

                            if (goal.getProgress() > goal.getObjective()) {
                                message = "You exceeded your goal by " + (goal.getProgress() -
                                        goal.getObjective()) + "!";
                            } else if (goal.isMet()) {
                                message = goal.getName() + " completed, congratulations!";
                            }
                            if (message != null) {
                                showToast(message);
                            }
                            if (goal.isRecurring()) {
                                mViewModel.addGoal(new Goal(goal.getObjective(), goal.getType(), goal.getDuration(), true));
                            }
                        }
                    }

                }
            }
        };
// Schedule the task for midnight
        handler.postAtTime(runnable, midnight);
    }
    private void showToast(String message){
            Handler mainHandler = new Handler(context.getMainLooper());

        // This is your code
        Runnable myRunnable = () -> Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            mainHandler.post(myRunnable);
    }
}
