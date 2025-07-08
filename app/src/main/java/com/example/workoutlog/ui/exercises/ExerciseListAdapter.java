package com.example.workoutlog.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workoutlog.R;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExerciseListAdapter
        extends ListAdapter<ExerciseEntity, ExerciseListAdapter.ViewHolder> {

    // A clear contract for the Fragment. Note the consistent naming.
    public interface OnExerciseClickListener {
        void onExerciseClick(ExerciseEntity exercise); // For future use if you want to edit/view
        void onRemoveExerciseClick(ExerciseEntity exercise);
    }

    private final OnExerciseClickListener listener;
    private final Map<Long, String> partNameMap = new HashMap<>();

    public ExerciseListAdapter(@NonNull OnExerciseClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseEntity currentExercise = getItem(position);
        String primaryPartName = partNameMap.get(currentExercise.primaryPartId);
        // Pass everything to the ViewHolder's bind method.
        holder.bind(currentExercise, primaryPartName, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView primaryText;
        private final ImageButton removeButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textExerciseName);
            primaryText = itemView.findViewById(R.id.textPrimaryMuscle);
            removeButton = itemView.findViewById(R.id.button_remove_exercise);
        }

        public void bind(final ExerciseEntity exercise, final String primaryPartName, final OnExerciseClickListener listener) {
            nameText.setText(exercise.name);
            primaryText.setText(primaryPartName != null ? primaryPartName : "");
            itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
            removeButton.setOnClickListener(v -> listener.onRemoveExerciseClick(exercise));
        }
    }

    // --- Unchanged Methods ---
    public void setPartList(@NonNull List<MusclePartEntity> parts) {
        partNameMap.clear();
        for (MusclePartEntity p : parts) {
            partNameMap.put(p.id, p.name);
        }
        notifyItemRangeChanged(0, getItemCount(), "PAYLOAD_PART_NAME");
    }

    private static final DiffUtil.ItemCallback<ExerciseEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull ExerciseEntity a, @NonNull ExerciseEntity b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull ExerciseEntity a, @NonNull ExerciseEntity b) {
                    return Objects.equals(a.name, b.name)
                            && a.primaryPartId == b.primaryPartId
                            && Objects.equals(a.secondaryPartId, b.secondaryPartId);
                }
            };
}