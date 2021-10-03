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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.data.projects.Project;
import com.ryanschafer.authorgenie.databinding.MainFragmentBinding;
import com.ryanschafer.authorgenie.ui.main.recyclerview.GoalListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.ProjectListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.SwipeToDeleteCallback;
import com.ryanschafer.authorgenie.ui.utils.ViewUtils;

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
    GoalListAdapter goalListAdapter;
    ProjectListAdapter projectListAdapter;
    ArrayAdapter<String> spinnerAdapter;
    ArrayAdapter<Project> projAdapter;
    MediaPlayer mediaPlayer;
    SharedPreferences mPreferences;





    public MainFragment(){}

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        mPreferences = requireContext().getSharedPreferences(MainActivity.prefFileName, MainActivity.MODE_PRIVATE);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if(savedInstanceState != null) {
            boolean showFooter = savedInstanceState.getBoolean(SHOW_FOOTER_KEY);
            setShowAuthorGenieButton(showFooter);
        }

        setUpEntrySpinner();
        setUpRecViewSpinner();
        setUpProjectSpinner();
        setupSwitch(binding.switch1);
        setUpRecyclerView();
        setupToggleRecViewButtonTabs(binding.recyclerview, binding.toggleButton, goalListAdapter,
                binding.toggleButton2, projectListAdapter);
        setUpEditText();
        binding.addProgressButton.setOnClickListener(v -> addProgressButtonPressed());
        binding.newGoalButton.setOnClickListener( v ->
                ((MainActivity) requireActivity()).showAddTabManagerFragment());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView linkTextView = binding.authorGenieButton;
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linkTextView.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.purple_light));
        setUpTextViewCarousel(binding.header);
        setUpInstructions(binding.instructions);
        ViewUtils.handleWindowInsets(binding.newGoalButton, 0, 8, 8, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        cycleText(binding.header);
        applyWordCount();
    }
//    export and make static


    @SuppressLint("ClickableViewAccessibility")
    private void setUpInstructions(TextView instructions) {
        instructions.setOnTouchListener(new OnSwipeTouchListener(requireContext()){
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
        int visibility = mPreferences.getBoolean(INSTRUCTIONS_SEEN_KEY, false) ?
                View.GONE : View.VISIBLE;
        instructions.setVisibility(visibility);
    }

//    can this go inside the custom component?
    private void setUpTextViewCarousel(TextViewCarousel tvc){
        tvc.setOnTouchListener(new OnSwipeTouchListener(requireContext()){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                cycleText(binding.header);
            }
            @Override
            public void onSwipeRight() {
                super.onSwipeLeft();
                cycleText(binding.header);
            }
        });
    }

    private void setUpProjectSpinner() {
    }

    private void setUpRecViewSpinner() {
        projAdapter = new ArrayAdapter<>(getContext(), R.layout.add_goal_spinner);
        projAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recViewSpinner.setAdapter(projAdapter);

        binding.recViewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Project project = (Project) parent.getSelectedItem();
                List<Goal> goals = mViewModel.getFilteredGoals(project);
                goalListAdapter.submitList(goals);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


//    export to another class and make static
    private void setupSwitch(SwitchCompat switchCompat) {
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(switchCompat.isChecked()){
                switchCompat.setText(switchCompat.getTextOn());
            }else{
                switchCompat.setText(switchCompat.getTextOff());
            }
        });
    }
//    export to another class and make static
    private void setupToggleRecViewButtonTabs(RecyclerView recyclerView, CompoundButton tb1, ListAdapter listAdapter1,
                                              CompoundButton tb2, ListAdapter listAdapter2) {
        tb1.setOnClickListener(v -> {
            tb1.setChecked(true);
            tb2.setChecked(false);
        });
        tb2.setOnClickListener(v -> {
            tb2.setChecked(true);
            tb1.setChecked(false);
        });

        tb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setButtonColor(buttonView, isChecked);
            if(isChecked){
                recyclerView.setAdapter(listAdapter1);
            }
        });

        tb2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setButtonColor(buttonView, isChecked);
            if(isChecked){
                recyclerView.setAdapter(listAdapter2);
            }
        });
        tb1.setChecked(true);
    }
