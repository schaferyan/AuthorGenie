package com.ryanschafer.authorgenie.data.projects;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ryanschafer.authorgenie.data.projects.Project;

import java.util.List;

@Dao
public interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Project project);

    @Update
    void update(Project project);

    @Delete
    void delete(Project project);

    @Update
    void updateProjects(Project... projects);

    @Update
    void updateProjectList(List<Project> projects);

    @Query("SELECT * FROM project_table WHERE current = 1")
    LiveData<List<Project>> getCurrentProjects();

    @Query("SELECT * FROM project_table ORDER BY deadline ASC")
    LiveData<List<Project>> getAllProjects();

    @Query("SELECT * FROM project_table WHERE current = 1")
    List<Project> getCurrentProjectsAsList();

}
