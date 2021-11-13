package com.jhm69.money_tracker.utils;

import android.content.Intent;
import android.util.SparseBooleanArray;

import com.jhm69.money_tracker.ExpenseTrackerApp;
import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.interfaces.IExpensesType;
import com.jhm69.money_tracker.widget.ExpensesWidgetProvider;
import com.jhm69.money_tracker.widget.ExpensesWidgetService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExpensesManager {

    private List<Expense> mExpensesList = new ArrayList<>();
    private SparseBooleanArray mSelectedExpensesItems = new SparseBooleanArray();

    private static ExpensesManager ourInstance = new ExpensesManager();

    public static ExpensesManager getInstance() {
        return ourInstance;
    }

    private ExpensesManager() {
    }

    public void setExpensesList(Date dateFrom, Date dateTo, @IExpensesType int type,  Category category) {
        mExpensesList = Expense.getExpensesList(dateFrom, dateTo, type, category);
        resetSelectedItems();
    }

    public void setExpensesListByDateMode(@IDateMode int mCurrentDateMode) {
        switch (mCurrentDateMode) {
            case IDateMode.MODE_TODAY:
                mExpensesList = Expense.getTodayExpenses();
                break;
            case IDateMode.MODE_WEEK:
                mExpensesList = Expense.getWeekExpenses();
                break;
            case IDateMode.MODE_MONTH:
                mExpensesList = Expense.getMonthExpenses();
                break;
        }
    }

    public List<Expense> getExpensesList() {
        return mExpensesList;
    }

    public SparseBooleanArray getSelectedExpensesItems() {
        return mSelectedExpensesItems;
    }

    public void resetSelectedItems() {
        mSelectedExpensesItems.clear();
    }

    public List<Integer> getSelectedExpensesIndex() {
        List<Integer> items = new ArrayList<>(mSelectedExpensesItems.size());
        for (int i = 0; i < mSelectedExpensesItems.size(); ++i) {
            items.add(mSelectedExpensesItems.keyAt(i));
        }
        return items;
    }

    public void eraseSelectedExpenses() {
        boolean isToday = false;
        List<Expense> expensesToDelete = new ArrayList<>();
        for (int position : getSelectedExpensesIndex()) {
            Expense expense = mExpensesList.get(position);
            expensesToDelete.add(expense);
            Date expenseDate = expense.getDate();
            // update widget if the expense is created today
            if (DateUtils.isToday(expenseDate)) {
                isToday = true;
            }
        }
        if (isToday) {
            Intent i = new Intent(ExpenseTrackerApp.getContext(), ExpensesWidgetProvider.class);
            i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
            ExpenseTrackerApp.getContext().sendBroadcast(i);
        }
        RealmManager.getInstance().delete(expensesToDelete);
    }

    public void setSelectedItems(SparseBooleanArray selectedItems) {
        this.mSelectedExpensesItems = selectedItems;
    }

}
