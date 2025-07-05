package com.example.workoutlog.ui.exercises;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.R;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.DialogAddExerciseBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class AddExerciseDialog extends DialogFragment {

    private DialogAddExerciseBinding binding;
    private ExercisesViewModel viewModel;
    private List<MusclePartEntity> muscleParts = new ArrayList<>();
    private List<String> allPartNames = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddExerciseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExercisesViewModel.class);

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

            // Use the new custom layout here
            ArrayAdapter<String> primaryAdapter = new ArrayAdapter<>(requireContext(), com.example.workoutlog.R.layout.dropdown_item, allPartNames);
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
        // And also use the new custom layout here
        ArrayAdapter<String> secondaryAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, secondaryNames);
        binding.spinnerSecondaryPart.setAdapter(secondaryAdapter);
        binding.spinnerSecondaryPart.setText("", false);
    }

    private void addExercise() {
        String name = binding.editExerciseName.getText().toString().trim();
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
        String name = binding.editExerciseName.getText().toString().trim();
        String primary = binding.spinnerPrimaryPart.getText().toString().trim();
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.layoutExerciseName.setError("Name cannot be empty");
            isValid = false;
        } else {
            binding.layoutExerciseName.setError(null);
        }

        if (primary.isEmpty()) {
            binding.layoutPrimaryPart.setError("Primary muscle is required");
            isValid = false;
        } else if (!allPartNames.contains(primary)) {
            binding.layoutPrimaryPart.setError("Please select a valid muscle");
            isValid = false;
        } else {
            binding.layoutPrimaryPart.setError(null);
        }

        return isValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}