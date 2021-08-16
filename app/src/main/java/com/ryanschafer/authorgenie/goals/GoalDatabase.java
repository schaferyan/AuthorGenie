package com.ryanschafer.authorgenie.goals;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Goal.class}, version = 2, exportSchema = false)
public abstract class GoalDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
    private static volatile GoalDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService dbWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static GoalDatabase getInstance(final Context context){
        if(INSTANCE == null){
            synchronized(GoalDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GoalDatabase.class, "goal_database")
                            .addCallback(roomCallback)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
//            dbWriteExecutor.execute(() -> {
////                GoalDao dao = INSTANCE.goalDao();
////                dao.clearGoals();
//            });
        }

    };

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE goal_table"
//                    + " DELETE COLUMN met INTEGER");
//            private int id;
//    private long deadline;
//    private final int objective;
//    private int progress;
//    private int goalTypeId;
//    private String name;
//    private boolean current;
//    private boolean notified;
//    private boolean recurring;
            database.execSQL(
                    "CREATE TABLE goal_table_new (id INTEGER, deadline INTEGER, objective INTEGER, progress INTEGER," +
                            "goalTypeId INTEGER, name TEXT, current INTEGER, notified INTEGER, " +
                            "recurring INTEGER, PRIMARY KEY(id))");
            database.execSQL(
                    "INSERT INTO goal_table_new (id , deadline , objective , progress , " +
                            "goalTypeId , name , current , notified) SELECT id , deadline , objective , progress , " +
                            "goalTypeId , name , current , notified FROM goal_table"
            );
            database.execSQL("DROP TABLE goal_table");
            database.execSQL("ALTER TABLE goal_table_new RENAME TO goal_table");
        }
    };


}
