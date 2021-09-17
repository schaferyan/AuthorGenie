package com.ryanschafer.authorgenie.data.projects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "project_table")
public class Project {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primary_id")
    private int id;
    private String name;
    private int wordGoalTotal;
    private int wordCountTotal;
    private int timeCountTotal;
    private int timeGoalTotal;
    private long deadline;

//    Use when recreating from database
    public Project(int id, String name, int wordGoalTotal, int wordCountTotal, int timeCountTotal, int timeGoalTotal, long deadline) {
        this.id = id;
        this.name = name;
        this.wordGoalTotal = wordGoalTotal;
        this.wordCountTotal = wordCountTotal;
        this.timeCountTotal = timeCountTotal;
        this.timeGoalTotal = timeGoalTotal;
        this.deadline = deadline;
    }

//    use when creating for the first time
    @Ignore
    public Project(String name, int wordGoalTotal, int timeGoalTotal, long deadline) {
        this.name = name;
        this.wordGoalTotal = wordGoalTotal;
        this.timeGoalTotal = timeGoalTotal;
        this.wordCountTotal = 0;
        this.timeCountTotal = 0;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWordGoalTotal() {
        return wordGoalTotal;
    }

    public int getWordCountTotal() {
        return wordCountTotal;
    }

    public int getTimeCountTotal() {
        return timeCountTotal;
    }

    public int getTimeGoalTotal() {
        return timeGoalTotal;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setWordCountTotal(int wordCountTotal) {
        this.wordCountTotal = wordCountTotal;
    }

    public void setTimeCountTotal(int timeCountTotal) {
        this.timeCountTotal = timeCountTotal;
    }
}
