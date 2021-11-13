package com.jhm69.money_tracker.utils;import android.content.Intent;import android.util.SparseBooleanArray;import com.jhm69.money_tracker.ExpenseTrackerApp;import com.jhm69.money_tracker.entities.Category;import com.jhm69.money_tracker.interfaces.IDateMode;import com.jhm69.money_tracker.interfaces.IIncomesType;import com.jhm69.money_tracker.ui.income.Income;import com.jhm69.money_tracker.widget.ExpensesWidgetProvider;import com.jhm69.money_tracker.widget.ExpensesWidgetService;import java.util.ArrayList;import java.util.Date;import java.util.List;public class IncomeManager  {    private List<Income> incomeList = new ArrayList<>();        private SparseBooleanArray mSelectedIncomesItems = new SparseBooleanArray();        private static final IncomeManager ourInstance = new IncomeManager();        public static IncomeManager getInstance() {            return ourInstance;        }        private IncomeManager() {        }        public void setIncomesList(Date dateFrom, Date dateTo, @IIncomesType int type, Category category) {            incomeList = Income.getIncomesList(dateFrom, dateTo, type, category);            resetSelectedItems();        }        public void setIncomesListByDateMode(@IDateMode int mCurrentDateMode) {            switch (mCurrentDateMode) {                case IDateMode.MODE_TODAY:                    incomeList = Income.getTodayIncomes();                    break;                case IDateMode.MODE_WEEK:                    incomeList = Income.getWeekIncomes();                    break;                case IDateMode.MODE_MONTH:                    incomeList = Income.getMonthIncomes();                    break;            }        }        public List<Income> getIncomesList() {            return incomeList;        }        public SparseBooleanArray getSelectedIncomesItems() {            return mSelectedIncomesItems;        }        public void resetSelectedItems() {            mSelectedIncomesItems.clear();        }        public List<Integer> getSelectedIncomesIndex() {            List<Integer> items = new ArrayList<>(mSelectedIncomesItems.size());            for (int i = 0; i < mSelectedIncomesItems.size(); ++i) {                items.add(mSelectedIncomesItems.keyAt(i));            }            return items;        }        public void eraseSelectedIncomes() {            boolean isToday = false;            List<Income> incomesToDelete = new ArrayList<>();            for (int position : getSelectedIncomesIndex()) {                Income expense = incomeList.get(position);                incomesToDelete.add(expense);                Date expenseDate = expense.getDate();                // update widget if the expense is created today                if (DateUtils.isToday(expenseDate)) {                    isToday = true;                }            }            if (isToday) {                Intent i = new Intent(ExpenseTrackerApp.getContext(), ExpensesWidgetProvider.class);                i.setAction(ExpensesWidgetService.UPDATE_WIDGET);                ExpenseTrackerApp.getContext().sendBroadcast(i);            }            RealmManager.getInstance().delete(incomesToDelete);        }        public void setSelectedItems(SparseBooleanArray selectedItems) {            this.mSelectedIncomesItems = selectedItems;        }}