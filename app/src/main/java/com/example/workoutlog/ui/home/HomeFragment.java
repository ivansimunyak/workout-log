package com.example.workoutlog.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import needed

import com.example.workoutlog.R;
import com.example.workoutlog.databinding.FragmentHomeBinding;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

// Imports needed for manual DI
import com.example.workoutlog.WorkoutLogApplication;
import com.example.workoutlog.data.WorkoutRepository;
import com.example.workoutlog.ui.ViewModelFactory;


import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.TextStyle; // Ensure correct import
import java.util.Locale;

// Removed @AndroidEntryPoint if present
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel; // Add ViewModel instance variable

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        // --- Manual ViewModel Initialization ---
        WorkoutLogApplication app = (WorkoutLogApplication) requireActivity().getApplication();
        WorkoutRepository repository = app.getWorkoutRepository();
        ViewModelFactory factory = new ViewModelFactory(repository);
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
        // --- End ViewModel Initialization ---


        // --- CalendarView Setup ---
        CalendarView calendarView = binding.calendarView;
        YearMonth today = YearMonth.now();
        YearMonth start = today.minusMonths(3); // Example range
        YearMonth end = today.plusMonths(3);   // Example range
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY; // Or get from locale

        calendarView.setup(start, end, firstDayOfWeek);
        calendarView.scrollToMonth(today);

        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View dayView) {
                return new DayViewContainer(dayView);
            }
            @Override
            public void bind(@NonNull DayViewContainer container, @NonNull CalendarDay day) {
                container.textView.setText(String.valueOf(day.getDate().getDayOfMonth()));
                // TODO: Add logic here to highlight dates with workouts based on data from homeViewModel
                // e.g., check if day.getDate() is in a list of workout dates from ViewModel
                // container.textView.setBackgroundResource(R.drawable.workout_indicator);
            }
        });

        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthHeaderContainer>() {
            @NonNull
            @Override
            public MonthHeaderContainer create(@NonNull View headerView) {
                return new MonthHeaderContainer(headerView);
            }
            @Override
            public void bind(@NonNull MonthHeaderContainer container, @NonNull CalendarMonth month) {
                YearMonth ym = month.getYearMonth();
                String title = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
                        + " " + ym.getYear();
                container.textView.setText(title);
            }
        });
        // --- End CalendarView Setup ---

        // Observe data from homeViewModel if needed
        // homeViewModel.getSomeData().observe(getViewLifecycleOwner(), data -> { ... });
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            // Example: Update a TextView if you add one to the layout
            // binding.someTextView.setText(text);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // avoid memory leaks
    }

    // --- ViewContainers for Calendar ---
    private static class DayViewContainer extends ViewContainer {
        final TextView textView;
        DayViewContainer(@NonNull View view) {
            super(view);
            // Ensure the ID matches your calendar_day.xml
            textView = view.findViewById(R.id.calendarDayText);
        }
    }

    private static class MonthHeaderContainer extends ViewContainer {
        final TextView textView;
        MonthHeaderContainer(@NonNull View view) {
            super(view);
            // Ensure the ID matches your calendar_month_header.xml
            textView = view.findViewById(R.id.monthText);
        }
    }
}