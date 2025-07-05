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
        if (modelClass.isAssignableFrom(ExercisesViewModel.class)) {
            try {
                return (T) ExercisesViewModel.class.getConstructor(WorkoutRepository.class).newInstance(repository);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            try {
                return (T) HomeViewModel.class.getConstructor(WorkoutRepository.class).newInstance(repository);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}