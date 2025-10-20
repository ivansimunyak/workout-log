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
import com.example.workoutlog.R;
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.WorkoutPresetEntity;
import com.example.workoutlog.databinding.FragmentWorkoutPresetsBinding;
import com.example.workoutlog.ui.ViewModelFactory;
import com.example.workoutlog.ui.exercises.RemoveDialog;
import java.io.Serializable;

// The fragment now implements our click listener interface.
public class WorkoutPresetsFragment extends Fragment implements WorkoutPresetListAdapter.OnItemClickListener {

    private FragmentWorkoutPresetsBinding binding;
    private WorkoutPresetsViewModel viewModel;
    private WorkoutPresetListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutPresetsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(WorkoutPresetsViewModel.class);

        setupRecyclerView();

        viewModel.allPresets.observe(getViewLifecycleOwner(), presets -> {
            adapter.submitList(presets);
        });

        binding.fabAddWorkoutPreset.setOnClickListener(v -> {
            new AddWorkoutPresetDialog().show(getChildFragmentManager(), "AddWorkoutPresetDialog");
        });

        // Setup listener for the remove dialog
        getParentFragmentManager().setFragmentResultListener(
                "REMOVE_PRESET_REQUEST",
                this,
                (requestKey, result) -> {
                    if (result.getBoolean(RemoveDialog.KEY_CONFIRMED)) {
                        Serializable item = result.getSerializable("itemToRemove");
                        if (item instanceof WorkoutPresetEntity) {
                            viewModel.deleteWorkoutPreset((WorkoutPresetEntity) item);
                        }
                    }
                }
        );
    }

    private void setupRecyclerView() {
        // Pass 'this' (the fragment) as the listener to the adapter.
        adapter = new WorkoutPresetListAdapter(this);
        binding.recyclerViewWorkoutPresets.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewWorkoutPresets.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Implementation of the click listener method from the interface.
     * This is where we perform the navigation using a Bundle.
     */
    @Override
    public void onItemClick(WorkoutPresetEntity preset) {
        Bundle bundle = new Bundle();
        // The key "presetId" is what we will use to retrieve the value in the detail fragment.
        bundle.putLong("presetId", preset.id);
        // We use the action ID we defined in the cleaned mobile_navigation.xml file.
        NavHostFragment.findNavController(this).navigate(R.id.action_workouts_to_detail, bundle);
    }

    @Override
    public void onEditClick(WorkoutPresetEntity preset) {
        EditWorkoutPresetDialog.newInstance(preset).show(getChildFragmentManager(), "EditWorkoutPresetDialog");
    }

    @Override
    public void onDeleteClick(WorkoutPresetEntity preset) {
        RemoveDialog removeDialog = RemoveDialog.newInstance(preset.name, "REMOVE_PRESET_REQUEST");
        Bundle args = removeDialog.getArguments();
        if (args != null) {
            args.putSerializable("itemToRemove", preset);
        }
        removeDialog.show(getChildFragmentManager(), "RemovePresetDialog");
    }
}

