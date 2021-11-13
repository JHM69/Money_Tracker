package com.jhm69.money_tracker.utils;

import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.FileGeneratorParser;

import java.util.List;


public class HistoryFileParser implements FileGeneratorParser {

    @Override
    public String generateFileContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Expense Tracker ").append(Util.formatDateToString(DateManager.getInstance().getDateFrom(), Util.getCurrentDateFormat())).append(" - ").append(Util.formatDateToString(DateManager.getInstance().getDateTo(), Util.getCurrentDateFormat())).append(addNextLine());
        List<Expense> expenseList = ExpensesManager.getInstance().getExpensesList();
        contentBuilder.append(addNextLine());
        for (Expense expense : expenseList) {
            contentBuilder.append(Util.formatDateToString(expense.getDate(), Util.getCurrentDateFormat())).append(addTab());
            contentBuilder.append(expense.getCategory().getName()).append(addTab());
            contentBuilder.append(expense.getDescription()).append(addTab());
            contentBuilder.append(expense.getTotal()).append(addNextLine());
        }
        contentBuilder.append(addNextLine());
        float total = Expense.getCategoryTotalByDate(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo(), null);
        contentBuilder.append("Total").append(addTab()).append(total);
        return contentBuilder.toString();
    }

    public static String addNextLine() {
        return "\n";
    }

    public static String addTab() {
        return "\t";
    }

}
