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

    private final MutableLiveData<Long> presetId = new MutableLiveData<>();

    public final LiveData<WorkoutPresetEntity> presetDetails;
    public final LiveData<List<WorkoutPresetExerciseWithName>> presetExercises;

    public WorkoutPresetDetailViewModel(WorkoutRepository repository) {
        this.repository = repository;

        // When presetId changes, these Transformations will automatically fetch the new data.
        presetDetails = Transformations.switchMap(presetId, repository::getPresetById
        );

        presetExercises = Transformations.switchMap(presetId, repository::getExercisesForPreset
        );
    }

    /**
     * This is called by the Fragment to tell the ViewModel which preset to load.
     */
    public void loadPreset(long id) {
        if (id != 0 && (presetId.getValue() == null || !presetId.getValue().equals(id))) {
            presetId.setValue(id);
        }
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
