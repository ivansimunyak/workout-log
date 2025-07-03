package com.example.workoutlog.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

@Database(
        entities = {
                MusclePartEntity.class,
                ExerciseEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MusclePartDao musclePartDao();
    public abstract ExerciseDao exerciseDao();

    // Keep the Seed record for structure
    public record Seed(String name, String primary, String secondary) {}
}