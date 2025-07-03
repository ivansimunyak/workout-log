package com.example.workoutlog.data;

import androidx.lifecycle.LiveData;

import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.List;

// Removed @Singleton and @Inject
public class WorkoutRepository {

    private final MusclePartDao musclePartDao;
    private final ExerciseDao exerciseDao;

    // Constructor now used by Application class
    public WorkoutRepository(MusclePartDao musclePartDao, ExerciseDao exerciseDao) {
        this.musclePartDao = musclePartDao;
        this.exerciseDao = exerciseDao;
    }

    // --- Methods remain the same ---
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
}