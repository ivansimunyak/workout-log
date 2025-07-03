package com.example.workoutlog.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.ui.exercises.ExercisesViewModel;
import com.example.workoutlog.ui.home.HomeViewModel; // Import other ViewModels

import java.lang.reflect.InvocationTargetException;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WorkoutRepository repository;

    public ViewModelFactory(WorkoutRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // Check which ViewModel is requested and instantiate it with the repository
        if (modelClass.isAssignableFrom(ExercisesViewModel.class)) {
            try {
                // Cast is safe due to isAssignableFrom check
                return (T) ExercisesViewModel.class.getConstructor(WorkoutRepository.class).newInstance(repository);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            // Assuming HomeViewModel also needs the repository (or other dependencies)
            // Adjust constructor call as needed for HomeViewModel
            try {
                // Example: If HomeViewModel only needs repository
                return (T) HomeViewModel.class.getConstructor(WorkoutRepository.class).newInstance(repository);
                // Example: If HomeViewModel has no dependencies yet
                // return (T) HomeViewModel.class.getConstructor().newInstance();
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        // Add more 'else if' blocks for other ViewModels

        // If the ViewModel class is unknown to this factory
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}