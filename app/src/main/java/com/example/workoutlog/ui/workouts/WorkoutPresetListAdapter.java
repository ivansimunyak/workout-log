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
import com.example.workoutlog.data.entities.WorkoutPresetEntity;

public class WorkoutPresetListAdapter extends ListAdapter<WorkoutPresetEntity, WorkoutPresetListAdapter.ViewHolder> {

    // NEW: An interface to allow the Fragment to listen to click events.
    // This is the standard, clean way to handle clicks in a RecyclerView.
    public interface OnItemClickListener {
        void onItemClick(WorkoutPresetEntity preset);
        void onEditClick(WorkoutPresetEntity preset);
        void onDeleteClick(WorkoutPresetEntity preset);
    }

    private final OnItemClickListener listener;

    public WorkoutPresetListAdapter(@NonNull OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_preset, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutPresetEntity current = getItem(position);
        holder.bind(current);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final ImageButton buttonEdit;
        private final ImageButton buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_workout_preset_name);
            buttonEdit = itemView.findViewById(R.id.button_edit_preset);
            buttonDelete = itemView.findViewById(R.id.button_delete_preset);

            // FIX: Set up click listeners that call the interface methods.
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            buttonEdit.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(getItem(position));
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(getItem(position));
                }
            });
        }

        public void bind(WorkoutPresetEntity preset) {
            textViewName.setText(preset.name);
        }
    }

    private static final DiffUtil.ItemCallback<WorkoutPresetEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<WorkoutPresetEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull WorkoutPresetEntity oldItem, @NonNull WorkoutPresetEntity newItem) {
            return oldItem.id == newItem.id;
        }
        @Override
        public boolean areContentsTheSame(@NonNull WorkoutPresetEntity oldItem, @NonNull WorkoutPresetEntity newItem) {
            return oldItem.name.equals(newItem.name);
        }
    };
}

