package com.ryanschafer.authorgenie.goal_data;

import android.app.Application;


import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import java.util.List;

public class GoalRepository {


    private final GoalDao goalDao;
    private final LiveData<List<Goal>> goals;
    private final LiveData<List<Goal>> allGoals;

    public GoalRepository(Application application){
        GoalDatabase db = GoalDatabase.getInstance(application);
        goalDao = db.goalDao();
//        Calendar calendar = Calendar.getInstance();
//        long timeInMillis = calendar.getTimeInMillis();
        goals = goalDao.getCurrentGoals();
        allGoals = goalDao.getAllGoals();
    }

    public void addGoal(@NotNull Goal goal){

        GoalDatabase.dbWriteExecutor.execute(() -> goalDao.insert(goal));

    }

    public LiveData<List<Goal>> getCurrentGoals() {
        return goals;
    }



    public void setGoalNotified(Goal goal, boolean b) {
        goal.setNotified(b);
        updateGoal(goal);
    }

    public void updateGoal(Goal goal) {
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.update(goal));
    }

    public void updateGoals(Goal... goals) {
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.updateGoals(goals));
    }

    public void updateGoals(List<Goal> goals){
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.updateGoalList(goals));
    }

    public List<Goal> getGoalsAsList() {

        return new ArrayList<>(goalDao.getCurrentGoalsAsList());
    }



    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

    public void delete(Goal goal) {
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.delete(goal));
    }
}
