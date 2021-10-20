package com.ryanschafer.authorgenie.ui.main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.data.AGItem;
import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.data.projects.Project;
import com.ryanschafer.authorgenie.databinding.MainFragmentBinding;
import com.ryanschafer.authorgenie.ui.main.recyclerview.AGListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.AGViewHolder;
import com.ryanschafer.authorgenie.ui.main.recyclerview.GoalListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.ProjectListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.SwipeToDeleteCallback;
import com.ryanschafer.authorgenie.ui.utils.Sound;
import com.ryanschafer.authorgenie.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    private static final String GOAL_TAB_SELECTED_KEY = "main.java.com.ryanschafer.authorgenie.ui.main.goal_tab_selected";
    private static final String NEW_RADIO_SELECTED_KEY = "main.java.com.ryanschafer.authorgenie.ui.main.new_radio_button_selected";
    private static final String PROJECT_SELECTED_KEY = "main.java.com.ryanschafer.authorgenie.ui.main.project_selected_in_project_spinner";
    public static String words_counted_name = "main.java.com.ryanschafer.authorgenie.ui.main.words_counted_in_word_counter";
    public static String words_counted_key = words_counted_name;

    MainFragmentBinding binding;
    MainViewModel mViewModel;
    GoalListAdapter goalListAdapter;
    ProjectListAdapter projectListAdapter;
    ArrayAdapter<String> spinnerAdapter;
    ArrayAdapter<Project> projAdapter;
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
        setUpEntrySpinner();
        setUpRecViewSpinner();
        setUpRecyclerView();
        setupProjectSpinner();
        ViewUtils.setupToggleRecViewButtonTabs(requireContext(), binding.recyclerview, binding.toggleButton, goalListAdapter,
                binding.toggleButton2, projectListAdapter, R.color.teal_200, R.color.teal_700);
        setUpEditText();
        binding.addProgressButton.setOnClickListener(v -> onAddProgressButtonPressed());
        binding.newGoalButton.setOnClickListener( v ->
                ((MainActivity) requireActivity()).showAddTabManagerFragment());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView linkTextView = binding.authorGenieButton;
//        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
//        linkTextView.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.purple_light));
        ViewUtils.handleWindowInsets(binding.newGoalButton, 0, 8, 8, 0, true);
        binding.header.setStrings(getResources().getStringArray(R.array.inspire_text));
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.header.cycleText();
        applyWordCount();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        outState.putBoolean(GOAL_TAB_SELECTED_KEY, binding.toggleButton.isChecked());
        outState.putBoolean(NEW_RADIO_SELECTED_KEY, binding.radioNew.isChecked());
        outState.putInt(PROJECT_SELECTED_KEY, binding.projectSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState == null){
            return;
        }
        if(savedInstanceState.getBoolean(GOAL_TAB_SELECTED_KEY)){
            binding.toggleButton.setChecked(true);
        }else{
            binding.toggleButton2.setChecked(true);
        }

        if(savedInstanceState.getBoolean(NEW_RADIO_SELECTED_KEY)){
            binding.radioNew.setChecked(true);
        }else{
            binding.radioTotal.setChecked(true);
        }

        int projSelected = savedInstanceState.getInt(PROJECT_SELECTED_KEY);
        binding.projectSpinner.setSelection(projSelected);
    }


    private void setupProjectSpinner() {
        projAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item);
        projAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.projectSpinner.setAdapter(projAdapter);
    }

    private void setUpRecViewSpinner() {

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

    private void setUpEntrySpinner(){
        spinnerAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, Goal.getPluralGoalTypes());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.entryTypeSpinner.setAdapter(spinnerAdapter);
    }


    private void setUpRecyclerView(){
        RecyclerView recyclerView = binding.recyclerview;
        goalListAdapter = new GoalListAdapter(new GoalListAdapter.GoalDiff<>());
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
            boolean enterPressed = actionId == binding.enterProgressEditText.getImeActionId();
            if(enterPressed){ onAddProgressButtonPressed(); }
            return enterPressed;
        });
        binding.enterProgressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                ViewUtils.hideKeyboard(v);
            }
        });
    }

    private void onGoalDataSetChanged(List<Goal> goals) {
        goalListAdapter.submitList(goals);
        mViewModel.cacheGoals(goals);
    }
    private void onProjectDataSetChanged(List<Project> projects) {
        mViewModel.onProjectDataChanged(projects);
        projAdapter.clear();
        projAdapter.addAll(projects);
        List<Project> userProjects = new ArrayList<>(projects);
        if(mViewModel.getDefaultProject() == null){
            mViewModel.setDefaultProject(projects);
        }
        if(!projects.isEmpty() && userProjects.get(0).isDefaultProject()) {
            userProjects.remove(0);
        }
        projectListAdapter.submitList(userProjects);
        ViewUtils.setShowView(!projAdapter.isEmpty(), binding.projectSpinner);
    }

    public void applyWordCount(){
        int words = mPreferences.getInt(words_counted_key, 0);
        if(words != 0) {
            addProgress(words, Goal.TYPE.WORD, projAdapter.getItem(0), false);
            mPreferences.edit().putInt(words_counted_key, 0).apply();
        }
    }



    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getBindingAdapterPosition();
                AGListAdapter<AGItem, AGViewHolder<AGItem>> adapter;
                adapter = (AGListAdapter<AGItem, AGViewHolder<AGItem>>) binding.recyclerview.getAdapter();
                if(adapter == null){return;}
                AGItem item = adapter.getItem(position);
                String sbMessage = item.getName() + " " + "was deleted.";
                mViewModel.removeItem(item);
                Snackbar snackbar = Snackbar
                        .make(binding.mainFragmentConstraintLayout, sbMessage, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", view -> {
                            mViewModel.undoDelete(item);
                            binding.recyclerview.scrollToPosition(position);})
                        .setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(binding.recyclerview);
    }

    private void onAddProgressButtonPressed() {
        EditText editText = binding.enterProgressEditText;
        int progress;
        try {
            progress = ViewUtils.getIntFromEditText(editText);
        } catch (NumberFormatException  | NullPointerException e) {
            Toast.makeText(requireContext(), "You must enter a number to submit progress",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Goal.TYPE inputType = Goal.TYPE.values()[binding.entryTypeSpinner.getSelectedItemPosition()];
        Project project = projAdapter.getItem(binding.projectSpinner.getSelectedItemPosition());
        addProgress(progress, inputType, project, binding.radioTotal.isChecked());

        editText.setText("");
        editText.clearFocus();
    }

    private void addProgress(int progress, Goal.TYPE inputType, Project project, boolean total) {
        if(mViewModel.addProgress(progress, inputType, project, total)) {
            respondToProgressAdded(progress);
        }else{
            Toast.makeText(requireContext(), R.string.toast_no_decrease, Toast.LENGTH_LONG).show();
        }
//        "You cannot unwrite what has been written :) Enter a total that's bigger than what you already have
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
            Sound.playSounds(requireContext(), sounds);
        }
    }

}
