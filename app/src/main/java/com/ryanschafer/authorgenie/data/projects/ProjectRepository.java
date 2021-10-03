package com.ryanschafer.authorgenie.data.projects;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.ryanschafer.authorgenie.data.AGDatabase;
import com.ryanschafer.authorgenie.data.ProjectWithGoals;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProjectRepository {


    private final ProjectDao projectDao;


    public ProjectRepository(Application application){
        AGDatabase db = AGDatabase.getInstance(application);
        projectDao = db.projectDao();
    }

    public void addProject(@NotNull Project project){

        AGDatabase.dbWriteExecutor.execute(() -> projectDao.insert(project));

    }

    public LiveData<List<Project>> getCurrentProjects() {
        return projectDao.getCurrentProjects();
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
        return projectDao.getAllProjects();
    }

    public void delete(Project project) {
        AGDatabase.dbWriteExecutor.execute(()-> projectDao.delete(project));
    }

    public LiveData<DefaultProject> getDefaultProject() {
        return projectDao.getDefaultProject();
    }
}
