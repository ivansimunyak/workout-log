package com.example.workoutlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.List;

@Dao
public interface MusclePartDao {
    @Query("SELECT * FROM muscle_parts ORDER BY name")
    LiveData<List<MusclePartEntity>> getAllParts();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertPart(MusclePartEntity part);
}