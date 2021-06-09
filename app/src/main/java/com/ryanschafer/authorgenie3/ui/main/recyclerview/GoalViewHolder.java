package com.ryanschafer.authorgenie3.ui.main.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryanschafer.authorgenie3.datamodel.Goal;
import com.ryanschafer.authorgenie3.R;

import java.text.DateFormat;

public class GoalViewHolder extends RecyclerView.ViewHolder {
    private final ProgressBar progressBar;
    private final TextView numberView;
    private final TextView goalLabel;
    private final TextView dateLabel;

    public GoalViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progBarRecyclerview);
        numberView = itemView.findViewById(R.id.progressNumbersRecyclerview);
        goalLabel = itemView.findViewById(R.id.goalLabelRecyclerview);
        dateLabel = itemView.findViewById(R.id.goalDateRecyclerview);
    }

    public void bind(Goal goal){

        int max = goal.getObjective();
        int progress = goal.getProgress();
        String labelText = goal.getName();
        String dueDate = DateFormat.getInstance().format(goal.getDeadlineAsDate());

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
