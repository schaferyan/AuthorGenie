package com.ryanschafer.authorgenie3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ryanschafer.authorgenie3.databinding.RecyclerviewFragmentBinding;
import com.ryanschafer.authorgenie3.main.MainActivity;
import com.ryanschafer.authorgenie3.main.MainViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecyclerviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecyclerviewFragment extends Fragment {


    private static final int NEW_GOAL_ACTIVITY_REQUEST_CODE = 1;



    MainViewModel mViewModel;
    RecyclerviewFragmentBinding binding;
    GoalListAdapter adapter;

    public RecyclerviewFragment() {
        // Required empty public constructor
    }

    
    // TODO: Rename and change types and number of parameters
    public static RecyclerviewFragment newInstance() {
        RecyclerviewFragment fragment = new RecyclerviewFragment();
        Bundle args = new Bundle();
        return fragment;
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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        adapter = new GoalListAdapter(new GoalListAdapter.GoalDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mViewModel.getGoals().observe(getViewLifecycleOwner(), adapter::submitList);

        binding.addGoalButton.setOnClickListener( view -> {
            ((MainActivity) requireActivity()).addGoal();
        });
    }

}