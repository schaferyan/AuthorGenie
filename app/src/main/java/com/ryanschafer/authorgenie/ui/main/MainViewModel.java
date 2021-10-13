package com.ryanschafer.authorgenie.ui.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ryanschafer.authorgenie.data.AGItem;
import com.ryanschafer.authorgenie.data.projects.ProjectSnapshot;
import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.data.goals.GoalRepository;
import com.ryanschafer.authorgenie.data.projects.Project;
import com.ryanschafer.authorgenie.data.projects.ProjectRepository;
import com.ryanschafer.authorgenie.data.projects.SnapshotRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Goal>> currentGoals;
    private final LiveData<List<Goal>> allGoals;
    private final LiveData<List<Project>> currentProjects;
    private final GoalRepository goalRepository;
    private final ProjectRepository projectRepository;
    private List<Goal> cachedGoals;
    private List<Project> cachedProjects;
    private Project defaultProject;
    private SnapshotRepository snapshotRepository;


    public MainViewModel(Application application){
        super(application);
        goalRepository = new GoalRepository(application);
        projectRepository = new ProjectRepository(application);
        snapshotRepository = new SnapshotRepository(application);
        currentProjects = projectRepository.getCurrentProjects();
        currentGoals = goalRepository.getCurrentGoals();
        allGoals = goalRepository.getAllGoals();
        cachedGoals = new ArrayList<>();
        cachedProjects = new ArrayList<>();
    }


    public void addGoal(Goal goal){
         goalRepository.addGoal(goal);
    }


    public void removeGoal(Goal goal){
//        goalRepository.delete(goal);
        goal.setCurrent(false);
        goalRepository.updateGoal(goal);
    }


    public boolean addProgress(int progress, Goal.TYPE inputType, @NonNull Project project,
                            boolean total) {
        if(total){
            return setProjectTotal(progress, inputType, project);
        } else {
            addProgressToProject(progress, inputType, project);
            return true;
        }
    }

    private void addProgressToProject(int progress, Goal.TYPE inputType, Project project){
        addProgressToGoals(progress, inputType, project);
        project.addProgress(progress, inputType);
        projectRepository.updateProject(project);
        if(!project.isDefaultProject()){
            if(defaultProject != null) {
                addProgressToProject(progress, inputType, defaultProject);
            }
        }
    }

    private Project findDefaultProject(List<Project> projects) {
        for(Project project: projects){
            if(project.isDefaultProject()){
                return project;
            }
        }
        return null;
    }

    private void addProgressToGoals(int progress, Goal.TYPE inputType, Project project){
        for (Goal goal : cachedGoals) {
            if (goal.getType() == inputType && goal.getProjectId() == project.getId()) {
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

    public void cacheGoals(List<Goal> cachedGoals) {
        this.cachedGoals.addAll(cachedGoals);
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
//        projectRepository.delete(project);
        project.setCurrent(false);
        projectRepository.updateProject(project);
        removeProjectGoals(project);
    }
    public void removeProjectGoals(Project project){
        for(Goal goal: cachedGoals){
            if(goal.getProjectId() == project.getId()) {
                removeGoal(goal);
            }
        }
    }
    public List<Goal> getUnannouncedMetGoals() {
        return getUnannouncedMetGoals(cachedGoals);
    }

    public void addItem(Object item) {
        if(item instanceof Goal){
            addGoal((Goal) item);
        }else if(item instanceof Project){
            addProject((Project) item);
        }
    }

    public void removeItem(AGItem item) {
        item.setCurrent(false);
        if(item instanceof Project){
            removeProjectGoals((Project) item);
        }
    }

    public boolean setProjectTotal(int progress, Goal.TYPE inputType, Project project) {
        int diff;
        if(inputType ==  Goal.TYPE.WORD) {
            diff = progress - project.getWordCount();
            project.setWordCount(progress);
        }else if(inputType == Goal.TYPE.TIME){
            diff = progress - project.getTimeCount();
            project.setTimeCount(progress);
        }else{
            return false;
        }
        if(diff <= 0){
            return false;
        }
        if(project != defaultProject){
            if(defaultProject != null) {
                addProgressToProject(diff, inputType, defaultProject);
            }
        }
        addProgressToGoals(diff, inputType, project);
        projectRepository.updateProject(project);
        return true;
    }

    public void setDefaultProject(List<Project> projects) {
        defaultProject = findDefaultProject(projects);
    }

    public Project getDefaultProject() {
        return defaultProject;
    }

    public void undoDelete(AGItem item) {
        item.setCurrent(true);
        if(item instanceof Goal){
            goalRepository.updateGoal((Goal) item);
        }else if(item instanceof Project){
            projectRepository.updateProject((Project) item);
        }
    }

    public List<Project> getCachedProjects() {
        return cachedProjects;
    }

    public void cacheProjects(List<Project> cachedProjects) {
        this.cachedProjects.addAll(cachedProjects);
    }

    public void makeSnapshots(Context context,  List<Project> projects) {
        List<Project>modProjects = new ArrayList<>(projects);

//        find changed elements
        modProjects.removeAll(cachedProjects);
        for(Project project : modProjects){
            ProjectSnapshot snapshot = new ProjectSnapshot(project.getId(),
                    project.getWordCount(), project.getWordGoal(), project.getTimeCount(), Calendar.getInstance().getTimeInMillis());
            snapshotRepository.updateSnapshot(snapshot);
        }

////        add ids to array so we can pass them to Worker
//        int[] arr = new int[modProjects.size()];
//        for (int i = 0; i < modProjects.size(); i++) {
//            arr[i] = modProjects.get(i).getId();
//        }
////        build work request, passing in our array of ids corresponding to changed projects
//        WorkRequest workRequest = new OneTimeWorkRequest.Builder(
//                ProjectSnaphotMaker.class)
//                .setInputData(
//                        new Data.Builder()
//                                .putIntArray("MODIFIED_PROJECT_IDS", arr)
//                                .build()
//                )
//                                .build();
//
//        WorkManager
//                .getInstance(context)
//                .enqueue(workRequest);
    }

//    public class ProjectSnaphotMaker extends Worker{
//
//        public ProjectSnaphotMaker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//            super(context, workerParams);
//        }
//
//        @NonNull
//        @Override
//        public Result doWork() {
//            int[] ids = getInputData().getIntArray("MODIFIED_PROJECT_IDS");
//            if(ids == null){
//                return Result.failure();
//            }
//            for (int id: ids) {
//                snapshotRepository.update
//            }
//            return Result.success();
//        }
//    }
}