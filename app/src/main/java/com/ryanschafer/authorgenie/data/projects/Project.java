package com.ryanschafer.authorgenie.data.projects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "project_table")
public class Project {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primary_id")
    private int id;
    private final String name;
    private final int wordGoalTotal;
    private int wordCountTotal;
    private final long deadline;
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
        this.current = deadline > Calendar.getInstance().getTimeInMillis();
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

    public boolean dueToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(deadline -1000);
        int monthDue = calendar.get(Calendar.MONTH);
        int dayDue = calendar.get(Calendar.DAY_OF_MONTH);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        return currentMonth == monthDue && currentDay == dayDue;
    }
}
