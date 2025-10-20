package com.example.workoutlog.ui.workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // ADDED
import androidx.lifecycle.ViewModel;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import java.util.List;

public class WorkoutPresetsViewModel extends ViewModel {

    private final WorkoutRepository repository;
    public final LiveData<List<WorkoutPresetEntity>> allPresets;

    /**
     * NEW: A LiveData to hold the ID of a newly created preset.
     * This is a single-event value. The Dialog will observe this,
     * navigate once, and then we'll clear it.
     */
    private final MutableLiveData<Long> _newlyCreatedPresetId = new MutableLiveData<>();
    public LiveData<Long> getNewlyCreatedPresetId() { return _newlyCreatedPresetId; }

    public WorkoutPresetsViewModel(WorkoutRepository repository) {
        this.repository = repository;
        this.allPresets = repository.getAllWorkoutPresets();
    }

    /**
     * FIX: This now calls the repository method that includes the callback,
     * which will trigger the _newlyCreatedPresetId LiveData.
     */
    public void addWorkoutPreset(String name) {
        repository.addWorkoutPreset(name, _newlyCreatedPresetId);
    }

    /**
     * NEW: A method to signal that navigation is complete, preventing
     * multiple navigations if the screen is rotated.
     */
    public void doneNavigating() {
        _newlyCreatedPresetId.setValue(null);
    }

    public void updateWorkoutPreset(WorkoutPresetEntity preset) {
        repository.updateWorkoutPreset(preset);
    }

    public void deleteWorkoutPreset(WorkoutPresetEntity preset) {
        repository.deleteWorkoutPreset(preset);
    }
}
