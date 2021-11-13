package com.jhm69.money_tracker.ui.income;

import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.ui.income.Income;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.interfaces.IIncomesType;
import com.jhm69.money_tracker.utils.DateUtils;
import com.jhm69.money_tracker.utils.RealmManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Income extends RealmObject {

    @PrimaryKey
    private String id;
    private String description;
    private Date date;
    private @IIncomesType
    int type;
    private Category category;
    private float total;

    public Income() {
    }

    public Income(String description, Date date, @IIncomesType int type, Category category, float total) {
        this.description = description;
        this.date = date;
        this.type = type;
        this.category = category;
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static float getTotalIncomesByDateMode(@IDateMode int dateMode){
        Date dateFrom;
        Date dateTo;
        switch (dateMode) {
            case IDateMode.MODE_TODAY:
                dateFrom = DateUtils.getToday();
                dateTo = DateUtils.getTomorrowDate();
                break;
            case IDateMode.MODE_WEEK:
                dateFrom = DateUtils.getFirstDateOfCurrentWeek();
                dateTo = DateUtils.getLastDateOfCurrentWeek();
                break;
            case IDateMode.MODE_MONTH:
                dateFrom = DateUtils.getFirstDateOfCurrentMonth();
                dateTo = DateUtils.getLastDateOfCurrentMonth();
                break;
            default:
                dateFrom = new Date();
                dateTo = new Date();
        }
        RealmResults<Income> totalIncome = getIncomesList(dateFrom, dateTo, IIncomesType.MODE_INCOME, null);
        return totalIncome.sum("total").floatValue();
    }

    public static List<Income> getTodayIncomes() {
        Date today = DateUtils.getToday();
        Date tomorrow = DateUtils.getTomorrowDate();
        return getIncomesList(today, tomorrow, null, null);
    }

    public static List<Income> getWeekIncomes() {
        Date startWeek = DateUtils.getFirstDateOfCurrentWeek();
        Date endWeek = DateUtils.getLastDateOfCurrentWeek();
        return getIncomesList(startWeek, endWeek, null, null);
    }

    public static List<Income> getWeekIncomesByCategory(Income expense) {
        Date startWeek = DateUtils.getFirstDateOfCurrentWeek();
        Date endWeek = DateUtils.getLastDateOfCurrentWeek();
        return getIncomesList(startWeek, endWeek, null, expense.getCategory());
    }

    public static List<Income> getMonthIncomes() {
        Date startMonth = DateUtils.getFirstDateOfCurrentMonth();
        Date endMonth = DateUtils.getLastDateOfCurrentMonth();
        return getIncomesList(startMonth, endMonth, null, null);
    }

    public static float getCategoryTotalByDate(Date date, Category category) {
        RealmResults<Income> totalIncome = getIncomesList(date, DateUtils.addDaysToDate(date, 1), IIncomesType.MODE_INCOME, category);
        return totalIncome.sum("total").floatValue();
    }

    public static float getCategoryTotalByDate(Date fromDate, Date toDate, Category category) {
        RealmResults<Income> totalIncome = getIncomesList(fromDate, DateUtils.addDaysToDate(toDate, 1), IIncomesType.MODE_INCOME, category);
        return totalIncome.sum("total").floatValue();
    }

    public static RealmResults<Income> getIncomesPerCategory(Category category) {
        return RealmManager.getInstance().getRealmInstance().where(Income.class).equalTo("category.id", category.getId()).findAll();
    }

    public static RealmResults<Income> getIncomesList(Date dateFrom, Date dateTo, @IIncomesType Integer type, Category category) {
        RealmQuery<Income> realmQuery = RealmManager.getInstance().getRealmInstance()
                .where(Income.class);
        if (dateTo != null) {
            realmQuery.between("date", dateFrom, dateTo);
        } else {
            realmQuery.equalTo("date", dateFrom);
        }
        if (category != null) realmQuery.equalTo("category.id", category.getId());
        if (type != null) realmQuery.equalTo("type", type);
        return realmQuery.findAll();
    }

    public static float getIncomesCategoryPercentage(Date fromDate, Date toDate, Category category) {
        float totalCategory = getCategoryTotalByDate(fromDate, toDate, category);
        float total = getIncomesList(fromDate, DateUtils.addDaysToDate(toDate, 1), IIncomesType.MODE_INCOME, null).sum("total").floatValue();
        return totalCategory * 100 / total;
    }

    public static List<Income> cloneIncomeCollection(List<Income> incomelist) {
        List<Income> clonedIncomes = new ArrayList<>();
        for (Income income : incomelist) {
            Income cloneIncome = new Income();
            cloneIncome.setId(income.getId());
            Category category = new Category();
            category.setName(income.getCategory().getName());
            cloneIncome.setCategory(category);
            cloneIncome.setDate(income.getDate());
            cloneIncome.setDescription(income.getDescription());
            cloneIncome.setTotal(income.getTotal());
            cloneIncome.setType(income.getType());
            clonedIncomes.add(cloneIncome);
        }
        return clonedIncomes;
    }
}
