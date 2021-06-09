package com.ryanschafer.authorgenie3.ui.main.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.ryanschafer.authorgenie3.datamodel.Goal;

import java.util.List;

public class GoalListAdapter extends ListAdapter<Goal, GoalViewHolder> {

    List<Goal> cachedGoals;

    protected GoalListAdapter(@NonNull DiffUtil.ItemCallback<Goal> diffCallback) {
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

    public void setCachedItems(List<Goal> goals) {
        cachedGoals = goals;
    }

    public Goal getGoal(int position) {
        return cachedGoals.get(position);
    }

    static class GoalDiff extends DiffUtil.ItemCallback<Goal> {

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
