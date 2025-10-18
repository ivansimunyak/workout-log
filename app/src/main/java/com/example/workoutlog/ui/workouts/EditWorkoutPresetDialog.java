// app/src/main/java/com/example/workoutlog/ui/workouts/EditWorkoutPresetDialog.java
package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
// 1. Remove ViewModelProvider import
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import com.example.workoutlog.databinding.DialogAddWorkoutPresetBinding;
import com.example.workoutlog.ui.exercises.BaseDialog;
import java.util.Objects;
import java.util.regex.Pattern;

// 2. Update class signature
public class EditWorkoutPresetDialog extends BaseDialog<DialogAddWorkoutPresetBinding, WorkoutPresetsViewModel> {

    private static final String ARG_PRESET_ID = "presetId";
    private static final String ARG_PRESET_NAME = "presetName";
    private WorkoutPresetEntity presetToUpdate;
    // 3. Remove local 'presetsViewModel', use inherited 'viewModel'
    // private WorkoutPresetsViewModel presetsViewModel;

    public static EditWorkoutPresetDialog newInstance(WorkoutPresetEntity preset) {
        EditWorkoutPresetDialog fragment = new EditWorkoutPresetDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_PRESET_ID, preset.id);
        args.putString(ARG_PRESET_NAME, preset.name);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddWorkoutPresetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 4. Implement abstract method
    @Override
    protected Class<WorkoutPresetsViewModel> getViewModelClass() {
        return WorkoutPresetsViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // 'viewModel' is initialized here
        // 5. Remove local VM initialization
        // presetsViewModel = new ViewModelProvider(requireActivity()).get(WorkoutPresetsViewModel.class);
        setupUI();
    }

    @Override
    protected void setupUI() {
        if (getArguments() != null) {
            long presetId = getArguments().getLong(ARG_PRESET_ID);
            String presetName = getArguments().getString(ARG_PRESET_NAME);

            presetToUpdate = new WorkoutPresetEntity();
            presetToUpdate.id = presetId;
            presetToUpdate.name = presetName;

            binding.editPresetName.setText(presetName);
            binding.dialogTitle.setText("Edit Workout Preset");
        }

        binding.buttonSave.setText("Update");
        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonSave.setOnClickListener(v -> {
            if (validateInput()) {
                String updatedName = Objects.requireNonNull(binding.editPresetName.getText()).toString().trim();
                presetToUpdate.name = updatedName;
                // 6. Use inherited 'viewModel'
                viewModel.updateWorkoutPreset(presetToUpdate);
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