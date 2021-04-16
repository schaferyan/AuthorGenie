//package com.ryanschafer.authorgenie3.main;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.ToggleButton;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.ryanschafer.authorgenie3.databinding.MainFragmentBinding;
//import com.ryanschafer.authorgenie3.databinding.MainFragmentSmallScreenBinding;
//import com.ryanschafer.authorgenie3.Model.Goal;
//
//
//public class MainFragmentSmallScreen extends Fragment {
//
//    MainViewModel mViewModel;
//    MainFragmentSmallScreenBinding binding;
//    ArrayAdapter<Goal> goalSpinnerAdapter;
//    ImageButton submitButton;
//
//    public static MainFragmentSmallScreen newInstance() {
//        return new MainFragmentSmallScreen();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        binding = MainFragmentSmallScreenBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
//
//
//        Spinner goalSpinner = binding.goalSpinner;
//        goalSpinnerAdapter = new ArrayAdapter<Goal>(
//                getContext(), android.R.layout.simple_spinner_item);
//        if(mViewModel.getGoals().getValue()!= null){
//            goalSpinnerAdapter.addAll(mViewModel.getGoals().getValue());
//        };
//        goalSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        goalSpinner.setAdapter(goalSpinnerAdapter);
//
//        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//               mViewModel.setActiveGoalPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        submitButton = binding.addProgressButton;
//
//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText editText = binding.enterProgressEditText;
//                ToggleButton toggleButton = binding.toggleButton;
//                Goal.TYPE inputType;
//                if (toggleButton.isChecked()) {
//                    inputType = Goal.TYPE.WORD;
//                } else {
//                    inputType = Goal.TYPE.TIME;
//                }
//
//                try {
//                    String progressStr = editText.getText().toString();
//                    int progress = Integer.parseInt(progressStr);
//                    mViewModel.addProgress(progress, inputType);
//
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                    Log.d("SUBMITTING PROGRESS", "Failed to submit progress, number format exception");
//                }
//
//                editText.setText("");
//                editText.clearFocus();
//            }
//        });
//
////
//        mViewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
//            goalSpinnerAdapter.clear();
//            goalSpinnerAdapter.addAll(goals);
//            mViewModel.getCurrentGoals().clear();
//            mViewModel.getCurrentGoals().addAll(goals);
//            try {
//                int position = mViewModel.getActiveGoalPositionAsLiveData().getValue();
//                updateProgressBar(position);
//            }catch (NullPointerException e){
//                e.printStackTrace();
//            }
//
//        });
//
//        mViewModel.getActiveGoalPositionAsLiveData().observe(getViewLifecycleOwner(), this::updateProgressBar);
//    }
//
//    private void updateProgressBar(int position){
//        if(!mViewModel.getCurrentGoals().isEmpty()) {
//            Goal goal = mViewModel.getCurrentGoals().get(position);
//            ProgressBar progressBar = binding.progressBar;
//            TextView progressLabel = binding.progressTextview;
//            try {
//                progressBar.setMax(goal.getObjective());
//                progressBar.setProgress(goal.getProgress());
//                progressLabel.setText(goal.getProgress()
//                        + " / " + goal.getObjective());
//            } catch (NullPointerException e) {
//                Log.d("ACTIVE GOAL", "Null object reference");
//            }
//        }
//    }
//}