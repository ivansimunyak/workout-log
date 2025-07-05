package com.example.workoutlog;

import android.os.Bundle;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.workoutlog.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemInsets.top, 0, 0);
            ViewGroup.LayoutParams params = v.getLayoutParams();
            int toolbarHeight = v.getResources().getDimensionPixelSize(R.dimen.default_toolbar_height);
            params.height = systemInsets.top + toolbarHeight;
            v.setLayoutParams(params);
            return insets;
        });

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_workouts,
                R.id.navigation_exercises,
                R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        binding.navView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_exercises) {
                androidx.navigation.fragment.NavHostFragment navHostFragment =
                        (androidx.navigation.fragment.NavHostFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.nav_host_fragment_activity_main);
                if (navHostFragment != null) {
                    Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                    if (currentFragment instanceof com.example.workoutlog.ui.exercises.ExercisesFragment) {
                        ((com.example.workoutlog.ui.exercises.ExercisesFragment) currentFragment).resetToDefaultState();
                    }
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, (AppBarConfiguration) null)
                || super.onSupportNavigateUp();
    }
}