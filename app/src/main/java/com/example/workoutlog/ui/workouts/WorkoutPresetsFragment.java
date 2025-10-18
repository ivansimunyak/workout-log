package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import com.example.workoutlog.databinding.FragmentWorkoutPresetsBinding;
import com.example.workoutlog.ui.ViewModelFactory;
import com.example.workoutlog.ui.exercises.RemoveDialog;

public class WorkoutPresetsFragment extends Fragment
        implements WorkoutPresetListAdapter.OnPresetClickListener {

    private FragmentWorkoutPresetsBinding binding;
    private WorkoutPresetsViewModel vm;
    private WorkoutPresetListAdapter presetAdapter;

    private WorkoutPresetEntity pendingPresetRemoval;

    public WorkoutPresetsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutPresetsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        vm = new ViewModelProvider(requireActivity(), factory).get(WorkoutPresetsViewModel.class);

        presetAdapter = new WorkoutPresetListAdapter(this);

        setupRecyclerView();
        setupFab();
        observeViewModel();
        setupConfirmationListener();
    }

    private void setupRecyclerView() {
        binding.presetsRecycler.setAdapter(presetAdapter);
    }

    private void setupFab() {
        binding.fabAddPreset.setOnClickListener(v -> new AddWorkoutPresetDialog().show(getParentFragmentManager(), "AddWorkoutPresetDialog"));
    }

    private void observeViewModel() {
        vm.allPresets.observe(getViewLifecycleOwner(), presets -> {
            if (presets != null) {
                presetAdapter.submitList(presets);
            }
        });
    }

    private void setupConfirmationListener() {
        getParentFragmentManager()
                .setFragmentResultListener(
                        "REMOVE_PRESET_KEY",
                        this,
                        (requestKey, bundle) -> {
                            boolean confirmed = bundle.getBoolean(RemoveDialog.KEY_CONFIRMED);
                            if (confirmed && pendingPresetRemoval != null) {
                                vm.deleteWorkoutPreset(pendingPresetRemoval);
                            }
                            pendingPresetRemoval = null;
                        }
                );
    }

    // --- OnPresetClickListener Implementations ---

    @Override
    public void onPresetClick(WorkoutPresetEntity preset) {
        // TODO: Implement navigation to the Preset Exercise Editor/Details screen
    }

    @Override
    public void onRemovePresetClick(WorkoutPresetEntity preset) {
        pendingPresetRemoval = preset;
        RemoveDialog
                .newInstance(preset.name, "REMOVE_PRESET_KEY")
                .show(getParentFragmentManager(), "RemovePresetDialog");
    }

    @Override
    public void onEditPresetClick(WorkoutPresetEntity preset) {
        EditWorkoutPresetDialog.newInstance(preset).show(getParentFragmentManager(), "EditWorkoutPresetDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.presetsRecycler.setAdapter(null);
        }
        binding = null;
    }
}