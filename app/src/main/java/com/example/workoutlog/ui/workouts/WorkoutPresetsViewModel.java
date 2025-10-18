// app/src/main/java/com/example/workoutlog/ui/workouts/WorkoutPresetsViewModel.java
package com.example.workoutlog.ui.workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // <-- ADD THIS
import androidx.lifecycle.ViewModel;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import java.util.List;

public class WorkoutPresetsViewModel extends ViewModel {

    private final WorkoutRepository repository;
    public final LiveData<List<WorkoutPresetEntity>> allPresets;

    // 1. ADD THESE TWO LINES
    private final MutableLiveData<Long> _newlyCreatedPresetId = new MutableLiveData<>();
    public LiveData<Long> getNewlyCreatedPresetId() { return _newlyCreatedPresetId; }


    public WorkoutPresetsViewModel(WorkoutRepository repository) {
        this.repository = repository;
        this.allPresets = repository.getAllWorkoutPresets();
    }

    // 2. UPDATE THIS METHOD
    public void addWorkoutPreset(String name) {
        // Pass the callback LiveData to the repository
        repository.addWorkoutPreset(name, _newlyCreatedPresetId);
    }

    // 3. ADD THIS (to reset the event)
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