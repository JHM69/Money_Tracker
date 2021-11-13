package com.jhm69.money_tracker.custom;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    protected RecyclerClickListener onRecyclerClickListener;

    public interface RecyclerClickListener {
        void onClick(RecyclerView.ViewHolder vh, int position);
        void onLongClick(RecyclerView.ViewHolder vh, int position);
    }

    public BaseViewHolder(View v, RecyclerClickListener onRecyclerClickListener ) {
        super(v);
        this.onRecyclerClickListener = onRecyclerClickListener;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerClickListener != null) onRecyclerClickListener.onClick(this, getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRecyclerClickListener != null) onRecyclerClickListener.onLongClick(this, getAdapterPosition());
        return true;
    }

}
