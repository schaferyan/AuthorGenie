package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.ryanschafer.authorgenie.datamodel.Goal;
import com.ryanschafer.authorgenie.databinding.RecyclerviewFragmentBinding;
import com.ryanschafer.authorgenie.ui.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

public class RecyclerviewFragment extends Fragment {





    MainViewModel mViewModel;
    RecyclerviewFragmentBinding binding;
    GoalListAdapter adapter;

    public RecyclerviewFragment() {
        // Required empty public constructor
    }

    public static RecyclerviewFragment newInstance() {
        return new RecyclerviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = RecyclerviewFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

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
            adapter.setCachedItems(goals);
        });


    }



    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getBindingAdapterPosition();
                final Goal selectedGoal = adapter.getGoal(position);

                mViewModel.removeGoal(selectedGoal);


                Snackbar snackbar = Snackbar
                        .make(binding.recyclerviewConstraintLayout, "Goal was deleted", Snackbar.LENGTH_LONG);
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

}