package com.example.workoutlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.workoutlog.data.entities.WorkoutPresetExerciseEntity;

import java.util.List;

@Dao
public interface WorkoutPresetExerciseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPresetExercise(WorkoutPresetExerciseEntity presetExercise);

    @Update
    void updatePresetExercise(WorkoutPresetExerciseEntity presetExercise);

    @Delete
    void deletePresetExercise(WorkoutPresetExerciseEntity presetExercise);

    @Query("SELECT * FROM workout_preset_exercises WHERE presetId = :presetId")
    LiveData<List<WorkoutPresetExerciseEntity>> getExercisesForPreset(long presetId);

    @Query("DELETE FROM workout_preset_exercises WHERE presetId = :presetId")
    void deleteExercisesForPreset(long presetId);

    @Insert
    void insertExerciseIntoPreset(WorkoutPresetExerciseEntity exercise);

    // ADD THIS METHOD (for later)
    @Delete
    void deleteExerciseFromPreset(WorkoutPresetExerciseEntity exercise);
}
