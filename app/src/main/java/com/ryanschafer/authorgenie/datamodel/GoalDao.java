package com.ryanschafer.authorgenie.datamodel;

import androidx.annotation.BoolRes;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Update
    void updateGoals(Goal... goals);

    @Update
    void updateGoalList(List<Goal> goals);

    @Query("SELECT * FROM goal_table WHERE current = 1")
    LiveData<List<Goal>> getCurrentGoals();

    @Query("SELECT * FROM goal_table WHERE current = 1")
    List<Goal> getCurrentGoalsAsList();
}
