// app/src/main/java/com/example/workoutlog/ui/exercises/AddMusclePartDialog.java
package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.workoutlog.databinding.DialogAddMusclePartBinding;
import java.util.regex.Pattern;

// 1. Update class signature
public class AddMusclePartDialog extends BaseDialog<DialogAddMusclePartBinding, ExercisesViewModel> {

    // 'viewModel' is inherited

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddMusclePartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 2. Implement abstract method
    @Override
    protected Class<ExercisesViewModel> getViewModelClass() {
        return ExercisesViewModel.class;
    }

    // 3. Add onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    @Override
    protected void setupUI() {
        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonAdd.setOnClickListener(v -> {
            if (validateInput()) {
                String name = binding.editMusclePartName.getText().toString().trim();
                // 4. Use inherited 'viewModel'
                viewModel.addMusclePart(name);
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