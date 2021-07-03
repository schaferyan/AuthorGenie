package com.ryanschafer.authorgenie.ui.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.ryanschafer.authorgenie.datamodel.Goal;
import com.ryanschafer.authorgenie.datamodel.GoalRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Goal>> goals;
    private final GoalRepository goalRepository;
    private boolean showFooter = false;
//    private final ArrayList<Goal> currentGoals = new ArrayList<>();


    public MainViewModel(Application application){
        super(application);
        goalRepository = new GoalRepository(application);
        goals = goalRepository.getGoals();
    }


    public void addGoal(Goal goal){
         goalRepository.addGoal(goal);
    }


    public void removeGoal(Goal goal){

        goal.setCurrent(false);
        goalRepository.updateGoal(goal);
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

    public LiveData<List<Goal>> getGoals(){
        return goals;
    }


    public void setGoalNotified(Goal goal, boolean b) {
        goalRepository.setGoalNotified(goal, b);
    }

    public boolean showFooter() {
        return showFooter;
    }

    public void setShowFooter(boolean showFooter) {
        this.showFooter = showFooter;
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
}