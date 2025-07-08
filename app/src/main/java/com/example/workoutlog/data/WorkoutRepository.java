package com.example.workoutlog.data;

import androidx.lifecycle.LiveData;

import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRepository {

    private final MusclePartDao musclePartDao;
    private final ExerciseDao exerciseDao;

    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

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

    public void addMusclePart(String name) {
        MusclePartEntity part = new MusclePartEntity();
        part.name = name;
        databaseWriteExecutor.execute(() -> {
            musclePartDao.insertPart(part);
        });
    }

    public void addExercise(String name, long primaryPartId, Long secondaryPartId) {
        ExerciseEntity exercise = new ExerciseEntity();
        exercise.name = name;
        exercise.primaryPartId = primaryPartId;
        exercise.secondaryPartId = secondaryPartId;

        databaseWriteExecutor.execute(() -> {
            exerciseDao.insertExercise(exercise);
        });
    }

    public void updateMusclePart(MusclePartEntity part) {
        databaseWriteExecutor.execute(() -> musclePartDao.updatePart(part));
    }

    public void updateExercise(ExerciseEntity exercise) {
        databaseWriteExecutor.execute(() -> exerciseDao.updateExercise(exercise));
    }

    public void deleteMusclePart(MusclePartEntity part) {
        databaseWriteExecutor.execute(() -> musclePartDao.deletePart(part));
    }

    public void deleteExercise(ExerciseEntity exercise) {
        databaseWriteExecutor.execute(() -> exerciseDao.deleteExercise(exercise));
    }

    public LiveData<ExerciseEntity> getExerciseById(long exerciseId) {
        return exerciseDao.getExerciseById(exerciseId);
    }

    public LiveData<MusclePartEntity> getMusclePartById(long musclePartId) {
        return musclePartDao.getMusclePartById(musclePartId);
    }
}