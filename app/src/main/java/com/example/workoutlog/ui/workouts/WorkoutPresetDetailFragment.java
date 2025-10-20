package com.example.workoutlog.ui.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.databinding.FragmentWorkoutPresetDetailBinding;
import com.example.workoutlog.ui.ViewModelFactory;

public class WorkoutPresetDetailFragment extends Fragment {

    private FragmentWorkoutPresetDetailBinding binding;
    private WorkoutPresetDetailViewModel viewModel;
    private PresetExerciseListAdapter adapter;
    private long presetId = -1L; // Initialize with an invalid ID

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the presetId from the arguments Bundle using our key "presetId".
        if (getArguments() != null) {
            this.presetId = getArguments().getLong("presetId", -1L);
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

        if (presetId == -1L) {
            // If the ID is invalid for any reason, safely navigate back.
            NavHostFragment.findNavController(this).navigateUp();
            return;
        }

        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(WorkoutPresetDetailViewModel.class);

        setupToolbar();
        setupRecyclerView();

        // Observer for the preset's name and details. This will update the toolbar title.
        viewModel.presetDetails.observe(getViewLifecycleOwner(), preset -> {
            if (preset != null) {
                binding.toolbar.setTitle(preset.name);
            }
        });

        // Observer for the list of exercises in the preset.
        viewModel.presetExercises.observe(getViewLifecycleOwner(), exercises -> {
            adapter.submitList(exercises);
            // Show or hide the "empty" text view based on whether the list has items.
            binding.textEmptyView.setVisibility(exercises == null || exercises.isEmpty() ? View.VISIBLE : View.GONE);
            binding.recyclerViewPresetExercises.setVisibility(exercises != null && !exercises.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // This is the crucial step: Tell the ViewModel which preset to load.
        viewModel.loadPreset(presetId);

        binding.fabAddExerciseToPreset.setOnClickListener(v -> {
            // In our next step, we will create and show a dialog here.
            System.out.println("FAB CLICKED: The next step is to open a dialog to add an exercise.");
        });
    }

    private void setupToolbar() {
        // The back arrow in the toolbar will now navigate up the stack to the previous screen.
        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );
    }

    private void setupRecyclerView() {
        adapter = new PresetExerciseListAdapter();
        binding.recyclerViewPresetExercises.setAdapter(adapter);
        binding.recyclerViewPresetExercises.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

