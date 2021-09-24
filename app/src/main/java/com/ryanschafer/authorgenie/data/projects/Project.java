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
    private long deadline;
    private boolean current;

//    Use when recreating from database
    public Project(int id, String name, int wordGoalTotal, int wordCountTotal, long deadline, boolean current) {
        this.id = id;
        this.name = name;
        this.wordGoalTotal = wordGoalTotal;
        this.wordCountTotal = wordCountTotal;
        this.deadline = deadline;
        this.current = current;
    }

//    use when creating for the first time
    @Ignore
    public Project(String name, int wordGoalTotal, long deadline) {
        this.name = name;
        this.wordGoalTotal = wordGoalTotal;
        this.wordCountTotal = 0;
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

    public long getDeadline() {
        return deadline;
    }

    public void setWordCountTotal(int wordCountTotal) {
        this.wordCountTotal = wordCountTotal;
    }

    public boolean isCurrent() {
        return current;
    }
}
