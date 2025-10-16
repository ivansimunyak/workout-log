package com.example.workoutlog.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "workout_preset_exercises",
        foreignKeys = {
                @ForeignKey(
                        entity = WorkoutPresetEntity.class,
                        parentColumns = "id",
                        childColumns = "presetId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = ExerciseEntity.class,
                        parentColumns = "id",
                        childColumns = "exerciseId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index("presetId"),
                @Index("exerciseId")
        }
)
public class WorkoutPresetExerciseEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "presetId")
    public long presetId;

    @ColumnInfo(name = "exerciseId")
    public long exerciseId;

    @ColumnInfo(name = "sets")
    public int sets;

    @ColumnInfo(name = "repetitions")
    public int repetitions;
}
