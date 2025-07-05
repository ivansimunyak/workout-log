package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback; // Import OnBackPressedCallback
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.R;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.FragmentExercisesBinding;

// Imports needed for manual DI and the Factory
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.ui.ViewModelFactory;

public class ExercisesFragment extends Fragment {
    private FragmentExercisesBinding binding;
    private ExercisesViewModel vm;
    private PartListAdapter partAdapter;
    private ExerciseListAdapter exerciseAdapter;
    private OnBackPressedCallback onBackPressedCallback;
    // Animation variables
    private android.view.animation.Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private boolean isFabOpen = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        vm = new ViewModelProvider(requireActivity(), factory).get(ExercisesViewModel.class);

        setupAdapters();
        setupRecyclerViews();
        setupSearchInput();
        setupBackButtonCallback();
        setupFabAnimations();
        observeViewModel();
    }

    private void setupAdapters() {
        partAdapter = new PartListAdapter(this::onPartSelected);
        exerciseAdapter = new ExerciseListAdapter();
    }

    // In ExercisesFragment.java
    private void setupFabAnimations() {
        fab_open = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open);
        fab_close = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close);
        rotate_forward = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward);
        rotate_backward = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_backward);

        binding.fabAddPart.setOnClickListener(v -> {
            new AddMusclePartDialog().show(getParentFragmentManager(), "AddMusclePartDialog");
            animateFab();
        });

        binding.fabAddExercise.setOnClickListener(v -> {
            new AddExerciseDialog().show(getParentFragmentManager(), "AddExerciseDialog");
            animateFab();
        });

        binding.fabAdd.setOnClickListener(v -> animateFab());
    }


    private void animateFab() {
        if (isFabOpen) {
            // Close animation
            binding.fabAdd.startAnimation(rotate_backward);

            binding.fabAddPart.startAnimation(fab_close);
            binding.addPartLabel.startAnimation(fab_close);
            binding.fabAddExercise.startAnimation(fab_close);
            binding.addExerciseLabel.startAnimation(fab_close);

            // After animation, set visibility to GONE so they don't block clicks
            binding.fabAddPart.setVisibility(View.GONE);
            binding.addPartLabel.setVisibility(View.GONE);
            binding.fabAddExercise.setVisibility(View.GONE);
            binding.addExerciseLabel.setVisibility(View.GONE);

            binding.fabAddPart.setClickable(false);
            binding.fabAddExercise.setClickable(false);
            isFabOpen = false;
        } else {
            // Open animation
            binding.fabAdd.startAnimation(rotate_forward);

            // Before animation, make them visible
            binding.fabAddPart.setVisibility(View.VISIBLE);
            binding.addPartLabel.setVisibility(View.VISIBLE);
            binding.fabAddExercise.setVisibility(View.VISIBLE);
            binding.addExerciseLabel.setVisibility(View.VISIBLE);

            binding.fabAddPart.startAnimation(fab_open);
            binding.addPartLabel.startAnimation(fab_open);
            binding.fabAddExercise.startAnimation(fab_open);
            binding.addExerciseLabel.startAnimation(fab_open);

            binding.fabAddPart.setClickable(true);
            binding.fabAddExercise.setClickable(true);
            isFabOpen = true;
        }
    }

    private void showAddItemDialog(String title, java.util.function.Consumer<String> positiveAction) {
        // Inflate the new custom layout
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_muscle_part, null);
        final com.google.android.material.textfield.TextInputEditText input = dialogView.findViewById(R.id.edit_muscle_part_name);

        // Use the new custom dialog theme
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.Theme_WorkoutLog_Dialog)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (!text.isEmpty()) {
                        positiveAction.accept(text);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void addMusclePart(String name) {
        vm.addMusclePart(name);
        android.widget.Toast.makeText(requireContext(), "Added: " + name, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void showAddExerciseDialog() {
        // Inflate the custom layout
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_exercise, null);
        final com.google.android.material.textfield.TextInputEditText nameInput = dialogView.findViewById(R.id.edit_exercise_name);
        // Note the change here to AutoCompleteTextView
        final android.widget.AutoCompleteTextView primarySpinner = dialogView.findViewById(R.id.spinner_primary_part);
        final android.widget.AutoCompleteTextView secondarySpinner = dialogView.findViewById(R.id.spinner_secondary_part);

        java.util.List<MusclePartEntity> muscleParts = vm.allParts.getValue();
        if (muscleParts == null) {
            android.widget.Toast.makeText(requireContext(), "Muscle parts not loaded yet.", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a list of names for the dropdowns
        java.util.List<String> partNames = new java.util.ArrayList<>();
        for (MusclePartEntity part : muscleParts) {
            partNames.add(part.name);
        }

        // Adapter for the primary dropdown
        android.widget.ArrayAdapter<String> primaryAdapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, partNames);
        primarySpinner.setAdapter(primaryAdapter);

        // For the secondary dropdown, add a "None" option
        java.util.List<String> secondaryPartNames = new java.util.ArrayList<>();
        secondaryPartNames.add("None");
        secondaryPartNames.addAll(partNames);
        android.widget.ArrayAdapter<String> secondaryAdapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, secondaryPartNames);
        secondarySpinner.setAdapter(secondaryAdapter);

        // Use the new custom dialog theme
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.Theme_WorkoutLog_Dialog)
                .setTitle("Add Exercise")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String primaryPartName = primarySpinner.getText().toString();

                    if (name.isEmpty() || primaryPartName.isEmpty()) {
                        return;
                    }

                    // Find the ID of the selected primary muscle part
                    long primaryId = -1;
                    for(int i = 0; i < partNames.size(); i++) {
                        if(partNames.get(i).equals(primaryPartName)) {
                            primaryId = muscleParts.get(i).id;
                            break;
                        }
                    }
                    if (primaryId == -1) return; // Should not happen

                    // Find the ID of the selected secondary muscle part
                    Long secondaryId = null;
                    String secondaryPartName = secondarySpinner.getText().toString();
                    if (!secondaryPartName.isEmpty() && !secondaryPartName.equals("None")) {
                        for(int i = 0; i < partNames.size(); i++) {
                            if(partNames.get(i).equals(secondaryPartName)) {
                                secondaryId = muscleParts.get(i).id;
                                break;
                            }
                        }
                    }

                    vm.addExercise(name, primaryId, secondaryId);
                    android.widget.Toast.makeText(requireContext(), "Added: " + name, android.widget.Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupRecyclerViews() {
        binding.mainRecycler.setAdapter(partAdapter);
    }

    private void setupSearchInput() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int a) {}
            @Override public void onTextChanged(CharSequence qs, int st, int b, int a) {
                handleSearchQuery(qs.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBackButtonCallback() {
        onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (binding.searchInput.getText() != null) {
                    binding.searchInput.setText("");
                } else {
                    switchToPartListState();
                }
                if (binding.searchInput.getText() == null || binding.searchInput.getText().toString().isEmpty()){
                    setEnabled(false);
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }


    private void handleSearchQuery(String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            switchToPartListState();
        } else {
            switchToExerciseListState(false, trimmedQuery, -1L);
        }
    }

    private void onPartSelected(MusclePartEntity part) {
        switchToExerciseListState(true, null, part.id);
    }

    private void observeViewModel() {
        vm.allParts.observe(getViewLifecycleOwner(), parts -> {
            if (parts != null) {
                exerciseAdapter.setPartList(parts);
                if (binding.mainRecycler.getAdapter() == partAdapter) {
                    partAdapter.submitList(parts);
                }
            }
        });
    }

    private void switchToPartListState() {
        binding.mainRecycler.setAdapter(partAdapter);
        if (vm.allParts.getValue() != null) {
            partAdapter.submitList(vm.allParts.getValue());
        }
        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(false);
        }
    }

    private void switchToExerciseListState(boolean isPartSelection, @Nullable String searchQuery, long selectedPartId) {
        binding.mainRecycler.setAdapter(exerciseAdapter);

        if (isPartSelection) {
            if (binding.searchInput.getText() != null && !binding.searchInput.getText().toString().isEmpty()) {
                binding.searchInput.setText("");
            }
            vm.getExercisesForPart(selectedPartId)
                    .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
        } else if (searchQuery != null) {
            vm.searchExercises(searchQuery)
                    .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
        }

        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(true);
        }
    }

    public void resetToDefaultState() {
        if (binding == null) return;

        if (binding.searchInput.getText() != null && !binding.searchInput.getText().toString().isEmpty()) {
            binding.searchInput.setText("");
        }
        else if (binding.mainRecycler.getAdapter() instanceof ExerciseListAdapter) {
            switchToPartListState();
        }

        binding.mainRecycler.scrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.mainRecycler.setAdapter(null);
        }
        binding = null;
    }
}