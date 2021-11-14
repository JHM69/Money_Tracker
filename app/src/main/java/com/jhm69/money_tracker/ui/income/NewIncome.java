package com.jhm69.money_tracker.ui.income;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.adapters.CategoriesSpinnerAdapter;
import com.jhm69.money_tracker.adapters.MainIncomeAdapter;
import com.jhm69.money_tracker.custom.BaseViewHolder;
import com.jhm69.money_tracker.custom.DefaultRecyclerViewItemDecorator;
import com.jhm69.money_tracker.custom.SparseBooleanArrayParcelable;
import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IConstants;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.interfaces.IExpensesType;
import com.jhm69.money_tracker.interfaces.IIncomesType;
import com.jhm69.money_tracker.interfaces.IUserActionsMode;
import com.jhm69.money_tracker.ui.MainFragment;
import com.jhm69.money_tracker.ui.expenses.ExpenseDetailFragment;
import com.jhm69.money_tracker.ui.expenses.NewExpenseFragment;
import com.jhm69.money_tracker.utils.DateUtils;
import com.jhm69.money_tracker.utils.DialogManager;
import com.jhm69.money_tracker.utils.ExpensesManager;
import com.jhm69.money_tracker.utils.IncomeManager;
import com.jhm69.money_tracker.utils.RealmManager;
import com.jhm69.money_tracker.utils.Util;
import com.jhm69.money_tracker.widget.ExpensesWidgetProvider;
import com.jhm69.money_tracker.widget.ExpensesWidgetService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class NewIncome extends DialogFragment implements View.OnClickListener{

    private TextView tvTitle;
    private Button btnDate;
    private Spinner spCategory;
    private EditText etDescription;
    private EditText etTotal;

    private CategoriesSpinnerAdapter mCategoriesSpinnerAdapter;
    private Date selectedDate;
    private Income income;

    private @IUserActionsMode
    int mUserActionMode;
    private @IExpensesType
    int type;

    static NewIncome newInstance(@IUserActionsMode int mode, String id) {
        NewIncome newIncome = new NewIncome();
        Bundle bundle = new Bundle();
        bundle.putInt(IUserActionsMode.MODE_TAG, mode);
        if (id != null) bundle.putString(ExpenseDetailFragment.EXPENSE_ID_KEY, id);
        newIncome.setArguments(bundle);
        return newIncome;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_new_income, container, false);
        tvTitle = (TextView)rootView.findViewById(R.id.tv_title);
        btnDate = (Button)rootView.findViewById(R.id.btn_date);
        spCategory = (Spinner)rootView.findViewById(R.id.sp_categories);
        etDescription = (EditText)rootView.findViewById(R.id.et_description);
        etTotal = (EditText)rootView.findViewById(R.id.et_total);
        type = IIncomesType.MODE_INCOME;
        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mUserActionMode = getArguments().getInt(IUserActionsMode.MODE_TAG) == IUserActionsMode.MODE_CREATE ? IUserActionsMode.MODE_CREATE : IUserActionsMode.MODE_UPDATE;
        }
        tvTitle.setText("Add New Income");
        setModeViews();
        btnDate.setOnClickListener(this);
        (Objects.requireNonNull(getView()).findViewById(R.id.btn_cancel)).setOnClickListener(this);
        (getView().findViewById(R.id.btn_save)).setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void setModeViews() {
        List<Category> categoriesList = Category.getCategoriesIncome();

        if(categoriesList.size()==0){
            Category category1 = new Category("Job", IIncomesType.MODE_INCOME);
            Category category2 = new Category("Business", IIncomesType.MODE_INCOME);
            Category category3 = new Category("Scholarship", IIncomesType.MODE_INCOME);

            categoriesList.add(category1);
            categoriesList.add(category2);
            categoriesList.add(category3);

            RealmManager.getInstance().save(category1, Category.class);
            RealmManager.getInstance().save(category2, Category.class);
            RealmManager.getInstance().save(category3, Category.class);
        }

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
                    income = (Income) RealmManager.getInstance().findById(Income.class, id);
                    tvTitle.setText("Edit");
                    selectedDate = income.getDate();
                    etDescription.setText(income.getDescription());
                    etTotal.setText(String.valueOf(income.getTotal()));
                    int categoryPosition = 0;
                    for (int i=0; i<categoriesArray.length; i++) {
                        if (categoriesArray[i].getId().equalsIgnoreCase(income.getCategory().getId())) {
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
                    RealmManager.getInstance().save(new Income(description, selectedDate, type, currentCategory, Float.parseFloat(total)), Income.class);
                } else {
                    Income income = new Income();
                    income.setId(this.income.getId());
                    income.setTotal(Float.parseFloat(total));
                    income.setDescription(description);
                    income.setCategory(currentCategory);
                    income.setDate(selectedDate);
                    RealmManager.getInstance().update(income);
                }
                // update widget if the expense is created today
                if (DateUtils.isToday(selectedDate)) {
                    Intent i = new Intent(getActivity(), ExpensesWidgetProvider.class);
                    i.setAction(ExpensesWidgetService.UPDATE_WIDGET);
                    Objects.requireNonNull(getActivity()).sendBroadcast(i);
                }
                assert getTargetFragment() != null;
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
