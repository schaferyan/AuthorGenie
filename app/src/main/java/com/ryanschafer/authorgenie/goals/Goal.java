package com.ryanschafer.authorgenie.goals;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity(tableName = "goal_table")
public class Goal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "primary_id")
    private int id;
    private long deadline;
    private final int objective;
    private int progress;
    private int goalTypeId;
    private String name;
    private boolean current;
    private boolean notified;
    private boolean recurring;

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
        notified = false;
    }
    @Ignore
    public Goal(int objective, TYPE type, DURATION duration, boolean recurring) {
        this.objective = objective;
        this.progress = 0;
        this.type = type;
        this.duration = duration;
        setGoalTypeId();
        setName();
        setDeadline();
        current = true;
        notified = false;
        this.recurring = recurring;
    }

    public Goal(int id, long deadline, int objective, int progress, int goalTypeId, String name, boolean current, boolean notified, boolean recurring) {
        this.id = id;
        this.deadline = deadline;
        this.objective = objective;
        this.progress = progress;
        this.goalTypeId = goalTypeId;
        this.name = name;
        this.current = current;
        this.notified = notified;
        this.recurring = recurring;
        setEnums();
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
            calendar.add(Calendar.MONTH, 1);
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

    public int getProgress() {
        return progress;
    }

    public int getGoalTypeId() {
        return goalTypeId;
    }
    
    public static int getGoalTypeId(int typeIndex, int durationIndex){
        TYPE type = TYPE.values()[typeIndex];
        DURATION duration = DURATION.values()[durationIndex];
        if (type == TYPE.WORD) {
            if (duration == DURATION.DAY) {
                return DAY_WORD;
            } else if (duration == DURATION.WEEK) {
                return WEEK_WORD;
            } else if (duration == DURATION.MONTH) {
                return MONTH_WORD;
            } else {
                return OTHER;
            }
        } else if (type == TYPE.TIME) {
            if (duration == DURATION.DAY) {
                return DAY_TIME;
            } else if (duration == DURATION.WEEK) {
                return WEEK_TIME;
            } else if (duration == DURATION.MONTH) {
                return MONTH_TIME;
            } else {
                return OTHER;
            }
        } else {
            return OTHER;
        }
    }

    public void addProgress(int progress) {
        this.progress += progress;
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
        return(progress >= objective);

    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
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

        return current;
    }


    public int getId() {
        return id;
    }

    public long getDeadline() {
        return deadline;
    }



    public static List<String> getDurations(){
        List<String> durations = new ArrayList<>();
        for(Goal.DURATION duration : Goal.DURATION.values()){
            durations.add(duration.toString());
        }
        return durations;
    }


    public static List<String> getGoalTypes(){
        List<String> types = new ArrayList<>();
        for(Goal.TYPE type : Goal.TYPE.values()){
            types.add(type.toString());
        }
        return types;
    }

    public boolean dueToday(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(deadline -1000);
        int monthDue = calendar.get(Calendar.MONTH);
        int dayDue = calendar.get(Calendar.DAY_OF_MONTH);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        return currentMonth == monthDue && currentDay == dayDue;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
}
