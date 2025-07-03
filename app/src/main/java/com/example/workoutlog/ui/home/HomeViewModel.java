package com.example.workoutlog.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.workoutlog.data.WorkoutRepository; // Import if needed

// Removed @HiltViewModel and @Inject (if they were added)
public class HomeViewModel extends ViewModel {

    // Example: If HomeViewModel needs the repository later
    // private final WorkoutRepository repository;

    private final MutableLiveData<String> mText;

    // Constructor now potentially takes repository (called by ViewModelFactory)
    // Adjust as needed based on HomeViewModel's actual dependencies
    public HomeViewModel(WorkoutRepository repository) { // Add repository if needed
        // this.repository = repository;
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment - Manual DI");
    }

    public LiveData<String> getText() {
        return mText;
    }
}