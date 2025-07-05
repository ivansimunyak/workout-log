package com.example.workoutlog.ui.exercises;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.workoutlog.databinding.DialogAddMusclePartBinding;
import java.util.regex.Pattern;

public class AddMusclePartDialog extends DialogFragment {

    private DialogAddMusclePartBinding binding;
    private ExercisesViewModel viewModel;

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
        binding = DialogAddMusclePartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ExercisesViewModel.class);

        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonAdd.setOnClickListener(v -> {
            if (validateInput()) {
                String name = binding.editMusclePartName.getText().toString().trim();
                viewModel.addMusclePart(name);
                dismiss();
            }
        });
    }

    private boolean validateInput() {
        String input = binding.editMusclePartName.getText().toString().trim();

        if (input.isEmpty()) {
            binding.layoutMusclePartName.setError("Name cannot be empty");
            return false;
        }

        if (Pattern.compile("[0-9]").matcher(input).find()) {
            binding.layoutMusclePartName.setError("Name cannot contain numbers");
            return false;
        }

        binding.layoutMusclePartName.setError(null);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}