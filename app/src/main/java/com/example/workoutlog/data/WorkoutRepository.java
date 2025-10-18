package com.example.workoutlog.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.dao.WorkoutPresetDao;
import com.example.workoutlog.data.dao.WorkoutPresetExerciseDao;
import com.example.workoutlog.data.dao.WorkoutPresetFullDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import com.example.workoutlog.data.entities.WorkoutPresetExerciseEntity;
import com.example.workoutlog.data.models.WorkoutPresetExerciseWithName;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRepository {

    private final MusclePartDao musclePartDao;
    private final ExerciseDao exerciseDao;
    private final WorkoutPresetDao workoutPresetDao; // ADDED
    private final WorkoutPresetExerciseDao workoutPresetExerciseDao; // ADDED
    private final WorkoutPresetFullDao workoutPresetFullDao; // ADDED

    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public WorkoutRepository(
            MusclePartDao musclePartDao,
            ExerciseDao exerciseDao,
            WorkoutPresetDao workoutPresetDao, // ADDED
            WorkoutPresetExerciseDao workoutPresetExerciseDao, // ADDED
            WorkoutPresetFullDao workoutPresetFullDao // ADDED
    ) {
        this.musclePartDao = musclePartDao;
        this.exerciseDao = exerciseDao;
        this.workoutPresetDao = workoutPresetDao; // ADDED
        this.workoutPresetExerciseDao = workoutPresetExerciseDao; // ADDED
        this.workoutPresetFullDao = workoutPresetFullDao; // ADDED
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

    // NEW WORKOUT PRESET METHODS
    public LiveData<List<WorkoutPresetEntity>> getAllWorkoutPresets() {
        return workoutPresetDao.getAllPresets();
    }

    // ADD THIS
    public LiveData<WorkoutPresetEntity> getPresetById(long presetId) {
        return workoutPresetDao.getPresetById(presetId);
    }

    public void addWorkoutPreset(String name, MutableLiveData<Long> newIdCallback) {
       databaseWriteExecutor.execute(() -> {
            WorkoutPresetEntity preset = new WorkoutPresetEntity();
            preset.name = name;
            long newId = workoutPresetDao.addWorkoutPreset(preset);
            newIdCallback.postValue(newId);
        });
    }

    public void updateWorkoutPreset(WorkoutPresetEntity preset) {
        databaseWriteExecutor.execute(() -> workoutPresetDao.updatePreset(preset));
    }

    public void deleteWorkoutPreset(WorkoutPresetEntity preset) {
        databaseWriteExecutor.execute(() -> workoutPresetDao.deletePreset(preset));
    }

    public LiveData<WorkoutPresetEntity> getWorkoutPresetById(long presetId) {
        return workoutPresetDao.getPresetById(presetId);
    }

    // ADD THIS
    public LiveData<List<WorkoutPresetExerciseWithName>> getExercisesForPreset(long presetId) {
        return workoutPresetFullDao.getExercisesForPreset(presetId);
    }

    // ADD THIS
    public void addExerciseToPreset(long presetId, long exerciseId, int sets, int repetitions) {
        databaseWriteExecutor.execute(() -> {
            WorkoutPresetExerciseEntity entity = new WorkoutPresetExerciseEntity();
            entity.presetId = presetId;
            entity.exerciseId = exerciseId;
            entity.sets = sets;
            entity.repetitions = repetitions;
            workoutPresetExerciseDao.insertExerciseIntoPreset(entity);
        });
    }

    // ADD THIS (for later)
    public void deleteExerciseFromPreset(WorkoutPresetExerciseWithName exercise) {
        databaseWriteExecutor.execute(() -> {
            // We need to map the 'WithName' model back to the base 'Entity' to delete it
            WorkoutPresetExerciseEntity entity = new WorkoutPresetExerciseEntity();
            entity.id = exercise.id;
            entity.presetId = exercise.presetId;
            entity.exerciseId = exercise.exerciseId;
            entity.sets = exercise.sets;
            entity.repetitions = exercise.repetitions;
            workoutPresetExerciseDao.deleteExerciseFromPreset(entity);
        });
    }
}