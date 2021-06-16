package com.ryanschafer.authorgenie.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ryanschafer.authorgenie.R;

import com.ryanschafer.authorgenie.databinding.DialogFragmentBinding;

public class NotificationDialogFragment extends DialogFragment {
    DialogFragmentBinding binding;
    AlertDialog alertDialog;
    
    public static final int NUMBER_FORMAT = 0;
    public static final int BLANK_INPUT = 1;
    public static final String DIALOG_TYPE_ID_KEY = "id";
    private int dialogTypeid;
    public NotificationDialogFragment() {
        // Required empty public constructor
    }

    public static NotificationDialogFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE_ID_KEY, id);
        NotificationDialogFragment fragment = new NotificationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogTypeid = getArguments().getInt(DIALOG_TYPE_ID_KEY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogFragmentBinding.inflate(LayoutInflater.from(getContext()));
        String title = "dialog";
        String message = "message";
        switch (dialogTypeid){
            case NUMBER_FORMAT:
                title = "Invalid Input";
                message = "Numbers only please and thank you";
                break;
            case BLANK_INPUT:
                title = "No input";
                message = "You must enter a number to set a goal!";
                break;
                        
        }
        alertDialog = new AlertDialog.Builder((new ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom)))
                .setTitle(title)
                .setPositiveButton(getString(R.string.OK), (dialog, id) -> dialog.dismiss())
                .setView(binding.getRoot())
                .create();

        binding.confirmMessageText.setText(message);

        return alertDialog;

    }
}
