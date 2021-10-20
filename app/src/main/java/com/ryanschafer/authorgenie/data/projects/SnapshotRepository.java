package com.ryanschafer.authorgenie.data.projects;

import android.app.Application;

import com.ryanschafer.authorgenie.data.AGDatabase;

public class SnapshotRepository {
    private final SnapshotDao snapshotDao;


    public SnapshotRepository(Application application){
        AGDatabase db = AGDatabase.getInstance(application);
        snapshotDao = db.snapshotDao();
    }

    public void updateSnapshot(ProjectSnapshot snapshot) {
        AGDatabase.dbWriteExecutor.execute(()->snapshotDao.update(snapshot));
    }

    public void addSnapshot(ProjectSnapshot snapshot){
        AGDatabase.dbWriteExecutor.execute(()->snapshotDao.insert(snapshot));
    }
}
