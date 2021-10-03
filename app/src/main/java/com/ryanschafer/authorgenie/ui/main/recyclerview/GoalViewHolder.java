package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryanschafer.authorgenie.data.goals.Goal;
import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.ui.utils.DateFormatting;

public class GoalViewHolder extends RecyclerView.ViewHolder {
    private final ProgressBar progressBar;
    private final TextView numberView;
    private final TextView goalLabel;
    private final TextView dateLabel;

    public GoalViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progBarRecyclerview);
        numberView = itemView.findViewById(R.id.progressNumbersRecyclerview);
        goalLabel = itemView.findViewById(R.id.nameLabelRecyclerview);
        dateLabel = itemView.findViewById(R.id.dateLabelRecyclerview);
    }

    public void bind(Goal goal){

        int max = goal.getObjective();
        int progress = goal.getProgress();
        String labelText = goal.getName();
        String dueDate;
        if(goal.dueToday()){
            dueDate = DateFormatting.formatDate(goal.getDeadline());
        }else {
            dueDate = DateFormatting.formatTime(goal.getDeadline());
        }

        progressBar.setMax(max);
        progressBar.setProgress(progress);
        numberView.setText(progress + " / " + max);
        goalLabel.setText(labelText);
        dateLabel.setText(dueDate);
    }

    static GoalViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new GoalViewHolder(view);
    }


}
