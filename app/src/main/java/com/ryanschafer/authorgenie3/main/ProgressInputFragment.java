package com.ryanschafer.authorgenie3.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ryanschafer.authorgenie3.R;
import com.ryanschafer.authorgenie3.databinding.ProgressInputFragmentBinding;
import com.ryanschafer.authorgenie3.Model.Goal;
import com.ryanschafer.authorgenie3.databinding.ProgressInputFragmentBinding;


public class ProgressInputFragment extends Fragment {

    MainViewModel mViewModel;
    ProgressInputFragmentBinding binding;
    ArrayAdapter<Goal> goalSpinnerAdapter;
    ImageButton submitButton;



    public static ProgressInputFragment newInstance() {
        return new ProgressInputFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ProgressInputFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionbar_add) {
            addProgress();
            return true;
        }
        else if (item.getItemId() == R.id.action_settings) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    private void openSettings() {
    }

    private void addProgress() {
        EditText editText = binding.enterProgressEditText;
        ToggleButton toggleButton = binding.toggleButton;
        Goal.TYPE inputType;
        if (toggleButton.isChecked()) {
            inputType = Goal.TYPE.WORD;
        } else {
            inputType = Goal.TYPE.TIME;
        }

        try {
            String progressStr = editText.getText().toString();
            int progress = Integer.parseInt(progressStr);
            mViewModel.addProgress(progress, inputType);
            for (Goal goal : mViewModel.getMetGoals()){
                Toast.makeText(getContext(),"You met your " + goal.getName() + "! ", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("SUBMITTING PROGRESS", "Failed to submit progress, number format exception");
        }

        editText.setText("");
        editText.clearFocus();



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);




        submitButton = binding.addProgressButton;

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProgress();
            }
        });
    }
}