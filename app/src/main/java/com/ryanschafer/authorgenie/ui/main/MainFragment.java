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
import android.view.Window;
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
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.databinding.MainFragmentBinding;
import com.ryanschafer.authorgenie.datamodel.Goal;
import com.ryanschafer.authorgenie.ui.main.recyclerview.GoalListAdapter;
import com.ryanschafer.authorgenie.ui.main.recyclerview.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class MainFragment extends Fragment {

    private static final String FOOTER_PREF_KEY = "footer_pref_key";
    MainFragmentBinding binding;
    MainViewModel mViewModel;
    GoalListAdapter adapter;
    ArrayAdapter<String> spinnerAdapter;
    Button submitButton;
    Spinner spinner;
    MediaPlayer mediaPlayer;


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
        ArrayList<String> types = new ArrayList<>();
        for(String type : Goal.getGoalTypes()){
            types.add(type + "s");
        }

        spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = binding.recyclerview;
        adapter = new GoalListAdapter(new GoalListAdapter.GoalDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enableSwipeToDeleteAndUndo();
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getGoals().observe(getViewLifecycleOwner(), goals -> {
            adapter.submitList(goals);
            boolean showFooter = goals.isEmpty();
            mViewModel.setShowFooter(showFooter);
            setShowFooter(showFooter);
        });

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
        binding.addGoalButton.setOnClickListener( v -> ((MainActivity) requireActivity()).showAddGoalFragment());
        TextView linkTextView = binding.footer2;
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linkTextView.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.purple_200));

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

        if (binding.instructions != null) {
            binding.instructions.setOnTouchListener(new OnSwipeTouchListener(requireContext()){
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    binding.instructions.setVisibility(View.GONE);
                }
                @Override
                public void onSwipeRight() {
                    super.onSwipeLeft();
                    binding.instructions.setVisibility(View.GONE);
                }
            });
        }
//        Window window = requireActivity().getWindow();



    }

    @Override
    public void onResume() {
        super.onResume();
        setShowFooter(mViewModel.showFooter());
        cycleText();
    }



    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getBindingAdapterPosition();
                final Goal selectedGoal = adapter.getGoal(position);

                mViewModel.removeGoal(selectedGoal);
                if(adapter.getCurrentList().isEmpty()){
                    setShowFooter(true);
                }

                Snackbar snackbar = Snackbar
                        .make(binding.mainFragmentConstraintLayout, "Goal was deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {

                    mViewModel.addGoal(selectedGoal);
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


    public void setShowFooter(boolean show){
        View textView = binding.footer2;
        if(show) {
            textView.setVisibility(View.VISIBLE);
            mViewModel.setShowFooter(true);
        }else{
            textView.setVisibility(View.GONE);
            mViewModel.setShowFooter(false);
        }
    }

    private void cycleText() {
        AppCompatTextView textView = (AppCompatTextView) binding.header;
        String[] strArr = getResources().getStringArray(R.array.inspire_text);
        Random random = new Random();
        int index = random.nextInt(strArr.length);
        String text = strArr[index];
        textView.setText(text);
    }
}
