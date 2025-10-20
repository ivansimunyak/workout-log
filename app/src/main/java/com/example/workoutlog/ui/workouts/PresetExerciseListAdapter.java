package com.example.workoutlog.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workoutlog.R;
import com.example.workoutlog.data.models.WorkoutPresetExerciseWithName;

public class PresetExerciseListAdapter extends ListAdapter<WorkoutPresetExerciseWithName, PresetExerciseListAdapter.ViewHolder> {

    public PresetExerciseListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPresetExerciseWithName item = getItem(position);
        holder.bind(item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textExerciseName;
        private final TextView textSetsReps;
        private final ImageButton buttonRemove;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textExerciseName = itemView.findViewById(R.id.text_exercise_name);
            textSetsReps = itemView.findViewById(R.id.text_sets_reps);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }

        void bind(WorkoutPresetExerciseWithName item) {
            textExerciseName.setText(item.exerciseName);
            String setsReps = item.sets + " sets x " + item.repetitions + " reps";
            textSetsReps.setText(setsReps);

            // TODO: Implement delete functionality for an exercise from a preset.
            buttonRemove.setOnClickListener(v -> {
                System.out.println("TODO: Implement remove exercise from preset");
            });
        }
    }

    private static final DiffUtil.ItemCallback<WorkoutPresetExerciseWithName> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WorkoutPresetExerciseWithName>() {
                @Override
                public boolean areItemsTheSame(@NonNull WorkoutPresetExerciseWithName oldItem, @NonNull WorkoutPresetExerciseWithName newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull WorkoutPresetExerciseWithName oldItem, @NonNull WorkoutPresetExerciseWithName newItem) {
                    return oldItem.exerciseName.equals(newItem.exerciseName) &&
                            oldItem.sets == newItem.sets &&
                            oldItem.repetitions == newItem.repetitions;
                }
            };
}
