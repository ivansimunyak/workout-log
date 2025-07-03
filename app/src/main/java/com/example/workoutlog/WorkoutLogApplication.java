package com.example.workoutlog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.workoutlog.data.AppDatabase;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Remove @HiltAndroidApp if it was present
public class WorkoutLogApplication extends Application {

    // Singletons held by the Application instance
    private AppDatabase database;
    private WorkoutRepository repository;
    // Executor for pre-population
    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Database and Repository here
        // Pass the callback directly during build
        database = Room.databaseBuilder(
                        getApplicationContext(),
                        AppDatabase.class,
                        "cybergym.db")
                .addCallback(createPrepopulationCallback()) // Add callback here
                .build();

        // Create repository instance using DAOs from the database
        repository = new WorkoutRepository(database.musclePartDao(), database.exerciseDao());
    }

    // Public accessors for dependencies
    public AppDatabase getDatabase() {
        return database;
    }

    public WorkoutRepository getWorkoutRepository() {
        return repository;
    }

    // Database Pre-population Callback (similar to before, but created here)
    private RoomDatabase.Callback createPrepopulationCallback() {
        return new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                databaseWriteExecutor.execute(() -> {
                    // Get DAOs *after* database is created and open
                    // Need to use the instance variable `database` here
                    MusclePartDao partDao = database.musclePartDao();
                    ExerciseDao exDao = database.exerciseDao();

                    // --- Pre-population logic remains the same ---
                    String[] partNames = {
                            "Chest", "Back", "Shoulders",
                            "Biceps", "Triceps", "Quads",
                            "Hamstrings", "Calves", "Core", "Glutes"
                    };
                    Map<String, Long> partIds = new HashMap<>();
                    for (String partName : partNames) {
                        MusclePartEntity part = new MusclePartEntity();
                        part.name = partName;
                        long id = partDao.insertPart(part);
                        partIds.put(partName, id);
                    }

                    List<AppDatabase.Seed> seeds = Arrays.asList(
                            new AppDatabase.Seed("Barbell Squat", "Quads", "Glutes"),
                            new AppDatabase.Seed("Bench Press", "Chest", "Triceps"),
                            new AppDatabase.Seed("Deadlift", "Back", "Hamstrings"),
                            // ... other seeds ...
                            new AppDatabase.Seed("Plank", "Core", null)
                    );
                    for (AppDatabase.Seed seed : seeds) {
                        ExerciseEntity ex = new ExerciseEntity();
                        ex.name = seed.name();
                        Long primaryId = partIds.get(seed.primary());
                        ex.primaryPartId = (primaryId != null) ? primaryId : 0L;
                        if (seed.secondary() != null) {
                            ex.secondaryPartId = partIds.get(seed.secondary());
                        } else {
                            ex.secondaryPartId = null;
                        }
                        if (ex.primaryPartId != 0L) {
                            exDao.insertExercise(ex);
                        }
                    }
                    // --- End pre-population logic ---
                });
            }
        };
    }
}