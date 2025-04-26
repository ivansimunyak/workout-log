package com.example.workoutlog.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workoutlog.R;
import com.example.workoutlog.databinding.FragmentHomeBinding;
import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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

        CalendarView calendarView = binding.calendarView;

        // 1) Set up the month range and first day of week
        YearMonth today = YearMonth.now();
        YearMonth start = today.minusMonths(3);
        YearMonth end   = today.plusMonths(3);
        calendarView.setup(start, end, DayOfWeek.MONDAY);

        // 2) Scroll initially to the current month
        calendarView.scrollToMonth(today);

        // 3) Bind each day cell (just show day number)
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View dayView) {
                return new DayViewContainer(dayView);
            }
            @Override
            public void bind(
                    @NonNull DayViewContainer container,
                    @NonNull CalendarDay day
            ) {
                container.textView.setText(
                        String.valueOf(day.getDate().getDayOfMonth())
                );
            }
        });

        // 4) Explicitly bind each month header to avoid NPE
        calendarView.setMonthHeaderBinder(
                new MonthHeaderFooterBinder<MonthHeaderContainer>() {
                    @NonNull
                    @Override
                    public MonthHeaderContainer create(@NonNull View headerView) {
                        return new MonthHeaderContainer(headerView);
                    }
                    @Override
                    public void bind(
                            @NonNull MonthHeaderContainer container,
                            @NonNull CalendarMonth month
                    ) {
                        YearMonth ym = month.getYearMonth();
                        String title =
                                ym.getMonth().getDisplayName(
                                        java.time.format.TextStyle.FULL,
                                        Locale.getDefault()
                                )
                                        + " " + ym.getYear();
                        container.textView.setText(title);
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // avoid memory leaks
    }

    // --- ViewContainer for day cells ---
    private static class DayViewContainer extends ViewContainer {
        final TextView textView;
        DayViewContainer(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.calendarDayText);
        }
    }

    // --- ViewContainer for month headers ---
    private static class MonthHeaderContainer extends ViewContainer {
        final TextView textView;
        MonthHeaderContainer(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.monthText);
        }
    }
}
