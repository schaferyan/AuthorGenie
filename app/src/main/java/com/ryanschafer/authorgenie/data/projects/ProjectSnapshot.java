package com.ryanschafer.authorgenie.data.projects;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ProjectSnapshot {
    @PrimaryKey
    private final int id;
    private final int wordCount;
    private final int wordGoal;
    private final int timeCount;

    public ProjectSnapshot(int id, int wordCountTotal, int wordGoalTotal, int timeCountTotal, long date) {
    this.id = id;
    this.wordCount = wordCountTotal;
    this.wordGoal = wordGoalTotal;
    this.timeCount = timeCountTotal;
    }
    public boolean equalToProject(Project project){
        return id == project.getId() && wordCount == project.getWordCount()
                && wordGoal == project.getWordGoal() && timeCount == project.getTimeCount();
    }

    public int getId() {
        return id;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getWordGoal() {
        return wordGoal;
    }

    public int getTimeCount() {
        return timeCount;
    }
}
