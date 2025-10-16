package com.example.workoutlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.workoutlog.data.entities.WorkoutPresetEntity;

import java.util.List;

@Dao
public interface WorkoutPresetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPreset(WorkoutPresetEntity preset);

    @Update
    void updatePreset(WorkoutPresetEntity preset);

    @Delete
    void deletePreset(WorkoutPresetEntity preset);

    @Query("SELECT * FROM workout_presets ORDER BY name")
    LiveData<List<WorkoutPresetEntity>> getAllPresets();

    @Query("SELECT * FROM workout_presets WHERE id = :presetId")
    LiveData<WorkoutPresetEntity> getPresetById(long presetId);
}
