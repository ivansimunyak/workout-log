package com.example.workoutlog.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.dao.WorkoutPresetDao;
import com.example.workoutlog.data.dao.WorkoutPresetExerciseDao;
import com.example.workoutlog.data.dao.WorkoutPresetFullDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import com.example.workoutlog.data.entities.WorkoutPresetExerciseEntity;

@Database(
        entities = {
                MusclePartEntity.class,
                ExerciseEntity.class,
                WorkoutPresetEntity.class, // ADDED
                WorkoutPresetExerciseEntity.class // ADDED
        },
        version = 2, // INCREMENTED
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MusclePartDao musclePartDao();
    public abstract ExerciseDao exerciseDao();
    public abstract WorkoutPresetDao workoutPresetDao(); // ADDED
    public abstract WorkoutPresetExerciseDao workoutPresetExerciseDao(); // ADDED
    public abstract WorkoutPresetFullDao workoutPresetFullDao(); // ADDED

    public record Seed(String name, String primary, String secondary) {}
}