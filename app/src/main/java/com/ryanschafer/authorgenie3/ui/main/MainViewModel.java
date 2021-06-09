package com.ryanschafer.authorgenie3.ui.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.ryanschafer.authorgenie3.datamodel.Goal;
import com.ryanschafer.authorgenie3.datamodel.GoalRepository;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Goal>> goals;
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

    public void overwriteGoal(Goal goal){
        goalRepository.replaceGoal(goal);
    }

    public void removeGoal(Goal goal){
        goalRepository.removeGoal(goal);
    }

    public void addProgress(int progress, Goal.TYPE inputType) {
        goalRepository.updateAllProgress(progress, inputType);
    }

    public LiveData<List<Goal>> getGoals(){
        return goals;
    }

//    public ArrayList<Goal> getCurrentGoals() {
//        return currentGoals;
//    }

    public List<Goal> getFinishedGoals(){
       return goalRepository.getFinishedGoals();
    }

    public List<Goal> getUnannouncedMetGoals() {
        return goalRepository.getUnannouncedMetGoals();
    }

}