package com.ryanschafer.authorgenie.data.projects;

import androidx.room.Dao;
import androidx.room.Update;

@Dao
public interface SnapshotDao {
    @Update
    void update(ProjectSnapshot snapshot);
}
