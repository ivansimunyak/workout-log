// app/src/main/java/com/example/workoutlog/ui/exercises/BaseDialog.java
package com.example.workoutlog.ui.exercises;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel; // Import base ViewModel
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.ui.ViewModelFactory;

// Make the class generic for ViewBinding (B) and ViewModel (VM)
public abstract class BaseDialog<B extends ViewBinding, VM extends ViewModel> extends DialogFragment {

    protected B binding;
    protected VM viewModel; // This field will be the correct ViewModel type

    /**
     * Subclasses must implement this to tell the BaseDialog which ViewModel class to create.
     * If no ViewModel is needed, return null.
     */
    @Nullable
    protected abstract Class<VM> getViewModelClass();

    /**
     * Subclasses must implement this to set up their UI.
     * This is called after the binding and (if requested) viewModel are initialized.
     */
    protected abstract void setupUI();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the ViewModel class from the subclass (e.g., WorkoutPresetsViewModel.class)
        Class<VM> vmClass = getViewModelClass();

        if (vmClass != null) {
            // This is the factory logic, now in one central place
            WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
            WorkoutRepository repository = app.getWorkoutRepository();
            ViewModelFactory factory = new ViewModelFactory(repository);
            viewModel = new ViewModelProvider(requireActivity(), factory).get(vmClass);
        }

        // We will let the subclass call setupUI() just to be safe
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}