package com.example.workoutlog.data.models;

import androidx.room.ColumnInfo;

public class WorkoutPresetExerciseWithName {
    public long id;
    public long presetId;
    public long exerciseId;
    public int sets;
    public int repetitions;

    @ColumnInfo(name = "exerciseName")
    public String exerciseName;

    public long primaryPartId;
    public Long secondaryPartId;
}
