package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.example.workoutlog.databinding.DialogAddWorkoutPresetBinding;
import com.example.workoutlog.ui.exercises.BaseDialog;
import java.util.Objects;
import java.util.regex.Pattern;

public class AddWorkoutPresetDialog extends BaseDialog<DialogAddWorkoutPresetBinding> {

    private WorkoutPresetsViewModel presetsViewModel;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddWorkoutPresetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Overrides BaseDialog's VM injection to get the correct ViewModel
        super.onViewCreated(view, savedInstanceState);
        presetsViewModel = new ViewModelProvider(requireActivity()).get(WorkoutPresetsViewModel.class);
    }

    @Override
    protected void setupUI() {
        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonSave.setOnClickListener(v -> {
            if (validateInput()) {
                String name = Objects.requireNonNull(binding.editPresetName.getText()).toString().trim();
                presetsViewModel.addWorkoutPreset(name);
                dismiss();
            }
        });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(binding.editPresetName.getText())) {
            binding.layoutPresetName.setError("Name cannot be empty");
            return false;
        }

        String input = binding.editPresetName.getText().toString();

        if (Pattern.compile("[0-9]").matcher(input).find()) {
            binding.layoutPresetName.setError("Name cannot contain numbers");
            return false;
        }

        return true;
    }
}