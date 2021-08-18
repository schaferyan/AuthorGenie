package com.ryanschafer.authorgenie.goal_data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Delete
    void delete(Goal goal);

    @Update
    void updateGoals(Goal... goals);

    @Update
    void updateGoalList(List<Goal> goals);

    @Query("SELECT * FROM goal_table WHERE current = 1")
    LiveData<List<Goal>> getCurrentGoals();

    @Query("SELECT * FROM goal_table ORDER BY deadline ASC")
    LiveData<List<Goal>> getAllGoals();

    @Query("SELECT * FROM goal_table WHERE current = 1")
    List<Goal> getCurrentGoalsAsList();

    @Query("SELECT * FROM goal_table WHERE goalTypeId = :typeId ORDER BY deadline ASC")
    LiveData<List<Goal>> queryGoalsByTypeId(int typeId);
}
