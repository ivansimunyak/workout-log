package com.example.workoutlog.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
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

    private static final String PAYLOAD_PART_NAME = "PAYLOAD_PART_NAME";

    /** Maps primaryPartId → partName; must be set by the host (Fragment) */
    private final Map<Long, String> partNameMap = new HashMap<>();

    public ExerciseListAdapter() {
        super(DIFF_CALLBACK);
    }

    /**
     * Supply the current list of muscle parts so we can resolve IDs to names.
     * Call this from your Fragment whenever your parts LiveData emits.
     */
    public void setPartList(@NonNull List<MusclePartEntity> parts) {
        partNameMap.clear();
        for (MusclePartEntity p : parts) {
            partNameMap.put(p.id, p.name);
        }
        // Only rebind the part-name field, not the entire row
        notifyItemRangeChanged(0, getItemCount(), PAYLOAD_PART_NAME);
    }

    // Full-bind: name + part
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseEntity ex = getItem(position);
        holder.nameText.setText(ex.name);
        // Lookup the human-readable name
        String partName = partNameMap.get(ex.primaryPartId);
        holder.primaryText.setText(partName != null ? partName : "");
    }

    // Partial-bind: called when we pass a payload
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // No payload → full bind
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // PAYLOAD_PART_NAME → only update the part-name TextView
            ExerciseEntity ex = getItem(position);
            String partName = partNameMap.get(ex.primaryPartId);
            holder.primaryText.setText(partName != null ? partName : "");
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    private static final DiffUtil.ItemCallback<ExerciseEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ExerciseEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull ExerciseEntity a,
                                               @NonNull ExerciseEntity b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull ExerciseEntity a,
                                                  @NonNull ExerciseEntity b) {
                    return Objects.equals(a.name, b.name)
                            && a.primaryPartId == b.primaryPartId
                            && Objects.equals(a.secondaryPartId, b.secondaryPartId);
                }
            };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameText;
        public final TextView primaryText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText    = itemView.findViewById(R.id.textExerciseName);
            primaryText = itemView.findViewById(R.id.textPrimaryMuscle);
        }
    }
}
