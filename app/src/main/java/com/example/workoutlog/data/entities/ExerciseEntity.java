package com.example.workoutlog.data.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "exercises",
        foreignKeys = {
                @ForeignKey(
                        entity = MusclePartEntity.class,
                        parentColumns = "id",
                        childColumns = "primaryPartId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = MusclePartEntity.class,
                        parentColumns = "id",
                        childColumns = "secondaryPartId",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index("primaryPartId"),
                @Index("secondaryPartId")
        }
)
public class ExerciseEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "primaryPartId")
    public long primaryPartId;

    @ColumnInfo(name = "secondaryPartId")
    public Long secondaryPartId;
}
