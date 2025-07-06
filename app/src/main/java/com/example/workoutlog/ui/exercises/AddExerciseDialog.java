package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.workoutlog.R;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.DialogAddExerciseBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AddExerciseDialog extends BaseDialog<DialogAddExerciseBinding> {

    private List<MusclePartEntity> muscleParts = new ArrayList<>();
    private List<String> allPartNames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    protected void setupUI() {
        setupSpinners();
        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonAddExercise.setOnClickListener(v -> {
            if (validateInput()) {
                addExercise();
            }
        });
    }

    private void setupSpinners() {
        viewModel.allParts.observe(getViewLifecycleOwner(), parts -> {
            if (parts == null) return;
            this.muscleParts = parts;
            this.allPartNames = parts.stream().map(p -> p.name).collect(Collectors.toList());

            ArrayAdapter<String> primaryAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, allPartNames);
            binding.spinnerPrimaryPart.setAdapter(primaryAdapter);
        });

        binding.spinnerPrimaryPart.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPrimary = (String) parent.getItemAtPosition(position);
            updateSecondarySpinner(selectedPrimary);
        });
    }

    private void updateSecondarySpinner(String primarySelection) {
        List<String> secondaryNames = new ArrayList<>();
        secondaryNames.add("None");
        for (String partName : allPartNames) {
            if (!partName.equals(primarySelection)) {
                secondaryNames.add(partName);
            }
        }
        ArrayAdapter<String> secondaryAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, secondaryNames);
        binding.spinnerSecondaryPart.setAdapter(secondaryAdapter);
        binding.spinnerSecondaryPart.setText("", false);
    }

    private void addExercise() {
        String name = Objects.requireNonNull(binding.editExerciseName.getText()).toString().trim();
        String primaryPartName = binding.spinnerPrimaryPart.getText().toString();
        String secondaryPartName = binding.spinnerSecondaryPart.getText().toString();

        long primaryId = muscleParts.stream().filter(p -> p.name.equals(primaryPartName)).findFirst().get().id;
        Long secondaryId = null;
        if (!secondaryPartName.isEmpty() && !secondaryPartName.equals("None")) {
            secondaryId = muscleParts.stream().filter(p -> p.name.equals(secondaryPartName)).findFirst().get().id;
        }

        viewModel.addExercise(name, primaryId, secondaryId);
        dismiss();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(binding.editExerciseName.getText())) {
            binding.layoutExerciseName.setError("Name cannot be empty.");
            return false;
        }

        String name = binding.editExerciseName.getText().toString().trim();
        String primary = binding.spinnerPrimaryPart.getText().toString().trim();

        if (Pattern.compile("[0-9]").matcher(name).find()) {
            binding.layoutExerciseName.setError("Name cannot contain numbers");
            return false;
        }

        if (primary.isEmpty()) {
            binding.layoutPrimaryPart.setError("Primary muscle is required");
            return false;
        } else if (!allPartNames.contains(primary)) {
            binding.layoutPrimaryPart.setError("Please select a valid muscle");
            return false;
        } else {
            binding.layoutPrimaryPart.setError(null);
        }

        return true;
    }
}