package com.ryanschafer.authorgenie3.Model;

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

    @Delete
    void delete(Goal goal);

    @Update
    void update(Goal goal);

    @Update
    void updateAll(List<Goal> goals);

    @Query("SELECT * FROM goal_table WHERE current = 1")
    LiveData<List<Goal>> getCurrentGoals();

    @Query("SELECT * FROM goal_table")
    LiveData<List<Goal>> getAllGoals();

    @Query("DELETE FROM goal_TABLE")
    void clearGoals();

    @Query("SELECT * FROM goal_table WHERE current = 1 AND name = :name ORDER BY deadline ASC")
    LiveData<Goal> getCurrentGoalByName(String name);
}
