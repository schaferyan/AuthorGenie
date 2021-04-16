package com.ryanschafer.authorgenie3.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.ryanschafer.authorgenie3.Model.Goal;
import com.ryanschafer.authorgenie3.Model.GoalRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Goal>> goals;
    private final MutableLiveData<Integer> activeGoalPosition = new MutableLiveData<Integer>();
    private final GoalRepository goalRepository;
//    private final ArrayList<Goal> currentGoals = new ArrayList<>();


    public MainViewModel(Application application){
        super(application);
        goalRepository = new GoalRepository(application);
        goals = goalRepository.getGoals();
    }


    public boolean addGoal(Goal goal){
        return goalRepository.addGoal(goal);
    }

    public boolean addGoal(int goalAmount, int typeItemPosition, int durationItemPosition) {
        Goal goal = new Goal(goalAmount,
                Goal.TYPE.values()[typeItemPosition],
                Goal.DURATION.values()[durationItemPosition]);
                boolean b = goalRepository.addGoal(goal);
        return b;
    }

    public void overwriteGoal(Goal goal){
        goalRepository.replaceGoal(goal);
    }

    public void removeGoal(Goal goal){
        goalRepository.removeGoal(goal);
    }

    public void addProgress(int progress, Goal.TYPE inputType) {
        goalRepository.updateAllProgress(progress, inputType);
    }

    public void setActiveGoalPosition(Integer goal) {
        activeGoalPosition.setValue(goal);
    }

    public LiveData<List<Goal>> getGoals(){
        return goals;
    }

    public LiveData<Integer> getActiveGoalPositionAsLiveData(){
        return activeGoalPosition;
    }


    public void overwriteGoal(int goalAmount, int typeItemPosition, int durationItemPosition) {
        Goal goal = new Goal(goalAmount,
                Goal.TYPE.values()[typeItemPosition],
                Goal.DURATION.values()[durationItemPosition]);
        goalRepository.replaceGoal(goal);
    }

//    public ArrayList<Goal> getCurrentGoals() {
//        return currentGoals;
//    }

    public List<Goal> getFinishedGoals(){
       return goalRepository.getFinishedGoals();
    }

    public List<Goal> getMetGoals() {
        return goalRepository.getMetGoals();
    }
}