package com.example.workoutlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.workoutlog.data.models.WorkoutPresetExerciseWithName;

import java.util.List;

@Dao
public interface WorkoutPresetFullDao {

    @Query(
            "SELECT wpe.*, e.name AS exerciseName, e.primaryPartId, e.secondaryPartId " +
                    "FROM workout_preset_exercises wpe " +
                    "JOIN exercises e ON wpe.exerciseId = e.id " +
                    "WHERE wpe.presetId = :presetId " +
                    "ORDER BY wpe.id"
    )
    LiveData<List<WorkoutPresetExerciseWithName>> getPresetWithExerciseDetails(long presetId);
}
