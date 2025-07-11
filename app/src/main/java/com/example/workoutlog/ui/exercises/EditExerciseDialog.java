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
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.DialogAddExerciseBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EditExerciseDialog extends BaseDialog<DialogAddExerciseBinding> {

    private static final String ARG_EXERCISE_ID = "EXERCISE_ID";

    private ExerciseEntity exercise;
    private List<MusclePartEntity> muscleParts = new ArrayList<>();
    private List<String> allPartNames = new ArrayList<>();

    public static EditExerciseDialog newInstance(ExerciseEntity exercise) {
        EditExerciseDialog fragment = new EditExerciseDialog();
        Bundle args = new Bundle();
        args.putLong("EXERCISE_ID", exercise.id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // This still calls the BaseDialog's onViewCreated, which in turn calls our setupUI() below.
        super.onViewCreated(view, savedInstanceState);

        long exerciseId = getArguments().getLong(ARG_EXERCISE_ID);

        // Fetch all parts for the spinners
        viewModel.allParts.observe(getViewLifecycleOwner(), parts -> {
            if (parts != null) {
                this.muscleParts = parts;
                this.allPartNames = parts.stream().map(p -> p.name).collect(Collectors.toList());
            }
        });

        // Fetch the specific exercise
        viewModel.getExerciseById(exerciseId).observe(getViewLifecycleOwner(), exerciseEntity -> {
            if (exerciseEntity != null) {
                this.exercise = exerciseEntity;
                // Once data is here, call setupUI() again. This time it will work.
                setupUI();
            }
        });
    }

    @Override
    protected void setupUI() {
        if (exercise == null) {
            return;
        }

        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonAddExercise.setText("Update");
        binding.buttonAddExercise.setOnClickListener(v -> {
            if (validateInput()) {
                updateExercise();
            }
        });

        binding.editExerciseName.setText(exercise.name);

        ArrayAdapter<String> primaryAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, allPartNames);
        binding.spinnerPrimaryPart.setAdapter(primaryAdapter);

        MusclePartEntity primaryPart = muscleParts.stream().filter(p -> p.id == exercise.primaryPartId).findFirst().orElse(null);
        if (primaryPart != null) {
            binding.spinnerPrimaryPart.setText(primaryPart.name, false);
            updateSecondarySpinner(primaryPart.name);
        }

        if (exercise.secondaryPartId != null) {
            MusclePartEntity secondaryPart = muscleParts.stream().filter(p -> p.id == exercise.secondaryPartId).findFirst().orElse(null);
            if (secondaryPart != null) {
                binding.spinnerSecondaryPart.setText(secondaryPart.name, false);
            }
        }
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
    }

    private void updateExercise() {
        String name = Objects.requireNonNull(binding.editExerciseName.getText()).toString().trim();
        String primaryPartName = binding.spinnerPrimaryPart.getText().toString();
        String secondaryPartName = binding.spinnerSecondaryPart.getText().toString();

        exercise.name = name;
        exercise.primaryPartId = muscleParts.stream().filter(p -> p.name.equals(primaryPartName)).findFirst().get().id;

        if (!secondaryPartName.isEmpty() && !secondaryPartName.equals("None")) {
            exercise.secondaryPartId = muscleParts.stream().filter(p -> p.name.equals(secondaryPartName)).findFirst().get().id;
        } else {
            exercise.secondaryPartId = null;
        }

        viewModel.updateExercise(exercise);
        dismiss();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(binding.editExerciseName.getText())) {
            binding.layoutExerciseName.setError("Name cannot be empty.");
            return false;
        }

        String name = binding.editExerciseName.getText().toString().trim();
        if (Pattern.compile("[0-9]").matcher(name).find()) {
            binding.layoutExerciseName.setError("Name cannot contain numbers");
            return false;
        }

        String primary = binding.spinnerPrimaryPart.getText().toString().trim();
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