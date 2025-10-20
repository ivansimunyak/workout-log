package com.example.workoutlog.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.ui.exercises.ExercisesViewModel;
import com.example.workoutlog.ui.home.HomeViewModel;
import com.example.workoutlog.ui.workouts.WorkoutPresetDetailViewModel;
import com.example.workoutlog.ui.workouts.WorkoutPresetsViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WorkoutRepository repository;

    public ViewModelFactory(WorkoutRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // This simplified approach is much cleaner and safer than using reflection.
        // We directly check for each ViewModel class and create it.
        if (modelClass.isAssignableFrom(ExercisesViewModel.class)) {
            return (T) new ExercisesViewModel(repository);
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        } else if (modelClass.isAssignableFrom(WorkoutPresetsViewModel.class)) {
            return (T) new WorkoutPresetsViewModel(repository);
        } else if (modelClass.isAssignableFrom(WorkoutPresetDetailViewModel.class)) {
            return (T) new WorkoutPresetDetailViewModel(repository);
        }
        // If the ViewModel is not recognized, we throw an error.
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

