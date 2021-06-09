package com.ryanschafer.authorgenie3.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ryanschafer.authorgenie3.R;
import com.ryanschafer.authorgenie3.databinding.DialogFragmentBinding;
import com.ryanschafer.authorgenie3.ui.addgoal.AddGoalFragment;
import com.ryanschafer.authorgenie3.ui.main.MainActivity;

public class ConfirmationDialogFragment extends DialogFragment {
    DialogFragmentBinding binding;
    AlertDialog alertDialog;
    
    public static final int PRIOR_GOAL_EXISTS = 0;
    public static final int FIRST_TIME_USER = 1;
    public static final String DIALOG_TYPE_ID_KEY = "id";
    private int dialogTypeId;

    public ConfirmationDialogFragment() {
        // Required empty public constructor
    }

    public static ConfirmationDialogFragment newInstance(int dialogTypeId) {
        Bundle args = new Bundle();
        args.putInt(DIALOG_TYPE_ID_KEY, dialogTypeId);
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogTypeId = getArguments().getInt(DIALOG_TYPE_ID_KEY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogFragmentBinding.inflate(LayoutInflater.from(getContext()));
        String title = "dialog";
        String message = "message";
        String posText = getString(R.string.yes);
        String negText = getString(R.string.no);
        DialogInterface.OnClickListener posListener = null;
        DialogInterface.OnClickListener negListener = null;
        switch (dialogTypeId){
            case PRIOR_GOAL_EXISTS:
                title = "Overwrite goal";
                message = "You already have a goal set of this kind. Would you like to cancel it and" +
                        "set a new one?";
                posText = getString(R.string.yes);
                negText = getString(R.string.no);
                posListener = this::onReplace;
                negListener = (dialog, id) -> dialog.dismiss();
                break;
            case FIRST_TIME_USER:
                title = "Welcome to Author Genie";
                message = "Get started by entering a goal.";
                posText = "Ok, I will!";
                negText = "No, I don't want to";
                posListener = this::showAddGoalFragment;
                negListener = (dialog, id) -> dialog.dismiss();
                break;
        }
        alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(), R.style.AlertDialogCustom))
                .setView(binding.getRoot())
                .setTitle(title)
                .setPositiveButton(posText, posListener)
                .setNegativeButton(negText, negListener )
                .create();
        binding.confirmMessageText.setText(message);
        return alertDialog;

    }

    private void showAddGoalFragment(DialogInterface dialogInterface, int i) {
        ((MainActivity) requireActivity()).showAddGoalFragment();
        dialogInterface.dismiss();
    }

    private void onReplace(DialogInterface dialog, int id) {
       Fragment fragment = requireActivity().getSupportFragmentManager().
               findFragmentById(R.id.main_fragment_container);
        if (fragment != null) {
            ((AddGoalFragment) fragment).replaceGoal();
        }
        dialog.dismiss();
    }
}
