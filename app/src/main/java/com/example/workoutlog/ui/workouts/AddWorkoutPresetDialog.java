// app/src/main/java/com/example/workoutlog/ui/workouts/AddWorkoutPresetDialog.java
package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment; // <-- ADD THIS

import com.example.workoutlog.databinding.DialogAddWorkoutPresetBinding;
import com.example.workoutlog.ui.exercises.BaseDialog;
import java.util.Objects;
import java.util.regex.Pattern;

public class AddWorkoutPresetDialog extends BaseDialog<DialogAddWorkoutPresetBinding, WorkoutPresetsViewModel> {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddWorkoutPresetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected Class<WorkoutPresetsViewModel> getViewModelClass() {
        return WorkoutPresetsViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();

        // Observe the new preset ID
        viewModel.getNewlyCreatedPresetId().observe(getViewLifecycleOwner(), newId -> {
            if (newId != null) {
                // When we get the new ID, navigate to the detail screen

                // 1. Get the PARENT fragment (WorkoutPresetsFragment)
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null) {
                    // 2. Create the navigation action
                    WorkoutPresetsFragmentDirections.ActionNavigationWorkoutsToWorkoutPresetDetailFragment action =
                            WorkoutPresetsFragmentDirections.actionNavigationWorkoutsToWorkoutPresetDetailFragment(newId);

                    // 3. Navigate using the parent's NavController
                    NavHostFragment.findNavController(parentFragment).navigate(action);
                }

                // Reset the event and dismiss
                viewModel.doneNavigating();
                dismiss();
            }
        });
    }
    @Override
    protected void setupUI() {
        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonSave.setOnClickListener(v -> {
            if (validateInput()) {
                String name = Objects.requireNonNull(binding.editPresetName.getText()).toString().trim();

                // 2. This now triggers the LiveData observer we just added
                viewModel.addWorkoutPreset(name);

                // 3. DO NOT dismiss here anymore. The observer will dismiss.
                // dismiss();
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