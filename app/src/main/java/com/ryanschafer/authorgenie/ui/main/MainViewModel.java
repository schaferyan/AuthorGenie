package com.ryanschafer.authorgenie.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.data.goals.GoalRepository;
import com.ryanschafer.authorgenie.data.projects.DefaultProject;
import com.ryanschafer.authorgenie.data.projects.Project;
import com.ryanschafer.authorgenie.data.projects.ProjectRepository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Goal>> currentGoals;
    private final LiveData<List<Goal>> allGoals;
    private final LiveData<List<Project>> currentProjects;
    private final LiveData<DefaultProject> defaultProjectLiveData;
    private DefaultProject defaultProject;
    private final GoalRepository goalRepository;
    private final ProjectRepository projectRepository;
    private List<Goal> cachedGoals;




    public MainViewModel(Application application){
        super(application);
        goalRepository = new GoalRepository(application);
        projectRepository = new ProjectRepository(application);
        defaultProjectLiveData = projectRepository.getDefaultProject();
        currentProjects = projectRepository.getCurrentProjects();
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
    public void addProgress(int progress, Goal.TYPE inputType) {
        for(Goal goal : cachedGoals){
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


    public void addProject(Project project) {
        projectRepository.addProject(project);
    }

    public LiveData<List<Project>> getCurrentProjects() {
        return currentProjects;
    }

    public List<Goal> getFilteredGoals(Project project) {
        if(project.isDefaultProject()){
            return getCachedGoals();
        }
        List<Goal> filteredList = new ArrayList<>();
        for (Goal goal: cachedGoals) {
            if(goal.getProjectId() == project.getId()){
                filteredList.add(goal);
            }
        }
        return filteredList;
    }

    public void removeProject(Project project) {
        projectRepository.delete(project);
    }

    public LiveData<DefaultProject> getDefaultProjectLiveData() {
        return defaultProjectLiveData;
    }

//    public DefaultProject getDefaultProject() {
//        return defaultProject;
//    }
//
//    public void setDefaultProject(DefaultProject defaultProject) {
//        this.defaultProject = defaultProject;
//    }

    public List<Goal> getUnannouncedMetGoals() {
        return getUnannouncedMetGoals(cachedGoals);
    }
}