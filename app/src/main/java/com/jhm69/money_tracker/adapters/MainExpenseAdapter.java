package com.jhm69.money_tracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.interfaces.IExpensesType;
import com.jhm69.money_tracker.interfaces.IIncomesType;
import com.jhm69.money_tracker.utils.DateUtils;
import com.jhm69.money_tracker.utils.ExpensesManager;
import com.jhm69.money_tracker.utils.Util;


public class MainExpenseAdapter extends BaseExpenseAdapter {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_EXPENSE_ROW = 1;
    private @IDateMode int mCurrentDateMode;

    public MainExpenseAdapter(Context context, ViewHolder.RecyclerClickListener onRecyclerClickListener, @IDateMode int currentDateMode) {
        super(context, onRecyclerClickListener);
        mCurrentDateMode = currentDateMode;
    }

    @Override
    public MainExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                layoutId = R.layout.layout_expense_header_item;
                break;
            }
            case VIEW_TYPE_EXPENSE_ROW: {
                layoutId = R.layout.layout_expense_item;
                break;
            }
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v, onRecyclerClickListener);
    }

    @Override
    public void onBindViewHolder(BaseExpenseViewHolder holder, int position) {
        holder.itemView.setSelected(isSelected(position));
        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER:
                float total = Expense.getTotalExpensesByDateMode(mCurrentDateMode);
                holder.tvTotal.setText(Util.getFormattedCurrency(total));
                String date;
                switch (mCurrentDateMode) {
                    case IDateMode.MODE_TODAY:
                        date = Util.formatDateToString(DateUtils.getToday(), Util.getCurrentDateFormat());
                        break;
                    case IDateMode.MODE_WEEK:
                        date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentWeek(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentWeek(), Util.getCurrentDateFormat()));
                        break;
                    case IDateMode.MODE_MONTH:
                        date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentMonth(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentMonth(), Util.getCurrentDateFormat()));
                        break;
                    default:
                        date = "";
                        break;
                }
                ((ViewHolder)holder).tvDate.setText(date);
                break;
            case VIEW_TYPE_EXPENSE_ROW:
                final Expense expense = (Expense) mExpensesList.get(position);
                String prefix;
                switch (expense.getType()) {
                    case IExpensesType.MODE_EXPENSES:
                        holder.tvTotal.setTextColor(colorExpense);
                        prefix = String.format(prefixExpense, Util.getFormattedCurrency(expense.getTotal()));
                        break;
                    case IIncomesType.MODE_INCOME:
                        holder.tvTotal.setTextColor(colorIncome);
                        prefix = String.format(prefixIncome, Util.getFormattedCurrency(expense.getTotal()));
                        break;
                    default:
                        prefix = "";
                }
                if (expense.getCategory() != null)holder.tvCategory.setText(expense.getCategory().getName());
                if (expense.getDescription() != null && !expense.getDescription().isEmpty()) {
                    holder.tvDescription.setText(expense.getDescription());
                    holder.tvDescription.setVisibility(View.VISIBLE);
                } else {
                    holder.tvDescription.setVisibility(View.GONE);
                }
                holder.tvTotal.setText(prefix);
                holder.itemView.setTag(expense);
//                ViewCompat.setTransitionName(holder.tvTotal, titleTransitionName);
                break;
        }
        setAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return mExpensesList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_EXPENSE_ROW;
        return VIEW_TYPE_EXPENSE_ROW;
    }

    public void updateExpenses(@IDateMode int mCurrentDateMode) {
        this.mCurrentDateMode = mCurrentDateMode;
        ExpensesManager.getInstance().setExpensesListByDateMode(mCurrentDateMode);
        this.mExpensesList = ExpensesManager.getInstance().getExpensesList();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends BaseExpenseViewHolder {

        TextView tvDate;

        public ViewHolder(View v, RecyclerClickListener onRecyclerClickListener) {
            super(v, onRecyclerClickListener);
            tvDate = (TextView)v.findViewById(R.id.tv_date);
        }

    }

}