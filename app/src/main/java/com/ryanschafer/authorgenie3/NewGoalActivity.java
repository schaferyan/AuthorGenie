package com.ryanschafer.authorgenie3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ryanschafer.authorgenie3.databinding.ActivityNewGoalBinding;
import com.ryanschafer.authorgenie3.Model.Goal;

public class NewGoalActivity extends AppCompatActivity {
    ActivityNewGoalBinding binding;
    public static final String EXTRA_AMOUNT = "GOAL AMOUNT";
    public static final String EXTRA_DURATION = "GOAL DURATION";
    public static final String EXTRA_GOALTYPE = "GOAL TYPE";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewGoalBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        ArrayAdapter<String> durAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Goal.getDurations());
        durAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goalDurationSpinner = binding.goalDurationSpinner;
        goalDurationSpinner.setAdapter(durAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Goal.getGoalTypes());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner goalTypeSpinner = binding.goalTypeSpinner;
        goalTypeSpinner.setAdapter(typeAdapter);


        final Button button = binding.buttonSubmitGoal;
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(binding.editGoalAmount.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                int amount = Integer.parseInt(binding.editGoalAmount.getText().toString());
                int duration = binding.goalDurationSpinner.getSelectedItemPosition();
                int goaltype = binding.goalTypeSpinner.getSelectedItemPosition();
                replyIntent.putExtra(EXTRA_AMOUNT, amount);
                replyIntent.putExtra(EXTRA_DURATION, duration);
                replyIntent.putExtra(EXTRA_GOALTYPE, goaltype);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}