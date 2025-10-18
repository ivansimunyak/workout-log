package com.example.workoutlog.ui.workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import java.util.List;

public class WorkoutPresetsViewModel extends ViewModel {

    private final WorkoutRepository repository;
    public final LiveData<List<WorkoutPresetEntity>> allPresets;

    public WorkoutPresetsViewModel(WorkoutRepository repository) {
        this.repository = repository;
        this.allPresets = repository.getAllWorkoutPresets();
    }

    public void addWorkoutPreset(String name) {
        repository.addWorkoutPreset(name);
    }

    public void updateWorkoutPreset(WorkoutPresetEntity preset) {
        repository.updateWorkoutPreset(preset);
    }

    public void deleteWorkoutPreset(WorkoutPresetEntity preset) {
        repository.deleteWorkoutPreset(preset);
    }
}