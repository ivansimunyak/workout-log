package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.workoutlog.R;
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

        // This observes the LiveData for the new ID from the ViewModel.
        viewModel.getNewlyCreatedPresetId().observe(getViewLifecycleOwner(), newId -> {
            if (newId != null) {
                // Find the parent fragment to get its NavController.
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null) {
                    // Navigate using the Bundle, just like in the main fragment.
                    Bundle bundle = new Bundle();
                    bundle.putLong("presetId", newId);
                    NavHostFragment.findNavController(parentFragment).navigate(R.id.action_workouts_to_detail, bundle);
                }

                // Reset the event to prevent re-navigation, then dismiss the dialog.
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
                // This call triggers the repository to save the data and then triggers the observer above.
                viewModel.addWorkoutPreset(name);
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

        // Clear the error if validation passes
        binding.layoutPresetName.setError(null);
        return true;
    }
}

