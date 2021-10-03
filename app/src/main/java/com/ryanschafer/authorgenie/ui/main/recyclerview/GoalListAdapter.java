package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.data.projects.Project;

import java.util.ArrayList;
import java.util.List;

public class GoalListAdapter extends ListAdapter<Goal, GoalViewHolder> {
    boolean filter;
    int project;
    List<Goal> cachedGoals;

    public GoalListAdapter(@NonNull DiffUtil.ItemCallback<Goal> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return GoalViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal current = getItem(position);
        holder.bind(current);
    }

//    public Goal getGoal(int position) {
//        return getCurrentList().get(position);
//    }


    @Override
    public Goal getItem(int position) {
        return super.getItem(position);
    }

    public static class GoalDiff extends DiffUtil.ItemCallback<Goal> {

        @Override
        public boolean areItemsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem.getProgress() == newItem.getProgress();
        }
    }
}
