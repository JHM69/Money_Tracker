package com.jhm69.money_tracker.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;

import com.jhm69.money_tracker.utils.ExpensesManager;

import java.util.ArrayList;
import java.util.List;


abstract class BaseExpenseRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {


    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }


    public void toggleSelection(int position) {
        if (ExpensesManager.getInstance().getSelectedExpensesItems().get(position, false)) {
            ExpensesManager.getInstance().getSelectedExpensesItems().delete(position);
        } else {
            ExpensesManager.getInstance().getSelectedExpensesItems().put(position, true);
        }
        notifyItemChanged(position);
    }


    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        ExpensesManager.getInstance().getSelectedExpensesItems().clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }


    public int getSelectedItemCount() {
        return ExpensesManager.getInstance().getSelectedExpensesItems().size();
    }


    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(ExpensesManager.getInstance().getSelectedExpensesItems().size());
        for (int i = 0; i < ExpensesManager.getInstance().getSelectedExpensesItems().size(); ++i) {
            items.add(ExpensesManager.getInstance().getSelectedExpensesItems().keyAt(i));
        }
        return items;
    }

    public SparseBooleanArray getSelectedBooleanArray() {
        return ExpensesManager.getInstance().getSelectedExpensesItems();
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        ExpensesManager.getInstance().setSelectedItems(selectedItems);
    }

}
