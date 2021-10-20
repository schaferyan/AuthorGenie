package com.ryanschafer.authorgenie.data.projects;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ProjectSnapshot {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int project;
    private final int wordCount;
    private final int wordGoal;
    private final int timeCount;
    private final long date;

    public ProjectSnapshot(int id, int project, int wordCount, int wordGoal, int timeCount, long date) {
        this.id = id;
        this.project = project;
        this.wordCount = wordCount;
        this.wordGoal = wordGoal;
        this.timeCount = timeCount;
        this.date = date;
    }

    @Ignore
    public ProjectSnapshot(int project, int wordCount, int wordGoal, int timeCount, long date) {
    this.project = project;
    this.wordCount = wordCount;
    this.wordGoal = wordGoal;
    this.timeCount = timeCount;
    this.date = date;
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

    public long getDate() {
        return date;
    }

    public int getProject() {
        return project;
    }

}
