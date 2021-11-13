package com.jhm69.money_tracker.ui.expenses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.adapters.CategoriesSpinnerAdapter;
import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IExpensesType;
import com.jhm69.money_tracker.interfaces.IUserActionsMode;
import com.jhm69.money_tracker.utils.DateUtils;
import com.jhm69.money_tracker.utils.DialogManager;
import com.jhm69.money_tracker.utils.RealmManager;
import com.jhm69.money_tracker.utils.Util;
import com.jhm69.money_tracker.widget.ExpensesWidgetProvider;
import com.jhm69.money_tracker.widget.ExpensesWidgetService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class NewExpenseFragment extends DialogFragment implements View.OnClickListener{

    private TextView tvTitle;
    private Button btnDate;
    private Spinner spCategory;
    private EditText etDescription;
    private EditText etTotal;

    private CategoriesSpinnerAdapter mCategoriesSpinnerAdapter;
    private Date selectedDate;
    private Expense mExpense;

    private @IUserActionsMode int mUserActionMode;
    private @IExpensesType int mExpenseType;

    static NewExpenseFragment newInstance(@IUserActionsMode int mode, String expenseId) {
        NewExpenseFragment newExpenseFragment = new NewExpenseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IUserActionsMode.MODE_TAG, mode);
        if (expenseId != null) bundle.putString(ExpenseDetailFragment.EXPENSE_ID_KEY, expenseId);
        newExpenseFragment.setArguments(bundle);
        return newExpenseFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_new_expense, container, false);
        tvTitle = (TextView)rootView.findViewById(R.id.tv_title);
        btnDate = (Button)rootView.findViewById(R.id.btn_date);
        spCategory = (Spinner)rootView.findViewById(R.id.sp_categories);
        etDescription = (EditText)rootView.findViewById(R.id.et_description);
        etTotal = (EditText)rootView.findViewById(R.id.et_total);
        mExpenseType = IExpensesType.MODE_EXPENSES;
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mUserActionMode = getArguments().getInt(IUserActionsMode.MODE_TAG) == IUserActionsMode.MODE_CREATE ? IUserActionsMode.MODE_CREATE : IUserActionsMode.MODE_UPDATE;
        }
        setModeViews();
        btnDate.setOnClickListener(this);
        (getView().findViewById(R.id.btn_cancel)).setOnClickListener(this);
        (getView().findViewById(R.id.btn_save)).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void setModeViews() {
        List<Category> categoriesList = Category.getCategoriesExpense();
        Category[] categoriesArray = new Category[categoriesList.size()];
        categoriesArray = categoriesList.toArray(categoriesArray);
        mCategoriesSpinnerAdapter = new CategoriesSpinnerAdapter(getActivity(), categoriesArray);
        spCategory.setAdapter(mCategoriesSpinnerAdapter);
        switch (mUserActionMode) {
            case IUserActionsMode.MODE_CREATE:
                selectedDate = new Date();
                break;
            case IUserActionsMode.MODE_UPDATE:
                if (getArguments() != null) {
                    String id = getArguments().getString(ExpenseDetailFragment.EXPENSE_ID_KEY);
                    mExpense = (Expense) RealmManager.getInstance().findById(Expense.class, id);
                    tvTitle.setText("Edit");
                    selectedDate = mExpense.getDate();
                    etDescription.setText(mExpense.getDescription());
                    etTotal.setText(String.valueOf(mExpense.getTotal()));
                    int categoryPosition = 0;
                    for (int i=0; i<categoriesArray.length; i++) {
                        if (categoriesArray[i].getId().equalsIgnoreCase(mExpense.getCategory().getId())) {
                            categoryPosition = i;
                            break;
                        }
                    }
                    spCategory.setSelection(categoryPosition);
                }
                break;
        }
        updateDate();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_date) {
            showDateDialog();
        } else if (view.getId() == R.id.btn_cancel) {
            dismiss();
        } else if (view.getId() == R.id.btn_save) {
            onSaveExpense();
        }
    }

    private void onSaveExpense() {
        if (mCategoriesSpinnerAdapter.getCount() > 0 ) {
            if (!Util.isEmptyField(etTotal)) {
                Category currentCategory = (Category) spCategory.getSelectedItem();
                String total = etTotal.getText().toString();
                String description = etDescription.getText().toString();
                if (mUserActionMode == IUserActionsMode.MODE_CREATE) {
                    RealmManager.getInstance().save(new Expense(description, selectedDate, mExpenseType, currentCategory, Float.parseFloat(total)), Expense.class);
                } else {
                    Expense editExpense = new Expense();
                    editExpense.setId(mExpense.getId());
                    editExpense.setTotal(Float.parseFloat(total));
                    editExpense.setDescription(description);
                    editExpense.setCategory(currentCategory);
                    editExpense.setDate(selectedDate);
                    RealmManager.getInstance().update(editExpense);
                }
                // update widget if the expense is created today
                if (DateUtils.isToday(selectedDate)) {
                    Intent i = new Intent(getActivity(), ExpensesWidgetProvider.class);
                    i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
                    getActivity().sendBroadcast(i);
                }
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
                dismiss();
            } else {
                DialogManager.getInstance().showShortToast(getString(R.string.error_total));
            }
        } else {
            DialogManager.getInstance().showShortToast(getString(R.string.no_categories_error));
        }
    }

    private void showDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        DialogManager.getInstance().showDatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                selectedDate = calendar.getTime();
                updateDate();
            }
        }, calendar);
    }

    private void updateDate() {
        btnDate.setText(Util.formatDateToString(selectedDate, Util.getCurrentDateFormat()));
    }

}
