package com.ryanschafer.authorgenie.ui.main.recyclerview;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryanschafer.authorgenie.data.AGItem;

import org.jetbrains.annotations.NotNull;

public abstract class AGViewHolder<T extends AGItem> extends RecyclerView.ViewHolder {

    public AGViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T item);



}
