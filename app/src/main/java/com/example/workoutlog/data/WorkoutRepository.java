package com.example.workoutlog.data;

import androidx.lifecycle.LiveData;

import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.List;

public class WorkoutRepository {

    private final MusclePartDao musclePartDao;
    private final ExerciseDao exerciseDao;

    public WorkoutRepository(MusclePartDao musclePartDao, ExerciseDao exerciseDao) {
        this.musclePartDao = musclePartDao;
        this.exerciseDao = exerciseDao;
    }

    public LiveData<List<MusclePartEntity>> getAllParts() {
        return musclePartDao.getAllParts();
    }

    public LiveData<List<ExerciseEntity>> getExercisesForPart(long partId) {
        return exerciseDao.getExercisesForPart(partId);
    }

    public LiveData<List<ExerciseEntity>> searchExercises(String query) {
        String effectiveQuery = (query == null || query.trim().isEmpty()) ? "%" : query.trim();
        return exerciseDao.searchExercises(effectiveQuery);
    }

    public LiveData<List<ExerciseEntity>> getAllExercises() {
        return exerciseDao.getAllExercises();
    }

    // Add this method to WorkoutRepository.java
    public void addMusclePart(String name) {
        MusclePartEntity part = new MusclePartEntity();
        part.name = name;
        java.util.concurrent.ExecutorService databaseWriteExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();
        databaseWriteExecutor.execute(() -> {
            musclePartDao.insertPart(part);
        });
    }

    // Add this method
    public void addExercise(String name, long primaryPartId, Long secondaryPartId) {
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.name = name;
        exercise.primaryPartId = primaryPartId;
        exercise.secondaryPartId = secondaryPartId;

        java.util.concurrent.ExecutorService databaseWriteExecutor = java.util.concurrent.Executors.newSingleThreadExecutor();
        databaseWriteExecutor.execute(() -> {
            exerciseDao.insertExercise(exercise);
        });
    }
}