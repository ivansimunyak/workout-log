package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.workoutlog.data.entities.MusclePartEntity;
import com.example.workoutlog.databinding.FragmentExercisesBinding;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;
    private ExercisesViewModel vm;
    private PartListAdapter partAdapter;
    private ExerciseListAdapter exerciseAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(ExercisesViewModel.class);
        partAdapter     = new PartListAdapter(this::onPartSelected);
        exerciseAdapter = new ExerciseListAdapter();

        // Observe the list of parts ONCE and feed BOTH adapters
        vm.allParts.observe(getViewLifecycleOwner(), parts -> {
            // 1) Show parts in the parts-list
            partAdapter.submitList(parts);
            // 2) Give exerciseAdapter the name map so it can replace IDs with names
            exerciseAdapter.setPartList(parts);
        });

        // Start by showing the parts list
        binding.mainRecycler.setAdapter(partAdapter);

        // Search box: empty → parts, non-empty → exercises filtered
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int b, int a) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence qs, int st, int b, int a) {
                String query = qs.toString().trim();
                if (query.isEmpty()) {
                    // back to parts
                    binding.mainRecycler.setAdapter(partAdapter);
                    // parts list will auto-refresh via the same allParts observer
                } else {
                    // show search results from exercises
                    binding.mainRecycler.setAdapter(exerciseAdapter);
                    vm.searchExercises(query)
                            .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
                }
            }
        });
    }

    private void onPartSelected(MusclePartEntity part) {
        // Drill into this part’s exercises
        binding.mainRecycler.setAdapter(exerciseAdapter);
        vm.getExercisesForPart(part.id)
                .observe(getViewLifecycleOwner(), exerciseAdapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
