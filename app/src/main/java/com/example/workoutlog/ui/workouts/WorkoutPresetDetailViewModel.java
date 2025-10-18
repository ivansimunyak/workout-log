// app/src/main/java/com/example/workoutlog/ui/workouts/WorkoutPresetDetailViewModel.java
package com.example.workoutlog.ui.workouts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import com.example.workoutlog.data.models.WorkoutPresetExerciseWithName;
import java.util.List;

public class WorkoutPresetDetailViewModel extends ViewModel {

    private final WorkoutRepository repository;

    // This holds the ID of the preset we are currently looking at
    private final MutableLiveData<Long> presetId = new MutableLiveData<>();

    // These are the public LiveData objects the Fragment will observe
    public LiveData<WorkoutPresetEntity> presetDetails;
    public LiveData<List<WorkoutPresetExerciseWithName>> presetExercises;

    public WorkoutPresetDetailViewModel(WorkoutRepository repository) {
        this.repository = repository;

        // When presetId changes, switchMap triggers and fetches the new data
        presetDetails = Transformations.switchMap(presetId, repository::getPresetById
        );

        presetExercises = Transformations.switchMap(presetId, repository::getExercisesForPreset
        );
    }

    /**
     * This is called by the Fragment to tell the ViewModel which preset to load.
     */
    public void loadPreset(long id) {
        presetId.setValue(id);
    }

    /**
     * Adds a new exercise to the currently loaded preset.
     */
    public void addExerciseToPreset(long exerciseId, int sets, int repetitions) {
        Long currentPresetId = presetId.getValue();
        if (currentPresetId != null) {
            repository.addExerciseToPreset(currentPresetId, exerciseId, sets, repetitions);
        }
    }
}