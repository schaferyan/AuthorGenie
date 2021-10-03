package com.ryanschafer.authorgenie.data.projects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

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
    private boolean defaultProject;

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
        this.current = deadline > Calendar.getInstance().getTimeInMillis();
    }
    @Ignore
    public Project(String name, boolean defaultProject) {
        this.name = name;
        this.current = true;
        this.defaultProject = defaultProject;
        wordGoalTotal = 0;
        deadline = 0;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
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

    public boolean dueToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(deadline -1000);
        int monthDue = calendar.get(Calendar.MONTH);
        int dayDue = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return currentMonth == monthDue && currentDay == dayDue;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return getName();
    }

    public boolean isDefaultProject() {
        return defaultProject;
    }

    public void setDefaultProject(boolean defaultProject) {
        this.defaultProject = defaultProject;
    }

    public void setWordGoalTotal(int wordGoalTotal) {
        this.wordGoalTotal = wordGoalTotal;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }
}
