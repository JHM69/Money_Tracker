package com.jhm69.money_tracker.ui.expenses;

import android.os.Bundle;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.ui.BaseActivity;

public class   ExpenseDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        String expenseId = getIntent().getStringExtra(ExpenseDetailFragment.EXPENSE_ID_KEY);
        replaceFragment(ExpenseDetailFragment.newInstance(expenseId), false);
    }

}
