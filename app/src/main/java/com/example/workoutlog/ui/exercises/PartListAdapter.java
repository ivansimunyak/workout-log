package com.example.workoutlog.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workoutlog.R;
import com.example.workoutlog.data.entities.MusclePartEntity;

public class PartListAdapter
        extends ListAdapter<MusclePartEntity, PartListAdapter.ViewHolder> {

    public interface OnPartClick { void onClick(MusclePartEntity part); }

    private final OnPartClick listener;

    public PartListAdapter(OnPartClick listener) {
        super(new DiffUtil.ItemCallback<MusclePartEntity>() {
            @Override public boolean areItemsTheSame(@NonNull MusclePartEntity a, @NonNull MusclePartEntity b) {
                return a.id == b.id;
            }
            @Override public boolean areContentsTheSame(@NonNull MusclePartEntity a, @NonNull MusclePartEntity b) {
                return a.name.equals(b.name);
            }
        });
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_muscle_part, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        MusclePartEntity part = getItem(pos);
        h.name.setText(part.name);
        h.itemView.setOnClickListener(v -> listener.onClick(part));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.partName);
        }
    }
}
