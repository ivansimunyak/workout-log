package com.example.workoutlog.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.workoutlog.data.entities.ExerciseEntity;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE primaryPartId = :partId ORDER BY name")
    LiveData<List<ExerciseEntity>> getExercisesForPart(long partId);

    @Query(
            "SELECT e.* FROM exercises e " +
                    "JOIN muscle_parts m ON e.primaryPartId = m.id " +
                    "WHERE e.name LIKE '%' || :q || '%' OR m.name LIKE '%' || :q || '%' " +
                    "ORDER BY e.name"
    )
    LiveData<List<ExerciseEntity>> searchExercises(String q);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertExercise(ExerciseEntity exercise);

    @Query("SELECT * FROM exercises ORDER BY name")
    LiveData<List<ExerciseEntity>> getAllExercises();
}