package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class AGListAdapter<T, VH extends RecyclerView.ViewHolder> extends ListAdapter<T, VH>{
    protected AGListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @NotNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull @NotNull VH holder, int position);

    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }
}
