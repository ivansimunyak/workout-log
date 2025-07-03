package com.example.workoutlog.ui.exercises;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.List;

// Removed @HiltViewModel and @Inject
public class ExercisesViewModel extends ViewModel {

    private final WorkoutRepository repository; // Make final
    public final LiveData<List<MusclePartEntity>> allParts;

    // Constructor now takes repository (called by ViewModelFactory)
    public ExercisesViewModel(WorkoutRepository repository) {
        this.repository = repository;
        this.allParts = repository.getAllParts();
    }

    // --- Methods remain the same ---
    public LiveData<List<ExerciseEntity>> getExercisesForPart(long partId) {
        return repository.getExercisesForPart(partId);
    }

    public LiveData<List<ExerciseEntity>> searchExercises(String query) {
        return repository.searchExercises(query);
    }
}