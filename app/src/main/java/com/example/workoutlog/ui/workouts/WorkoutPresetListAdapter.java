package com.example.workoutlog.ui.workouts;

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
import com.example.workoutlog.data.entities.WorkoutPresetEntity;

public class WorkoutPresetListAdapter
        extends ListAdapter<WorkoutPresetEntity, WorkoutPresetListAdapter.ViewHolder> {

    public interface OnPresetClickListener {
        void onPresetClick(WorkoutPresetEntity preset);
        void onRemovePresetClick(WorkoutPresetEntity preset);
        void onEditPresetClick(WorkoutPresetEntity preset);
    }

    private final OnPresetClickListener listener;

    public WorkoutPresetListAdapter(@NonNull OnPresetClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_preset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPresetEntity currentPreset = getItem(position);
        holder.bind(currentPreset, listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageButton removeButton;
        private final ImageButton editButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.presetName);
            editButton = itemView.findViewById(R.id.button_edit_preset);
            removeButton = itemView.findViewById(R.id.button_remove_preset);
        }

        public void bind(final WorkoutPresetEntity preset, final OnPresetClickListener listener) {
            name.setText(preset.name);
            itemView.setOnClickListener(v -> listener.onPresetClick(preset));
            editButton.setOnClickListener(v -> listener.onEditPresetClick(preset));
            removeButton.setOnClickListener(v -> listener.onRemovePresetClick(preset));
        }
    }

    private static final DiffUtil.ItemCallback<WorkoutPresetEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull WorkoutPresetEntity a, @NonNull WorkoutPresetEntity b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull WorkoutPresetEntity a, @NonNull WorkoutPresetEntity b) {
                    return a.name.equals(b.name);
                }
            };
}