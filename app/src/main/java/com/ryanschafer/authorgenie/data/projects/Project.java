package com.ryanschafer.authorgenie.data.projects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.ryanschafer.authorgenie.data.AGItem;
import com.ryanschafer.authorgenie.data.goals.Goal;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

@Entity(tableName = "project_table")
public class Project implements AGItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primary_id")
    private int id;
    private String name;
    private int wordGoal;
    private int wordCount;
    private int timeCount;
    private long deadline;
    private boolean current;
    private boolean defaultProject = false;

//    Use when recreating from database
    public Project(int id, String name, int wordGoalTotal, int wordCountTotal, long deadline, boolean current) {
        this.id = id;
        this.name = name;
        this.wordGoal = wordGoalTotal;
        this.wordCount = wordCountTotal;
        this.deadline = deadline;
        this.current = current;
    }

//    use when creating for the first time
    @Ignore
    public Project(String name, int wordGoalTotal, long deadline) {
        this.name = name;
        this.wordGoal = wordGoalTotal;
        this.wordCount = 0;
        this.deadline = deadline;
        this.current = deadline > Calendar.getInstance().getTimeInMillis();
    }
    @Ignore
    protected Project(String name, boolean defaultProject) {
        this.name = name;
        this.current = true;
        this.defaultProject = defaultProject;
        wordGoal = 0;
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

    public int getWordGoal() {
        return wordGoal;
    }

    public int getWordCount() {
        return wordCount;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
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

    public void setWordGoal(int wordGoal) {
        this.wordGoal = wordGoal;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public void addProgress(int progress, Goal.TYPE inputType) {
        if(inputType == Goal.TYPE.WORD ){
            setWordCount(wordCount + progress);
        }else if(inputType == Goal.TYPE.TIME){
            setTimeCount(timeCount + progress);
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Project)){
            return false;
        }
        Project proj = (Project)obj;
        return  id == proj.getId() && wordCount == proj.getWordCount()
                && wordGoal == proj.getWordGoal() && timeCount == proj.getTimeCount();
    }
}
