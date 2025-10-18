package com.example.workoutlog.data.models;

import androidx.room.ColumnInfo;

public class WorkoutPresetExerciseWithName {
    // These fields come from the 'workout_preset_exercises' table
    public long id;
    public long presetId;
    public long exerciseId;
    public int sets;
    public int repetitions;

    // This field comes from the 'exercises' table via a JOIN
    @ColumnInfo(name = "exerciseName")
    public String exerciseName;

    // This field ALSO comes from the 'exercises' table via a JOIN.
    // The error occurs because this field exists, but the query isn't providing it.
    public long primaryPartId;
}