package com.example.workoutlog.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Database(
        entities = {
                MusclePartEntity.class,
                ExerciseEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private record Seed(String name, String primary, String secondary) {}

    private static volatile AppDatabase INSTANCE;

    public abstract MusclePartDao musclePartDao();
    public abstract ExerciseDao exerciseDao();

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "cybergym.db")
                            .addCallback(prepopulateCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Callback prepopulateCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase database = AppDatabase.INSTANCE;

                String[] partNames = {
                        "Chest", "Back", "Shoulders",
                        "Biceps", "Triceps", "Quads",
                        "Hamstrings", "Calves", "Core"
                };
                Map<String, Long> partIds = new HashMap<>();
                for (String partName : partNames) {
                    MusclePartEntity part = new MusclePartEntity();
                    part.name = partName;
                    long id = database.musclePartDao().insertPart(part);
                    partIds.put(partName, id);
                }

                List<Seed> seeds = Arrays.asList(
                        new Seed("Barbell Squat",   "Quads",     "Glutes"),
                        new Seed("Bench Press",     "Chest",     "Triceps"),
                        new Seed("Deadlift",        "Back",      "Hamstrings"),
                        new Seed("Overhead Press",  "Shoulders", "Triceps"),
                        new Seed("Barbell Row",     "Back",      "Biceps"),
                        new Seed("Bicep Curl",      "Biceps",    null),
                        new Seed("Tricep Push-down","Triceps",   null)
                );
                for (Seed seed : seeds) {
                    ExerciseEntity ex = new ExerciseEntity();
                    ex.name = seed.name();

                    Long primaryId = partIds.get(seed.primary());
                    ex.primaryPartId = (primaryId != null ? primaryId : 0L);

                    if (seed.secondary() != null) {
                        ex.secondaryPartId = partIds.get(seed.secondary());
                    } else {
                        ex.secondaryPartId = null;
                    }

                    database.exerciseDao().insertExercise(ex);
                }
            });
        }
    };
}
