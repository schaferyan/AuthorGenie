package com.ryanschafer.authorgenie.ui.main;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
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
            ((MainActivity) requireActivity()).showAddGoalFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addProgress() {
        EditText editText = binding.enterProgressEditText;
        String progressStr = editText.getText().toString();
        int progress;
        try {
            progress = Integer.parseInt(progressStr);
        } catch (NumberFormatException  | NullPointerException e) {
            Toast.makeText(requireContext(), "You must enter a number to submit progress", Toast.LENGTH_LONG).show();
            return;
        }
        Goal.TYPE inputType = Goal.TYPE.values()[spinner.getSelectedItemPosition()];
        String message = null;
        int[] sounds = new int[2];




        if(progress != 0) {
            sounds[0] = R.raw.tinkle;
        }
            mViewModel.addProgress(progress, inputType);
            List<Goal> metGoals = mViewModel.getUnannouncedMetGoals();

            if(!metGoals.isEmpty()){
                sounds[1] = R.raw.success;
                message = "Success!";
                for(Goal goal: metGoals){
                    mViewModel.setGoalNotified(goal, true);
                }
            }
            if(message != null) {
                Toast.makeText(getContext(),
                        message, Toast.LENGTH_LONG).show();
            }
        editText.setText("");
        editText.clearFocus();

        playSounds(sounds);

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

    public void playSounds(int[] sounds){
        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), sounds[0]);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
        if(sounds[1] != 0) {
            MediaPlayer mediaPlayer2 = MediaPlayer.create(requireContext(), sounds[1]);
            mediaPlayer2.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer2.start();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}