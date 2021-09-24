package com.ryanschafer.authorgenie.data.projects;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.ryanschafer.authorgenie.data.AGDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {


    private final ProjectDao projectDao;
    private final LiveData<List<Project>> projects;
    private final LiveData<List<Project>> allProjects;

    public ProjectRepository(Application application){
        AGDatabase db = AGDatabase.getInstance(application);
        projectDao = db.projectDao();
        projects = projectDao.getCurrentProjects();
        allProjects = projectDao.getAllProjects();
    }

    public void addProject(@NotNull Project project){

        AGDatabase.dbWriteExecutor.execute(() -> projectDao.insert(project));

    }

    public LiveData<List<Project>> getCurrentProjects() {
        return projects;
    }

    public void updateProject(Project project) {
        AGDatabase.dbWriteExecutor.execute(()-> projectDao.update(project));
    }

    public void updateProjects(Project... projects) {
        AGDatabase.dbWriteExecutor.execute(()-> projectDao.updateProjects(projects));
    }

    public void updateProjects(List<Project> projects){
        AGDatabase.dbWriteExecutor.execute(()-> projectDao.updateProjectList(projects));
    }

    public List<Project> getProjectsAsList() {

        return new ArrayList<>(projectDao.getCurrentProjectsAsList());
    }



    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public void delete(Project project) {
        AGDatabase.dbWriteExecutor.execute(()-> projectDao.delete(project));
    }
}
