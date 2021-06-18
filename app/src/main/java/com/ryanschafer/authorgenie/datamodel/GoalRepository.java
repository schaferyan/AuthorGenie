package com.ryanschafer.authorgenie.datamodel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GoalRepository {


    private final GoalDao goalDao;
    private final LinkedHashMap<Integer, Goal> activeGoalsMap = new LinkedHashMap<>();
    private final LiveData<List<Goal>> goals;


    public GoalRepository(Application application){
        GoalDatabase db = GoalDatabase.getInstance(application);
        goalDao = db.goalDao();
        goals = goalDao.getCurrentGoals();
//        if a goal is added to or edited in the database, make the same modification to the hashmap
        goals.observeForever(goals1 -> {
            for(Goal goal : goals1){
                activeGoalsMap.put(goal.getGoalTypeId(), goal);
            }
        });

    }

    public boolean addGoal(@NotNull Goal goal){
        synchronized (activeGoalsMap) {
            if (activeGoalsMap.containsKey(goal.getGoalTypeId())) {
                return false;
            } else {
                activeGoalsMap.put(goal.getGoalTypeId(), goal);
                GoalDatabase.dbWriteExecutor.execute(() -> goalDao.insert(goal));

                return true;
            }
        }
    }
    public void replaceGoal(Goal newGoal) {
        Goal oldGoal = findGoalById(newGoal.getGoalTypeId());
        activeGoalsMap.put(newGoal.getGoalTypeId(), newGoal);
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.delete(oldGoal));
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.insert(newGoal));
    }

    public void removeGoal(Goal goal){
        activeGoalsMap.remove(goal.getGoalTypeId());
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.delete(goal));
    }

    public void updateAllProgress(int progress, Goal.TYPE progressType){
        synchronized (activeGoalsMap) {
            for (Goal goal : activeGoalsMap.values()) {
                if (goal.getType() == progressType) {
                    goal.addProgress(progress);
                    GoalDatabase.dbWriteExecutor.execute(() -> goalDao.update(goal));
                }
            }
        }
    }

    public List<Goal> getFinishedGoals(){
        List<Goal> goalsEnded = new ArrayList<>();
        synchronized (activeGoalsMap) {
            for (Goal goal : activeGoalsMap.values()) {
                if (!goal.isCurrent()) {
                    GoalDatabase.dbWriteExecutor.execute(() -> goalDao.update(goal));
                    goalsEnded.add(goal);
                }
            }
        }
        for(Goal goal: goalsEnded){
            activeGoalsMap.remove(goal.getGoalTypeId());
        }
        return goalsEnded;
    }


    public Goal findGoalById(int id){
        return activeGoalsMap.get(id);
    }

    public LiveData<List<Goal>> getGoals() {
        return goals;
    }

    public List<Goal> getUnannouncedMetGoals() {
        List<Goal> metGoals = new ArrayList<>();
        synchronized (activeGoalsMap) {
            for (Goal goal : activeGoalsMap.values()) {
                if (goal.isMet() && !goal.isNotified()) {
                    GoalDatabase.dbWriteExecutor.execute(() -> goalDao.update(goal));
                    metGoals.add(goal);
                }
            }
        }
        return metGoals;
    }


    public void setGoalNotified(Goal goal, boolean b) {
        Goal updatedGoal = activeGoalsMap.get(goal.getGoalTypeId());
        if (updatedGoal != null) {
            updatedGoal.setNotified(b);
        }
        GoalDatabase.dbWriteExecutor.execute(()-> goalDao.update(updatedGoal));
    }
}
