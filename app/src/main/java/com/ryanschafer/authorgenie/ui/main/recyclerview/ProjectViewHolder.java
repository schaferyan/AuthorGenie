package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryanschafer.authorgenie.R;
import com.ryanschafer.authorgenie.data.projects.Project;
import com.ryanschafer.authorgenie.ui.utils.DateFormatting;

public class ProjectViewHolder extends RecyclerView.ViewHolder {
    private final ProgressBar progressBar;
    private final TextView numberView;
    private final TextView projectLabel;
    private final TextView dateLabel;

    public ProjectViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progBarRecyclerview);
        numberView = itemView.findViewById(R.id.progressNumbersRecyclerview);
        projectLabel = itemView.findViewById(R.id.nameLabelRecyclerview);
        dateLabel = itemView.findViewById(R.id.dateLabelRecyclerview);
    }

    public void bind(Project project){

        int max = project.getWordGoalTotal();
        int progress = project.getWordCountTotal();
        String labelText = project.getName();
        String dueDate;
        if(project.dueToday()){
            dueDate = DateFormatting.formatTime(project.getDeadline());
        }else {
            dueDate = DateFormatting.formatDate(project.getDeadline());
        }

        progressBar.setMax(max);
        progressBar.setProgress(progress);
        numberView.setText(progress + " / " + max);
        projectLabel.setText(labelText);
        dateLabel.setText(dueDate);
    }

    static ProjectViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new ProjectViewHolder(view);
    }


}
