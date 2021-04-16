package com.ryanschafer.authorgenie3;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.ryanschafer.authorgenie3.Model.Goal;

public class GoalListAdapter extends ListAdapter<Goal, GoalViewHolder> {
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
