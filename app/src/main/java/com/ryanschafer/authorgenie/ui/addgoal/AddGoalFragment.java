package com.ryanschafer.authorgenie.ui.addgoal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ryanschafer.authorgenie.goals.Goal;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.AddGoalFragmentBinding;
import com.ryanschafer.authorgenie.ui.dialogs.NotificationDialogFragment;
import com.ryanschafer.authorgenie.ui.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddGoalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGoalFragment extends Fragment {


    private static final String SPINNER_TYPE_KEY = "goal type spinner";
    private static final String SPINNER_DURATION_KEY = "goal duration spinner";
    private static final String EDIT_TEXT_KEY = "goal amount edit text" ;
    AddGoalFragmentBinding binding;
    MainViewModel mViewModel;
    Goal goal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(EDIT_TEXT_KEY, binding.editGoalAmount.getText().toString());
            outState.putInt(SPINNER_DURATION_KEY, binding.goalDurationSpinner.getSelectedItemPosition());
            outState.putInt(SPINNER_TYPE_KEY, binding.goalTypeSpinner.getSelectedItemPosition());
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }


    public AddGoalFragment() {
        // Required empty public constructor
    }
        public static AddGoalFragment newInstance() {
            return new AddGoalFragment();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = AddGoalFragmentBinding.inflate(inflater);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            binding.editGoalAmount.setText(savedInstanceState.getString(EDIT_TEXT_KEY));
            binding.goalDurationSpinner.setSelection(savedInstanceState.getInt(SPINNER_DURATION_KEY));
            binding.goalTypeSpinner.setSelection(savedInstanceState.getInt(SPINNER_TYPE_KEY));
        }

        ArrayAdapter<String> durAdapter = new ArrayAdapter<>(getContext(),
                R.layout.add_goal_spinner, Goal.getDurations());
        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goalDurationSpinner = binding.goalDurationSpinner;
        goalDurationSpinner.setAdapter(durAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                R.layout.add_goal_spinner, Goal.getGoalTypes());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goalTypeSpinner = binding.goalTypeSpinner;
        goalTypeSpinner.setAdapter(typeAdapter);


        final Button button = binding.buttonSubmitGoal;
        button.setOnClickListener(buttonView -> {
            if (submitNewGoal()) {
                requireActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        });
        SwitchCompat freqSwitch = binding.freqSwitch;
        freqSwitch.setOnClickListener(v -> {
            if(freqSwitch.isChecked()){
                freqSwitch.setText(R.string.recurring);
            }else{
                freqSwitch.setText(R.string.one_time);
            }
        });
    }

    public boolean submitNewGoal(){
        if (TextUtils.isEmpty(binding.editGoalAmount.getText())) {
            onEmptyInput();
            return false;
        }
        int amount;
        int duration;
        int goaltype;
        boolean recurring;
        try {
            amount = Integer.parseInt(binding.editGoalAmount.getText().toString());
            duration = binding.goalDurationSpinner.getSelectedItemPosition();
            goaltype = binding.goalTypeSpinner.getSelectedItemPosition();
            recurring = binding.freqSwitch.isChecked();
        }catch(NumberFormatException e){
            onInvalidInput();
            return false;
        }
        goal = new Goal(amount,
                Goal.TYPE.values()[goaltype],
                Goal.DURATION.values()[duration], recurring);
        mViewModel.addGoal(goal);
        binding.editGoalAmount.setText("");
        return true;
    }

    private void onEmptyInput() {
        NotificationDialogFragment iidf = NotificationDialogFragment.newInstance(1);
        iidf.show(getChildFragmentManager(),"empty input");
    }


    private void onInvalidInput() {
        NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(0);
        dialog.show(getChildFragmentManager(),"invalid input");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}