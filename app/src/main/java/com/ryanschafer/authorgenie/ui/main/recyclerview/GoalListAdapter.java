package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ryanschafer.authorgenie.data.goals.Goal;

public class GoalListAdapter extends AGListAdapter<Goal, GoalViewHolder> {

    public GoalListAdapter(@NonNull DiffUtil.ItemCallback<Goal> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return GoalViewHolder.create(parent);
    }

//    public Goal getGoal(int position) {
//        return getCurrentList().get(position);
//    }
    
    
    public static class GoalDiff<Goal> extends DiffUtil.ItemCallback<Goal> {

        @Override
        public boolean areItemsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem == newItem;
        }

        
        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Goal oldItem, @NonNull Goal newItem) {
            return oldItem.equals(newItem);
        }
    }
}
