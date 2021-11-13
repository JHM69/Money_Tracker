package com.jhm69.money_tracker.ui.income;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhm69.money_tracker.ExpenseTrackerApp;
import com.jhm69.money_tracker.R;

import com.jhm69.money_tracker.adapters.MainExpenseAdapter;
import com.jhm69.money_tracker.adapters.MainIncomeAdapter;
import com.jhm69.money_tracker.custom.BaseViewHolder;
import com.jhm69.money_tracker.custom.DefaultRecyclerViewItemDecorator;
import com.jhm69.money_tracker.custom.SparseBooleanArrayParcelable;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IConstants;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.ui.MainActivity;
import com.jhm69.money_tracker.ui.MainFragment;
import com.jhm69.money_tracker.ui.expenses.ExpenseDetailActivity;
import com.jhm69.money_tracker.ui.expenses.ExpenseDetailFragment;
import com.jhm69.money_tracker.ui.expenses.ExpensesFragment;
import com.jhm69.money_tracker.utils.ExpensesManager;
import com.jhm69.money_tracker.utils.IncomeManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class IncomeFragment extends MainFragment implements BaseViewHolder.RecyclerClickListener {

    public static final int RQ_NEW_INCOME = 101;

    private MainIncomeAdapter mainIncomeAdapter;
    private RecyclerView rvExpenses;
    private @IDateMode
    int mCurrentDateMode;
    private IncomeFragment.IIncomeContainerListener incomeContainerListener;
    private final IncomeFragment.IncomeChangeReceiver incomeChangeReceiver = new IncomeFragment.IncomeChangeReceiver();

    public static IncomeFragment newInstance(@IDateMode int dateMode) {
        IncomeFragment incomeFragment = new IncomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IDateMode.DATE_MODE_TAG, dateMode);
        incomeFragment.setArguments(bundle);
        return incomeFragment;
    }

    public IncomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expenses, container, false);
        rvExpenses = rootView.findViewById(R.id.rv_expenses);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IConstants.TAG_SELECTED_ITEMS, new SparseBooleanArrayParcelable(mainIncomeAdapter.getSelectedBooleanArray()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(incomeChangeReceiver, new IntentFilter(IConstants.BROADCAST_UPDATE_EXPENSES));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(incomeChangeReceiver);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            incomeContainerListener = (IncomeFragment.IIncomeContainerListener) getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getParentFragment() != null) {
            incomeContainerListener = null;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            @IDateMode int mode = getArguments().getInt(IDateMode.DATE_MODE_TAG);
            mCurrentDateMode = mode;
            IncomeManager.getInstance().setIncomesListByDateMode(mCurrentDateMode);
            mainIncomeAdapter = new MainIncomeAdapter(getActivity(), this, mCurrentDateMode);
            rvExpenses.setAdapter(mainIncomeAdapter);
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IConstants.TAG_SELECTED_ITEMS)) {
                mainIncomeAdapter.setSelectedItems((SparseBooleanArray)savedInstanceState.getParcelable(IConstants.TAG_SELECTED_ITEMS));
                mainIncomeAdapter.notifyDataSetChanged();
            }
        }
        rvExpenses.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvExpenses.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_5dp)));
    }

    public void updateData() {
        IncomeManager.getInstance().setIncomesListByDateMode(mCurrentDateMode);
        IncomeManager.getInstance().resetSelectedItems();
        if (mainIncomeAdapter != null) mainIncomeAdapter.updateIncomes(mCurrentDateMode);
    }

    @Override
    public void onClick(RecyclerView.ViewHolder vh, int position) {
        /*if (!incomeContainerListener.isActionMode()) {
            Expense expenseSelected = (Expense) vh.itemView.getTag();
            Intent expenseDetail = new Intent(getActivity(), ExpenseDetailActivity.class);
            expenseDetail.putExtra(ExpenseDetailFragment.EXPENSE_ID_KEY, expenseSelected.getId());
            startActivity(expenseDetail);
        } else {
            toggleSelection(position);
        }*/
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder vh, int position) {
        if (!incomeContainerListener.isActionMode()) {
            incomeContainerListener.startActionMode();
        }
        toggleSelection(position);
    }

    public void toggleSelection(int position) {
        mainIncomeAdapter.toggleSelection(position);
        int count = mainIncomeAdapter.getSelectedItemCount();
        if (count == 0) {
            incomeContainerListener.endActionMode();
        } else {
            incomeContainerListener.setActionModeTitle(String.valueOf(count));
        }
    }

    public void cancelActionMode() {
        if (incomeContainerListener.isActionMode()) {
            incomeContainerListener.endActionMode();
            mainIncomeAdapter.clearSelection();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode ==  RQ_NEW_INCOME && resultCode == Activity.RESULT_OK) {
            updateData();
        }
    }

    public class IncomeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            updateData();
        }
    }

    public @IDateMode int getCurrentDateMode() {
        return mCurrentDateMode;
    }

    public interface IIncomeContainerListener {
        void updateIncomeFragments();
        boolean isActionMode();
        void startActionMode();
        void endActionMode();
        void setActionModeTitle(String title);
    }

}
