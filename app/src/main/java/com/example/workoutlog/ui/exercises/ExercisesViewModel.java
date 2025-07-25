package com.example.workoutlog.ui.exercises;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.List;

public class ExercisesViewModel extends ViewModel {

    private final WorkoutRepository repository;
    public final LiveData<List<MusclePartEntity>> allParts;

    public ExercisesViewModel(WorkoutRepository repository) {
        this.repository = repository;
        this.allParts = repository.getAllParts();
    }

    public LiveData<List<ExerciseEntity>> getExercisesForPart(long partId) {
        return repository.getExercisesForPart(partId);
    }

    public LiveData<List<ExerciseEntity>> searchExercises(String query) {
        return repository.searchExercises(query);
    }
    public void addMusclePart(String name) {
        repository.addMusclePart(name);
    }

    public void addExercise(String name, Long primaryPartId, Long secondaryPartId) {
        repository.addExercise(name, primaryPartId, secondaryPartId);
    }

    public void updateMusclePart(MusclePartEntity part) {
        repository.updateMusclePart(part);
    }

    public void updateExercise(ExerciseEntity exercise) {
        repository.updateExercise(exercise);
    }

    public void deleteMusclePart(MusclePartEntity part){
        repository.deleteMusclePart(part);
    }

    public void deleteExercise(ExerciseEntity exercise){
        repository.deleteExercise(exercise);
    }

    public LiveData<ExerciseEntity> getExerciseById(long exerciseId) {
        return repository.getExerciseById(exerciseId);
    }

    public LiveData<MusclePartEntity> getMusclePartById(long musclePartId) {
        return repository.getMusclePartById(musclePartId);
    }
}