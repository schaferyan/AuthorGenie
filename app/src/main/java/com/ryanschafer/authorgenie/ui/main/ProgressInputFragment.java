package com.ryanschafer.authorgenie.ui.main;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.ProgressInputFragmentBinding;
import com.ryanschafer.authorgenie.datamodel.Goal;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ProgressInputFragment extends Fragment {

    MainViewModel mViewModel;
    ProgressInputFragmentBinding binding;
    ArrayAdapter<String> spinnerAdapter;
    Button submitButton;
    Spinner spinner;
    AudioManager audioManager;
    MediaPlayer mediaPlayer;



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
        submitButton = binding.addProgressButton;
        spinner = binding.spinner;
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Goal.getGoalTypes());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
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
        Goal.TYPE inputType = Goal.TYPE.values()[spinner.getSelectedItemPosition()];
        String message = null;


            String progressStr = editText.getText().toString();
            int progress = 0;
        try {
            progress = Integer.parseInt(progressStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("SUBMITTING PROGRESS", "Failed to submit progress, number format exception");
        }
        if(progress != 0) {
            playSound(getContext(), R.raw.tinkle);
        }
            mViewModel.addProgress(progress, inputType);
            List<Goal> metGoals = mViewModel.getUnannouncedMetGoals();

            if(!metGoals.isEmpty()){
                playSound(getContext(), R.raw.choir);

                if(metGoals.size() > 1){
                    message = "Congratulations! You met your goals!";
                }else{
                    message = "Congratulations! You met your goal!";
                }
                for(Goal goal: metGoals){
                    goal.setNotified(true);
                    mViewModel.overwriteGoal(goal);
                }
            }
            if(message != null) {
                Toast.makeText(getContext(),
                        message, Toast.LENGTH_LONG).show();
            }



        editText.setText("");
        editText.clearFocus();

    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.enterProgressEditText.setImeActionLabel("Update", KeyEvent.KEYCODE_ENTER);
        binding.enterProgressEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.enterProgressEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == binding.enterProgressEditText.getImeActionId()){
                addProgress();
                return true;
            }
            else return false;
        });
        binding.enterProgressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });
        submitButton.setOnClickListener(v -> addProgress());
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void playSound(Context context, int resId){
        mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setVolume(0.5f,0.5f);
        audioManager = (AudioManager) requireActivity().getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer.start();
    }
}