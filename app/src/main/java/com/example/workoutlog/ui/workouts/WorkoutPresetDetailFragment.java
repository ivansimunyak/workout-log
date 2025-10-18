// app/src/main/java/com/example/workoutlog/ui/workouts/WorkoutPresetDetailFragment.java
package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutlog.R;
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.databinding.FragmentWorkoutPresetDetailBinding;
import com.example.workoutlog.ui.ViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;

public class WorkoutPresetDetailFragment extends Fragment {

    private FragmentWorkoutPresetDetailBinding binding;
    private WorkoutPresetDetailViewModel viewModel;
    private PresetExerciseListAdapter adapter;
    private long presetId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Get the presetId from the navigation arguments
        if (getArguments() != null) {
            presetId = WorkoutPresetDetailFragmentArgs.fromBundle(getArguments()).getPresetId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutPresetDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Setup ViewModel
        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(WorkoutPresetDetailViewModel.class);

        setupToolbar();
        setupRecyclerView();

        // 3. Setup Observers
        viewModel.presetDetails.observe(getViewLifecycleOwner(), preset -> {
            if (preset != null) {
                binding.toolbar.setTitle(preset.name);
            }
        });

        viewModel.presetExercises.observe(getViewLifecycleOwner(), exercises -> {
            adapter.submitList(exercises);
            if (exercises == null || exercises.isEmpty()) {
                binding.textEmptyView.setVisibility(View.VISIBLE);
                binding.recyclerViewPresetExercises.setVisibility(View.GONE);
            } else {
                binding.textEmptyView.setVisibility(View.GONE);
                binding.recyclerViewPresetExercises.setVisibility(View.VISIBLE);
            }
        });

        // 4. Tell the ViewModel which preset to load
        viewModel.loadPreset(presetId);

        // 5. Handle FAB click
        binding.fabAddExerciseToPreset.setOnClickListener(v -> {
            // This is where we will open a *new* dialog to select an exercise
            // For now, let's just log it. We'll build this dialog next.
            System.out.println("FAB CLICKED: Would open add exercise to preset dialog.");

            // Example of how to add a hardcoded exercise:
            // viewModel.addExerciseToPreset(1, 3, 10); // Adds exercise with ID 1 (Bench Press)
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void setupRecyclerView() {
        adapter = new PresetExerciseListAdapter();
        binding.recyclerViewPresetExercises.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}