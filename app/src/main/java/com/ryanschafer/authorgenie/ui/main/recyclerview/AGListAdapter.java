package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.ryanschafer.authorgenie.data.AGItem;

import org.jetbrains.annotations.NotNull;

public abstract class AGListAdapter<T extends AGItem, VH extends AGViewHolder<T>> extends ListAdapter<T, VH> {

    protected AGListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T current = getItem(position);
        holder.bind(current);
    }

    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }
}
