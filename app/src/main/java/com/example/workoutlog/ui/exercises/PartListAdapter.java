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
import com.example.workoutlog.data.entities.MusclePartEntity;

public class PartListAdapter
        extends ListAdapter<MusclePartEntity, PartListAdapter.ViewHolder> {

    // The contract for what the Fragment needs to handle
    public interface OnPartClickListener {
        void onPartClick(MusclePartEntity part);
        void onRemovePartClick(MusclePartEntity part);
    }

    private final OnPartClickListener listener;

    public PartListAdapter(@NonNull OnPartClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_muscle_part, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusclePartEntity currentPart = getItem(position);
        // The ViewHolder now handles all the binding logic.
        holder.bind(currentPart, listener);
    }

    // The ViewHolder is now responsible for its own views and listeners.
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageButton removeButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.partName);
            removeButton = itemView.findViewById(R.id.button_remove_part);
        }

        public void bind(final MusclePartEntity part, final OnPartClickListener listener) {
            name.setText(part.name);
            // The click listeners are set here, using the specific item for this row.
            itemView.setOnClickListener(v -> listener.onPartClick(part));
            removeButton.setOnClickListener(v -> listener.onRemovePartClick(part));
        }
    }

    private static final DiffUtil.ItemCallback<MusclePartEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull MusclePartEntity a, @NonNull MusclePartEntity b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull MusclePartEntity a, @NonNull MusclePartEntity b) {
                    return a.name.equals(b.name);
                }
            };
}