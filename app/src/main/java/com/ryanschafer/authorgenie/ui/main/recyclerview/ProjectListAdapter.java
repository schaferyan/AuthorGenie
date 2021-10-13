package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ryanschafer.authorgenie.data.projects.Project;

public class ProjectListAdapter extends AGListAdapter<Project, ProjectViewHolder> {


    public ProjectListAdapter(@NonNull DiffUtil.ItemCallback<Project> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ProjectViewHolder.create(parent);
    }

    public static class ProjectDiff extends DiffUtil.ItemCallback<Project> {

        @Override
        public boolean areItemsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Project oldItem, @NonNull Project newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
