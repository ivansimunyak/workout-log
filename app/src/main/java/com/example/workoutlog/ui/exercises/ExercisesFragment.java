package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.workoutlog.R;
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.data.entities.ExerciseEntity;
import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.FragmentExercisesBinding;
import com.example.workoutlog.ui.ViewModelFactory;


public class ExercisesFragment extends Fragment
        implements PartListAdapter.OnPartClickListener,
        ExerciseListAdapter.OnExerciseClickListener {

    private FragmentExercisesBinding binding;
    private ExercisesViewModel vm;
    private PartListAdapter partAdapter;
    private ExerciseListAdapter exerciseAdapter;
    private OnBackPressedCallback onBackPressedCallback;

    private android.view.animation.Animation fab_open, fab_close, rotate_forward,
            rotate_backward;
    private boolean isFabOpen = false;

    private MusclePartEntity pendingMusclePartRemoval;
    private ExerciseEntity pendingExerciseRemoval;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
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

        partAdapter = new PartListAdapter(this);
        exerciseAdapter = new ExerciseListAdapter(this);

        setupRecyclerViews();
        setupSearchInput();
        setupBackButtonCallback();
        setupFabAnimations();
        observeViewModel();
        setupConfirmationListener();
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

    private void setupConfirmationListener() {
        getParentFragmentManager()
                .setFragmentResultListener(
                        "REMOVE_REQUEST_KEY",
                        this,
                        (requestKey, bundle) -> {
                            boolean confirmed = bundle.getBoolean("CONFIRMED");
                            if (confirmed) {
                                if (pendingMusclePartRemoval != null) {
                                    vm.deleteMusclePart(pendingMusclePartRemoval);
                                } else if (pendingExerciseRemoval != null) {
                                    vm.deleteExercise(pendingExerciseRemoval);
                                }
                            }
                            pendingMusclePartRemoval = null;
                            pendingExerciseRemoval = null;
                        }
                );
    }

    @Override
    public void onPartClick(MusclePartEntity part) {
        switchToExerciseListState(true, null, part.id);
    }

    @Override
    public void onRemovePartClick(MusclePartEntity part) {
        pendingMusclePartRemoval = part;
        pendingExerciseRemoval = null;
        RemoveDialog
                .newInstance(part.name, "REMOVE_REQUEST_KEY")
                .show(getParentFragmentManager(), "RemoveDialog");
    }

    @Override
    public void onEditExerciseClick(ExerciseEntity exercise) {
        EditExerciseDialog.newInstance(exercise).show(getParentFragmentManager(), "EditExerciseDialog");
    }

    @Override
    public void onEditPartClick(MusclePartEntity part) {
        EditMusclePartDialog.newInstance(part).show(getParentFragmentManager(), "EditMusclePartDialog");
    }
    @Override
    public void onRemoveExerciseClick(ExerciseEntity exercise) {
        pendingExerciseRemoval = exercise;
        pendingMusclePartRemoval = null;
        RemoveDialog
                .newInstance(exercise.name, "REMOVE_REQUEST_KEY")
                .show(getParentFragmentManager(), "RemoveDialog");
    }

    private void setupRecyclerViews() {
        binding.mainRecycler.setAdapter(partAdapter);
    }

    private void setupSearchInput() {
        binding.searchInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int st, int b, int a) {}

                    @Override
                    public void onTextChanged(CharSequence qs, int st, int b, int a) {
                        handleSearchQuery(qs.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                }
        );
    }

    private void setupBackButtonCallback() {
        onBackPressedCallback =
                new OnBackPressedCallback(false) {
                    @Override
                    public void handleOnBackPressed() {
                        if (binding.searchInput.getText() != null) {
                            binding.searchInput.setText("");
                        } else {
                            switchToPartListState();
                        }
                        if (
                                binding.searchInput.getText() == null ||
                                        binding.searchInput.getText().toString().isEmpty()
                        ) {
                            setEnabled(false);
                        }
                    }
                };
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    private void handleSearchQuery(String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            switchToPartListState();
        } else {
            switchToExerciseListState(false, trimmedQuery, -1L);
        }
    }

    private void observeViewModel() {
        vm.allParts.observe(
                getViewLifecycleOwner(),
                parts -> {
                    if (parts != null) {
                        exerciseAdapter.setPartList(parts);
                        if (binding.mainRecycler.getAdapter() == partAdapter) {
                            partAdapter.submitList(parts);
                        }
                    }
                }
        );
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

    private void switchToExerciseListState(
            boolean isPartSelection,
            @Nullable String searchQuery,
            long selectedPartId
    ) {
        binding.mainRecycler.setAdapter(exerciseAdapter);

        if (isPartSelection) {
            if (
                    binding.searchInput.getText() != null &&
                            !binding.searchInput.getText().toString().isEmpty()
            ) {
                binding.searchInput.setText("");
            }
            vm
                    .getExercisesForPart(selectedPartId)
                    .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
        } else if (searchQuery != null) {
            vm
                    .searchExercises(searchQuery)
                    .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
        }

        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            binding.mainRecycler.setAdapter(null);
        }
        binding = null;
    }

    private void setupFabAnimations() {
        fab_open =
                android.view.animation.AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fab_open
                );
        fab_close =
                android.view.animation.AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fab_close
                );
        rotate_forward =
                android.view.animation.AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate_forward
                );
        rotate_backward =
                android.view.animation.AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate_backward
                );

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
            binding.fabAdd.startAnimation(rotate_backward);
            binding.fabAddPart.startAnimation(fab_close);
            binding.addPartLabel.startAnimation(fab_close);
            binding.fabAddExercise.startAnimation(fab_close);
            binding.addExerciseLabel.startAnimation(fab_close);
            binding.fabAddPart.setVisibility(View.GONE);
            binding.addPartLabel.setVisibility(View.GONE);
            binding.fabAddExercise.setVisibility(View.GONE);
            binding.addExerciseLabel.setVisibility(View.GONE);
            binding.fabAddPart.setClickable(false);
            binding.fabAddExercise.setClickable(false);
            isFabOpen = false;
        } else {
            binding.fabAdd.startAnimation(rotate_forward);
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
}