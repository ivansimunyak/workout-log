package com.example.workoutlog.ui.exercises;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.workoutlog.data.AppDatabase;
import com.example.workoutlog.data.dao.ExerciseDao;
import com.example.workoutlog.data.dao.MusclePartDao;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;
import java.util.List;

public class ExercisesViewModel extends AndroidViewModel {
    private final MusclePartDao partDao;
    private final ExerciseDao exerciseDao;

    public final LiveData<List<MusclePartEntity>> allParts;

    public ExercisesViewModel(@NonNull Application app) {
        super(app);
        AppDatabase db = AppDatabase.get(app);
        partDao     = db.musclePartDao();
        exerciseDao = db.exerciseDao();

        allParts = partDao.getAllParts();
    }

    /** Returns all exercises in that part */
    public LiveData<List<ExerciseEntity>> getExercisesForPart(long partId) {
        return exerciseDao.getExercisesForPart(partId);
    }

    /** Searches both exercise names AND part names */
    public LiveData<List<ExerciseEntity>> searchExercises(String query) {
        return exerciseDao.searchExercises(query);
    }
}
