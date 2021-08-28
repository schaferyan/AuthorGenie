package com.ryanschafer.authorgenie.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.MainFragmentBinding;
import com.ryanschafer.authorgenie.goal_data.Goal;
import com.ryanschafer.authorgenie.ui.main.recyclerview.GoalListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.SwipeToDeleteCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainFragment extends Fragment {

    private static final String INSTRUCTIONS_SEEN_KEY = "instructions_seen_key";
    private static final String SHOW_FOOTER_KEY = "show_footer_if_empty";
    public static String words_counted_name = "words_counted_in_word_counter";
    public static String words_counted_key = words_counted_name;
    MainFragmentBinding binding;
    MainViewModel mViewModel;
    GoalListAdapter adapter;
    ArrayAdapter<String> spinnerAdapter;
    Button submitButton;
    Spinner spinner;
    MediaPlayer mediaPlayer;
    SharedPreferences mPreferences;




    public MainFragment(){}

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        submitButton = binding.addProgressButton;
        spinner = binding.spinner;
        mPreferences = requireContext().getSharedPreferences(MainActivity.prefFileName, MainActivity.MODE_PRIVATE);

        if(savedInstanceState != null) {
            boolean showFooter = savedInstanceState.getBoolean(SHOW_FOOTER_KEY);
            setShowAuthorGenieButton(showFooter);
        }

        setUpSpinner();
        return binding.getRoot();
    }

    private void setUpSpinner(){
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, Goal.getPluralGoalTypes());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }


    private void setUpRecyclerView(){
        RecyclerView recyclerView = binding.recyclerview;
        adapter = new GoalListAdapter(new GoalListAdapter.GoalDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipeToDeleteAndUndo();
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getCurrentGoals().observe(getViewLifecycleOwner(), this::onDataSetChanged);
    }

    private void setUpEditText(){
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
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView();
        setUpEditText();

        submitButton.setOnClickListener(v -> addProgress());
        binding.newGoalButton.setOnClickListener( v -> ((MainActivity) requireActivity()).showAddGoalFragment());
        TextView linkTextView = binding.authorGenieButton;
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linkTextView.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.purple_light));

        binding.header.setOnTouchListener(new OnSwipeTouchListener(requireContext()){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                cycleText();
            }
            @Override
            public void onSwipeRight() {
                super.onSwipeLeft();
                cycleText();
            }
        });

        binding.instructions.setOnTouchListener(new OnSwipeTouchListener(requireContext()){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                hideInstructions();
            }
            @Override
            public void onSwipeRight() {
                super.onSwipeLeft();
                hideInstructions();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(binding.newGoalButton, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() |
                    WindowInsetsCompat.Type.systemGestures());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = insets.bottom + 8;
            mlp.rightMargin = insets.right + 8;
            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });

        if(mPreferences.getBoolean(INSTRUCTIONS_SEEN_KEY, false)){

            binding.instructions.setVisibility(View.GONE);
        }else{
            binding.instructions.setVisibility(View.VISIBLE);
        }



    }

    private void onDataSetChanged(List<Goal> goals) {
        adapter.submitList(goals);
        boolean showAuthorGenieButton = goals.isEmpty();
        setShowAuthorGenieButton(showAuthorGenieButton);
    }

    private void hideInstructions() {
        binding.instructions.setVisibility(View.GONE);
        mPreferences.edit().putBoolean(INSTRUCTIONS_SEEN_KEY, true).apply();

    }

    @Override
    public void onResume() {
        super.onResume();
        cycleText();
        int words = mPreferences.getInt(words_counted_key, 0);
        mViewModel.addProgress(words, Goal.TYPE.WORD, adapter.getCurrentList());
        mPreferences.edit().putInt(words_counted_key, 0).apply();
    }



    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getBindingAdapterPosition();
                final Goal selectedGoal = adapter.getGoal(position);

                mViewModel.removeGoal(selectedGoal);
                if(adapter.getCurrentList().isEmpty()){
                    setShowAuthorGenieButton(true);
                }

                Snackbar snackbar = Snackbar
                        .make(binding.mainFragmentConstraintLayout, "Goal was deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    selectedGoal.setCurrent(true);
                    mViewModel.updateGoal(selectedGoal);
                    binding.recyclerview.scrollToPosition(position);
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.recyclerview);
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
        mViewModel.addProgress(progress, inputType, adapter.getCurrentList());
        List<Goal> metGoals = mViewModel.getUnannouncedMetGoals(adapter.getCurrentList());

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


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void playSounds(int[] sounds){
        if(PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("SoundFX", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), sounds[0]);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.start();
            if (sounds[1] != 0) {
                MediaPlayer mediaPlayer2 = MediaPlayer.create(requireContext(), sounds[1]);
                mediaPlayer2.setOnCompletionListener(MediaPlayer::release);
                mediaPlayer2.start();
            }
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        try {
            boolean showFooter = binding.authorGenieButton.getVisibility() == View.VISIBLE;
            outState.putBoolean(SHOW_FOOTER_KEY, showFooter);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
    }


    public void setShowAuthorGenieButton(boolean show){
        View textView = binding.authorGenieButton;
        if(show) {
            textView.setVisibility(View.VISIBLE);
        }else{
            textView.setVisibility(View.GONE);
        }
    }

    private void cycleText() {
        AppCompatTextView textView = binding.header;
        String[] strArr = getResources().getStringArray(R.array.inspire_text);
        Random random = new Random();
        int index = random.nextInt(strArr.length);
        String text = strArr[index];
        textView.setText(text);
    }
}