//    export and make static
    private void setButtonColor(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            buttonView.setBackgroundColor(getResources().getColor(R.color.teal_200));
        }else{
            buttonView.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
    }

    private void setUpEntrySpinner(){
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, Goal.getPluralGoalTypes());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.entryTypeSpinner.setAdapter(spinnerAdapter);
    }


    private void setUpRecyclerView(){
        RecyclerView recyclerView = binding.recyclerview;
        goalListAdapter = new GoalListAdapter(new GoalListAdapter.GoalDiff());
        projectListAdapter = new ProjectListAdapter(new ProjectListAdapter.ProjectDiff());
        recyclerView.setAdapter(goalListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipeToDeleteAndUndo();
        mViewModel.getCurrentGoals().observe(getViewLifecycleOwner(), this::onGoalDataSetChanged);
        mViewModel.getCurrentProjects().observe(getViewLifecycleOwner(), this::onProjectDataSetChanged);
    }

    private void setUpEditText(){
        binding.enterProgressEditText.setImeActionLabel("Update", KeyEvent.KEYCODE_ENTER);
        binding.enterProgressEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.enterProgressEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == binding.enterProgressEditText.getImeActionId()){
                addProgressButtonPressed();
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

    private void onGoalDataSetChanged(List<Goal> goals) {
        goalListAdapter.submitList(goals);
        mViewModel.setCachedGoals(goals);
        boolean showAuthorGenieButton = goals.isEmpty();
        setShowAuthorGenieButton(showAuthorGenieButton);
    }
    private void onProjectDataSetChanged(List<Project> projects) {

        boolean showAuthorGenieButton = projects.isEmpty();
        setShowAuthorGenieButton(showAuthorGenieButton);

        projAdapter.clear();
        projAdapter.addAll(projects);
        List<Project> userProjects = new ArrayList<Project>(projects);
        userProjects.remove(0);
        projectListAdapter.submitList(userProjects);
    }

    private void hideInstructions() {
        binding.instructions.setVisibility(View.GONE);
        mPreferences.edit().putBoolean(INSTRUCTIONS_SEEN_KEY, true).apply();
    }



    public void applyWordCount(){
        int words = mPreferences.getInt(words_counted_key, 0);
        if(words != 0) {
            addProgress(words, Goal.TYPE.WORD);
            mPreferences.edit().putInt(words_counted_key, 0).apply();
        }
    }


//    clean this up...
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getBindingAdapterPosition();
                Snackbar snackbar;
                if(binding.recyclerview.getAdapter() instanceof GoalListAdapter){

                    final Goal selectedGoal = goalListAdapter.getItem(position);
                    mViewModel.removeGoal(selectedGoal);
                    if (goalListAdapter.getCurrentList().isEmpty()) {
                        setShowAuthorGenieButton(true);
                    }

                    snackbar = Snackbar
                            .make(binding.mainFragmentConstraintLayout, "Goal was deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", view -> {
                        mViewModel.addGoal(selectedGoal);
                        binding.recyclerview.scrollToPosition(position);
                    });

                }else if(binding.recyclerview.getAdapter() instanceof ProjectListAdapter){
                    final Project selectedProj = projectListAdapter.getItem(position);
                    mViewModel.removeProject(selectedProj);
                    if (projectListAdapter.getCurrentList().isEmpty()) {
                        setShowAuthorGenieButton(true);
                    }

                    snackbar = Snackbar
                            .make(binding.mainFragmentConstraintLayout, "Project was deleted", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", view -> {
                        mViewModel.addProject(selectedProj);
                        binding.recyclerview.scrollToPosition(position);
                    });
                }else{
                    return;
                }
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.recyclerview);
    }

    private void addProgressButtonPressed() {
        EditText editText = binding.enterProgressEditText;
        String progressStr = editText.getText().toString();
        int progress;
        try {
            progress = Integer.parseInt(progressStr);
        } catch (NumberFormatException  | NullPointerException e) {
            Toast.makeText(requireContext(), "You must enter a number to submit progress", Toast.LENGTH_LONG).show();
            return;
        }
        Goal.TYPE inputType = Goal.TYPE.values()[
                binding.entryTypeSpinner.getSelectedItemPosition()];
        addProgress(progress, inputType);
        editText.setText("");
        editText.clearFocus();
    }

    private void addProgress(int progress, Goal.TYPE inputType) {
        mViewModel.addProgress(progress, inputType);
        respondToProgressAdded(progress);
    }

    private void respondToProgressAdded(int progress){
        String message = null;
        int[] sounds = new int[2];

        if(progress != 0) {
            sounds[0] = R.raw.tinkle;
        }

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
        if(PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean("SoundFX", true)){
            playSounds(sounds);
        }

    }


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void playSounds(int[] sounds){
            playSound(sounds[0], new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (sounds[1] != 0) {
                        playSound(sounds[1]);
                    }
                }
            });
    }

    //    export to another class and make static
    public void playSound(int sound, MediaPlayer.OnCompletionListener listener){
        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), sound);
        mediaPlayer.setOnCompletionListener(listener);
        mediaPlayer.start();
    }

    //    export to another class and make static
    public void playSound(int sound){
        MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), sound);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
//        try {
            boolean showFooter = binding.authorGenieButton.getVisibility() == View.VISIBLE;
            outState.putBoolean(SHOW_FOOTER_KEY, showFooter);
//        }catch (NullPointerException e){
//            e.printStackTrace();
//        }
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
        setShowView(show, binding.authorGenieButton);
    }

//    export to another class
    private void setShowView(boolean show, View view){
        if(show) {
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }

    }
//    can this method be owned by TextViewCarousel?
    private void cycleText(TextViewCarousel textView) {
        String[] strArr = getResources().getStringArray(R.array.inspire_text);
        Random random = new Random();
        int index = random.nextInt(strArr.length);
        String text = strArr[index];
        textView.setText(text);
    }
}
