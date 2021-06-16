package com.ryanschafer.authorgenie.background;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.ryanschafer.authorgenie.datamodel.Goal;
import com.ryanschafer.authorgenie.ui.main.MainViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoalStatusHandlerThread extends HandlerThread {

    Handler handler;
    MainViewModel mViewModel;
    Context context;

    public GoalStatusHandlerThread(String name, MainViewModel viewModel, Context context) {
        super(name);
        mViewModel = viewModel;
        this.context = context;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = null;
                    List<Goal> goalsEnded = mViewModel.getFinishedGoals();
                    for (Goal goal : goalsEnded) {
                        if (goal.isMet() && goal.getProgress() > goal.getObjective()) {
                            message = "You exceeded your goal by " + (goal.getProgress() -
                                    goal.getObjective()) + "!";
                        } else if (goal.isMet()) {
                            message = goal.getName() + " completed, congratulations!";
                        }

                        if (message != null) {
                            showToast(message);
                        }
                        mViewModel.addGoal(new Goal(goal.getObjective(), goal.getGoalTypeId()));
                    }
                    Log.d("Handlers", "Called on handler thread");
                    handler.postDelayed(this, TimeUnit.MINUTES.toMillis(1));
                }
            }
        };
// Start the initial runnable task by posting through the handler
        handler.post(runnable);
    }
    private void showToast(String message){
            Handler mainHandler = new Handler(context.getMainLooper());

        // This is your code
        Runnable myRunnable = () -> Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            mainHandler.post(myRunnable);
    }
}
