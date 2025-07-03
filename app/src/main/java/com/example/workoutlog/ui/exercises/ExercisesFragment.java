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
import androidx.recyclerview.widget.LinearLayoutManager; // Import if needed

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
    private OnBackPressedCallback onBackPressedCallback; // Declare the callback

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Get dependencies manually ---
        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        vm = new ViewModelProvider(this, factory).get(ExercisesViewModel.class);
        // --- End dependency setup ---

        setupAdapters();
        setupRecyclerViews();
        setupSearchInput();
        setupBackButtonCallback(); // Setup the back button callback
        observeViewModel();
    }

    private void setupAdapters() {
        partAdapter = new PartListAdapter(this::onPartSelected);
        exerciseAdapter = new ExerciseListAdapter();
    }

    private void setupRecyclerViews() {
        // Set LayoutManager if not done in XML
        // binding.mainRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.mainRecycler.setAdapter(partAdapter); // Start with part list
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
        onBackPressedCallback = new OnBackPressedCallback(false) { // Initially disabled
            @Override
            public void handleOnBackPressed() {
                // This is called when back is pressed AND callback is enabled
                // (i.e., when Exercise List is showing)

                // Revert to Part List State
                if (binding.searchInput.getText() != null) {
                    binding.searchInput.setText(""); // Clear search triggers handleSearchQuery below
                } else {
                    // If search was already empty (drill-down state), directly switch adapter
                    switchToPartListState();
                }
                // Note: Clearing search text will call handleSearchQuery,
                // which will switch the adapter and disable this callback.
                // If search was already empty, we need to explicitly disable.
                if (binding.searchInput.getText() == null || binding.searchInput.getText().toString().isEmpty()){
                    setEnabled(false);
                }
            }
        };
        // Add the callback, tied to the fragment's view lifecycle
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }


    private void handleSearchQuery(String query) {
        String trimmedQuery = query.trim();
        if (trimmedQuery.isEmpty()) {
            // Switching TO Part List because search is empty/cleared
            switchToPartListState();
        } else {
            // Switching TO Exercise List (Search)
            switchToExerciseListState(false, trimmedQuery, -1L); // Indicate search mode
        }
    }

    private void onPartSelected(MusclePartEntity part) {
        // User clicked a part, switch to Exercise List (Part Selection)
        switchToExerciseListState(true, null, part.id); // Indicate part selection mode
    }

    private void observeViewModel() {
        // Observe the list of parts
        vm.allParts.observe(getViewLifecycleOwner(), parts -> {
            if (parts != null) {
                // Update exercise adapter's map regardless of current view
                exerciseAdapter.setPartList(parts);
                // Only update the part list adapter if it's currently visible
                if (binding.mainRecycler.getAdapter() == partAdapter) {
                    partAdapter.submitList(parts);
                }
            }
        });
    }

    private void switchToPartListState() {
        binding.mainRecycler.setAdapter(partAdapter);
        // Ensure the latest part list is displayed
        if (vm.allParts.getValue() != null) {
            partAdapter.submitList(vm.allParts.getValue());
        }
        // Disable the custom back press callback when showing the part list
        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(false);
        }
    }

    private void switchToExerciseListState(boolean isPartSelection, @Nullable String searchQuery, long selectedPartId) {
        binding.mainRecycler.setAdapter(exerciseAdapter);

        if (isPartSelection) {
            // Clear search bar when drilling down into a part for cleaner state
            if (binding.searchInput.getText() != null && !binding.searchInput.getText().toString().isEmpty()) {
                binding.searchInput.setText("");
            }
            vm.getExercisesForPart(selectedPartId)
                    .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
        } else if (searchQuery != null) {
            vm.searchExercises(searchQuery)
                    .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
        }

        // Enable the custom back press callback when showing the exercise list
        if (onBackPressedCallback != null) {
            onBackPressedCallback.setEnabled(true);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // No need to explicitly remove onBackPressedCallback thanks to getViewLifecycleOwner()

        if (binding != null) {
            binding.mainRecycler.setAdapter(null); // Clear adapter reference
        }
        binding = null; // Destroy binding reference
    }
}