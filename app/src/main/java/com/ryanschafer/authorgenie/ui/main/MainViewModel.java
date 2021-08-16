package com.ryanschafer.authorgenie.ui.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.ryanschafer.authorgenie.goals.Goal;
import com.ryanschafer.authorgenie.goals.GoalRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Goal>> currentGoals;
    private final LiveData<List<Goal>> allGoals;
    private final GoalRepository goalRepository;
    private List<Goal> cachedGoals;

//    private final ArrayList<Goal> currentGoals = new ArrayList<>();


    public MainViewModel(Application application){
        super(application);
        goalRepository = new GoalRepository(application);
        currentGoals = goalRepository.getCurrentGoals();
        allGoals = goalRepository.getAllGoals();
        cachedGoals = new ArrayList<>();
    }


    public void addGoal(Goal goal){
         goalRepository.addGoal(goal);
    }


    public void removeGoal(Goal goal){

        goalRepository.delete(goal);
    }

//    Adds progress to goals in the list passed with the specified input type, then updates
//    the database
    public void addProgress(int progress, Goal.TYPE inputType, List<Goal> activeGoals) {
        for(Goal goal : activeGoals){
            if(goal.getType() == inputType){
                goal.addProgress(progress);
                goalRepository.updateGoal(goal);
            }
        }
    }

    public LiveData<List<Goal>> getCurrentGoals(){
        return currentGoals;
    }


    public void setGoalNotified(Goal goal, boolean b) {
        goalRepository.setGoalNotified(goal, b);
    }

    public void updateGoal(Goal goal) {
        goalRepository.updateGoal(goal);
    }

    public List<Goal> getUnannouncedMetGoals(List<Goal> currentList) {
        List<Goal> metGoals = new ArrayList<>();
        for(Goal goal : currentList){
            if(goal.isMet() && !goal.isNotified()){
                metGoals.add(goal);
                goal.setNotified(true);
            }
        }
        return metGoals;
    }

    public List<Goal> getGoalsAsList() {
        return goalRepository.getGoalsAsList();
    }

    public LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

    public List<Goal> getCachedGoals() {
        return cachedGoals;
    }

    public void setCachedGoals(List<Goal> cachedGoals) {
        this.cachedGoals = cachedGoals;
    }
}