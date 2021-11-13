package com.jhm69.money_tracker.custom;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

import com.jhm69.money_tracker.ExpenseTrackerApp;
import com.jhm69.money_tracker.R;


public class FABScrollBehavior extends FloatingActionButton.Behavior {

    public FABScrollBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
            FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        String a = directTargetChild.getClass().getSimpleName();
        //excluding Statistics case and History for FAB behavior
        if (target instanceof NestedScrollView && target.getTag() != null) {
            if (target.getTag().toString().equalsIgnoreCase(ExpenseTrackerApp.getContext().getString(R.string.statistics)) || target.getTag().toString().equalsIgnoreCase(ExpenseTrackerApp.getContext().getString(R.string.history))) {
                return false;
            }
        }
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
            super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }

}