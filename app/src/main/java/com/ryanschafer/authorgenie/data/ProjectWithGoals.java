package com.ryanschafer.authorgenie.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.data.projects.Project;

import java.util.List;

public class ProjectWithGoals {
    @Embedded
    public Project project;
    @Relation(
            parentColumn = "primary_id",
            entityColumn = "projectId"
    )
    public List<Goal> goals;
}
