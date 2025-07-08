package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.DialogAddMusclePartBinding;
import java.util.regex.Pattern;

public class EditMusclePartDialog extends BaseDialog<DialogAddMusclePartBinding> {

    private static final String ARG_PART_ID = "partId";
    private static final String ARG_PART_NAME = "partName";
    private MusclePartEntity musclePartToUpdate;

    public static EditMusclePartDialog newInstance(MusclePartEntity part) {
        EditMusclePartDialog fragment = new EditMusclePartDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_PART_ID, part.id);
        args.putString(ARG_PART_NAME, part.name);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddMusclePartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void setupUI() {
        if (getArguments() != null) {
            long partId = getArguments().getLong(ARG_PART_ID);
            String partName = getArguments().getString(ARG_PART_NAME);

            musclePartToUpdate = new MusclePartEntity();
            musclePartToUpdate.id = partId;
            musclePartToUpdate.name = partName;

            binding.editMusclePartName.setText(partName);
        }

        binding.buttonAdd.setText("Update");
        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonAdd.setOnClickListener(v -> {
            if (validateInput()) {
                String updatedName = binding.editMusclePartName.getText().toString().trim();
                musclePartToUpdate.name = updatedName;
                viewModel.updateMusclePart(musclePartToUpdate);
                dismiss();
            }
        });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(binding.editMusclePartName.getText())) {
            binding.layoutMusclePartName.setError("Name cannot be empty");
            return false;
        };

        String input = binding.editMusclePartName.getText().toString();

        if (Pattern.compile("[0-9]").matcher(input).find()) {
            binding.layoutMusclePartName.setError("Name cannot contain numbers");
            return false;
        }

        return true;
    }
}