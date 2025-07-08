package com.example.workoutlog.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.workoutlog.databinding.DialogRemoveBinding;

public class RemoveDialog extends BaseDialog<DialogRemoveBinding> {

    // Using constants for arguments is a best practice for Fragments
    private static final String ARG_ITEM_NAME = "itemName";
    private static final String ARG_REQUEST_KEY = "requestKey";
    public static final String KEY_CONFIRMED = "CONFIRMED";

    /**
     * Creates a new, properly configured instance of RemoveDialog.
     * @param itemName The name of the item to display in the confirmation message.
     * @param requestKey The key the calling fragment will use to listen for the result.
     * @return A new instance of RemoveDialog.
     */
    public static RemoveDialog newInstance(String itemName, String requestKey) {
        RemoveDialog fragment = new RemoveDialog();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_NAME, itemName);
        args.putString(ARG_REQUEST_KEY, requestKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRemoveBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Sets up all UI elements and listeners for the dialog.
     * This method is called automatically by the BaseDialog's onViewCreated.
     */
    @Override
    protected void setupUI() {
        if (getArguments() != null) {
            String itemName = getArguments().getString(ARG_ITEM_NAME);
            String message = "Are you sure you want to remove \"" + itemName + "\"?";
            binding.textDialogMessage.setText(message);
        }

        binding.buttonCancel.setOnClickListener(v -> dismiss());

        binding.buttonRemove.setOnClickListener(v -> {
            // Send the positive confirmation back to the calling fragment
            if (getArguments() != null) {
                String requestKey = getArguments().getString(ARG_REQUEST_KEY);
                Bundle result = new Bundle();
                result.putBoolean(KEY_CONFIRMED, true);
                getParentFragmentManager().setFragmentResult(requestKey, result);
            }
            dismiss();
        });
    }
}