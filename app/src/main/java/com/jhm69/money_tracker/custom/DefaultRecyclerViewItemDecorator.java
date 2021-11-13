package com.jhm69.money_tracker.custom;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;


public class DefaultRecyclerViewItemDecorator extends RecyclerView.ItemDecoration {

    private final float mVerticalSpaceHeight;

    public DefaultRecyclerViewItemDecorator(float mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = (int) mVerticalSpaceHeight;
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = (int) mVerticalSpaceHeight;
        }
    }

}
