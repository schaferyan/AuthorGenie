package com.ryanschafer.authorgenie3.Model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity(tableName = "goal_table")
public class Goal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primary_id")
    private int id;
    private long deadline;
    private int objective;
    private int progress;
    private int goalTypeId;
    private String name;
    private boolean current;
    private boolean met;

    @Ignore
    private DURATION duration;
    @Ignore
    private TYPE type;

    @Ignore
    public static final int DAY_WORD = 0;
    @Ignore
    public static final int DAY_TIME = 1;
    @Ignore
    public static final int WEEK_WORD = 2;
    @Ignore
    public static final int WEEK_TIME = 3;
    @Ignore
    public static final int MONTH_WORD = 4;
    @Ignore
    public static final int MONTH_TIME = 5;
    @Ignore
    public static final int OTHER = 6;

    public enum DURATION {
        DAY {
            @NonNull
            @Override
            public String toString() {
                return "Daily";
            }
        },
        WEEK {
            @NonNull
            @Override
            public String toString() {
                return "Weekly";
            }
        },
        MONTH {
            @NonNull
            @Override
            public String toString() {
                return "Monthly";
            }
        },
    }

    public enum TYPE {
        WORD {
            @NonNull
            @Override
            public String toString() {
                return "Word";
            }
        },
        TIME{
            @NonNull
            @Override
            public String toString() {
                return "Minute";
            }
        },
    }

    @Ignore
    public Goal(int objective, TYPE type, DURATION duration) {
        this.objective = objective;
        this.progress = 0;
        this.type = type;
        this.duration = duration;
        setGoalTypeId();
        setName();
        setDeadline();
        current = true;
    }
    @Ignore
    public Goal(int objective, int goalTypeId){
        this.objective = objective;
        this.goalTypeId = goalTypeId;
        this.progress = 0;
        setEnums();
        setName();
        setDeadline();
        current = true;
    }

    public Goal(int id, long deadline, int objective, int progress, int goalTypeId, String name, boolean current, boolean met) {
        this.id = id;
        this.deadline = deadline;
        this.objective = objective;
        this.progress = progress;
        this.goalTypeId = goalTypeId;
        this.met = met;
        this.name = name;
        this.current = current;
        setEnums();
        isCurrent();
    }

    private void setGoalTypeId() {
        if (type == TYPE.WORD) {
            if (duration == DURATION.DAY) {
                goalTypeId = DAY_WORD;
            } else if (duration == DURATION.WEEK) {
                goalTypeId = WEEK_WORD;
            } else if (duration == DURATION.MONTH) {
                goalTypeId = MONTH_WORD;
            } else {
                goalTypeId = OTHER;
            }
        } else if (type == TYPE.TIME) {
            if (duration == DURATION.DAY) {
                goalTypeId = DAY_TIME;
            } else if (duration == DURATION.WEEK) {
                goalTypeId = WEEK_TIME;
            } else if (duration == DURATION.MONTH) {
                goalTypeId = MONTH_TIME;
            } else {
                goalTypeId = OTHER;
            }
        } else {
            goalTypeId = OTHER;
        }
    }
    
    private void setEnums(){
        switch(goalTypeId){
            case DAY_WORD:
                duration = DURATION.DAY;
                type = TYPE.WORD;
                break;
            case DAY_TIME:
                duration = DURATION.DAY;
                type = TYPE.TIME;
                break;
            case WEEK_WORD:
                duration = DURATION.WEEK;
                type = TYPE.WORD;
                break;
            case WEEK_TIME:
                duration = DURATION.WEEK;
                type = TYPE.TIME;
                break;
            case MONTH_WORD:
                duration = DURATION.MONTH;
                type = TYPE.WORD;
                break;
            case MONTH_TIME:
                duration = DURATION.MONTH;
                type = TYPE.TIME;
                break;
            case OTHER:
                break;
        }
        
    }

    private void setDeadline() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (goalTypeId <= 1) {
            calendar.add(Calendar.DATE, 1);
        } else if (goalTypeId <= 3) {
            calendar.add(Calendar.DATE, 7);
        } else if (goalTypeId <= 5) {
            calendar.add(Calendar.DATE, 1);
        }
        deadline = calendar.getTimeInMillis();
    }
    public void setDeadline(long millis){
        this.deadline = millis;
    }

    public Date getDeadlineAsDate() {
        return new Date(deadline);
    }


    public int getObjective() {
        return objective;
    }

    public void setObjective(int objective) {
        this.objective = objective;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGoalTypeId() {
        return goalTypeId;
    }

    public void setGoalTypeId(int goalTypeId) {
        this.goalTypeId = goalTypeId;
    }

    public void addProgress(int progress) {
        this.progress += progress;
        met = this.progress >= objective;
    }

    public DURATION getDuration() {
        return duration;
    }

    public TYPE getType() {
        return type;
    }

    public void setName() {
        if (goalTypeId < OTHER) {
            name = duration.toString() + " " + type.toString() + " " + "Goal";
        }else{
            name = "Custom";
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isMet() {
        met = (progress >= objective);
        return met;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }


    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean isCurrent(){
        Date date = Calendar.getInstance().getTime();
        boolean b = !date.after(getDeadlineAsDate());
        setCurrent(b);
        return b;
    }

    public int getId() {
        return id;
    }

    public long getDeadline() {
        return deadline;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getDurations(){
        return Stream.of(Goal.DURATION.values()).map(Enum::toString).collect(Collectors.toList());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getGoalTypes(){
        return Stream.of(Goal.TYPE.values()).map(Enum::toString).collect(Collectors.toList());
    }
}
