package com.jhm69.money_tracker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import com.google.android.material.appbar.AppBarLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.interfaces.IMainActivityListener;
import com.jhm69.money_tracker.ui.categories.CategoriesFragment;
import com.jhm69.money_tracker.ui.expenses.ExpensesContainerFragment;
import com.jhm69.money_tracker.ui.income.Income;
import com.jhm69.money_tracker.ui.income.IncomeFragment;
import com.jhm69.money_tracker.ui.income.IncomesContainerFragment;
import com.jhm69.money_tracker.ui.statistics.StatisticsFragment;
import com.jhm69.money_tracker.utils.DateUtils;
import com.jhm69.money_tracker.utils.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;
    public static final String NAVIGATION_POSITION = "navigation_position";
    @IDateMode int dateModes;
    private int mCurrentMode = NAVIGATION_MODE_STANDARD;

    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavigationView;
    private Toolbar mToolbar;
    private TabLayout mainTabLayout;
    private FloatingActionButton mFloatingActionButton;

    private Category category;
    // Expenses Summary related views
    private ConstraintLayout llExpensesSummary;
    private TextView tvDate;
    private TextView tvDescription;
    @SuppressLint("StaticFieldLeak")
    public static TextView TOTAL_TV, stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpDrawer();
        setUpToolbar();

        if ( savedInstanceState != null) {
            int menuItemId = savedInstanceState.getInt(NAVIGATION_POSITION);
            mainNavigationView.setCheckedItem(menuItemId);
            mainNavigationView.getMenu().performIdentifierAction(menuItemId, 0);
        } else {
            mainNavigationView.getMenu().performIdentifierAction(R.id.nav_expenses, 0);
        }
    }

    @NavigationMode
    public int getNavigationMode() {
        return mCurrentMode;
    }

    private void initUI() {
        mainDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mainTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mainNavigationView = (NavigationView)findViewById(R.id.nav_view);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab_main);
        llExpensesSummary = (ConstraintLayout) findViewById(R.id.ll_expense_container);
        tvDate = (TextView)findViewById(R.id.tv_date);
        tvDescription = (TextView)findViewById(R.id.tv_description);
        TOTAL_TV = (TextView)findViewById(R.id.tv_amount);
        stock = (TextView)findViewById(R.id.stock);
    }

    private void setUpDrawer() {
        mainNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int idSelectedNavigationItem = 0;
        outState.putInt(NAVIGATION_POSITION, idSelectedNavigationItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mainDrawerLayout.closeDrawers();
        switchFragment(menuItem.getItemId());
        return false;
    }

    @Override
    public void setTabs(List<String> tabList, final TabLayout.OnTabSelectedListener onTabSelectedListener) {
        mainTabLayout.removeAllTabs();
        mainTabLayout.setVisibility(View.VISIBLE);
        mainTabLayout.setOnTabSelectedListener(onTabSelectedListener);
        for (String tab : tabList) {
            mainTabLayout.addTab(mainTabLayout.newTab().setText(tab).setTag(tab));
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setMode(@NavigationMode int mode) {
        mFloatingActionButton.setVisibility(View.GONE);
        llExpensesSummary.setVisibility(View.GONE);
        mCurrentMode = mode;
        switch (mode) {
            case NAVIGATION_MODE_STANDARD:
                setNavigationModeStandard();
                break;
            case NAVIGATION_MODE_TABS:
                setNavigationModeTabs();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setExpensesSummary(@IDateMode int dateMode) {
        float totalExpense = Expense.getTotalExpensesByDateMode(dateMode);
        float totalIncome = Income.getTotalIncomesByDateMode(dateMode);
        stock.setText("Stock Amount " + Util.getFormattedCurrency(totalIncome-totalExpense));
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        if(currentFragment instanceof ExpensesContainerFragment) {
            tvDescription.setText("Total Expenses");
            TOTAL_TV.setText(Util.getFormattedCurrency(totalExpense));
            String date;
            switch (dateMode) {
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
            tvDate.setText(date);
        }else if(currentFragment instanceof IncomesContainerFragment){
            tvDescription.setText("Total Incomes");
            TOTAL_TV.setText(Util.getFormattedCurrency(totalIncome));
            String date;
            switch (dateMode) {
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
            tvDate.setText(date);
        }
    }
    

    @Override
    public void setFAB(@DrawableRes int drawableId, View.OnClickListener onClickListener) {
        mFloatingActionButton.setImageDrawable(getResources().getDrawable(drawableId));
        mFloatingActionButton.setOnClickListener(onClickListener);
        mFloatingActionButton.show();
    }

    @Override
    public void setTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void setPager(ViewPager vp, final TabLayout.ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {
        mainTabLayout.setupWithViewPager(vp);
        mainTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                @IDateMode int dateMode;
                switch (tab.getPosition()) {
                    case 1:
                        dateMode = IDateMode.MODE_WEEK;
                        dateModes = dateMode;
                        break;
                    case 2:
                        dateMode = IDateMode.MODE_MONTH;
                        dateModes = dateMode;
                        break;
                    case 0:
                    default:
                        dateMode = IDateMode.MODE_TODAY;
                        dateModes = dateMode;
                }
                setExpensesSummary(dateMode);
                viewPagerOnTabSelectedListener.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPagerOnTabSelectedListener.onTabUnselected(tab);
            }
        });
        setExpensesSummary(IDateMode.MODE_TODAY);
    }

    public ActionMode setActionMode(final ActionMode.Callback actionModeCallback) {
       return mToolbar.startActionMode(new ActionMode.Callback() {
           @Override
           public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
               return actionModeCallback.onCreateActionMode(mode,menu);
           }

           @Override
           public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
               return actionModeCallback.onPrepareActionMode(mode, menu);
           }

           @Override
           public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
               return actionModeCallback.onActionItemClicked(mode, item);
           }

           @Override
           public void onDestroyActionMode(ActionMode mode) {
               mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
               actionModeCallback.onDestroyActionMode(mode);
           }
       });
    }

    private void setNavigationModeTabs() {
        mainTabLayout.setVisibility(View.VISIBLE);
        llExpensesSummary.setVisibility(View.VISIBLE);
    }

    private void setNavigationModeStandard() {
        CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null && appbar != null) {
            int[] consumed = new int[2];
            behavior.onNestedPreScroll(coordinator, appbar, null, 0, -1000, consumed);
        }
        mainTabLayout.setVisibility(View.GONE);
    }

    @SuppressLint("NonConstantResourceId")
    private void switchFragment(int menuItemId) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        switch (menuItemId) {
            case R.id.nav_expenses:
                if (!(currentFragment instanceof ExpensesContainerFragment)) {
                    replaceFragment(ExpensesContainerFragment.newInstance(), false);
                }
                break;
            case R.id.nav_income:
                if (!(currentFragment instanceof  IncomeFragment)) {
                    replaceFragment(IncomesContainerFragment.newInstance(), false);
                }
                break;
            case R.id.nav_categories:
                if (!(currentFragment instanceof  CategoriesFragment)) replaceFragment(CategoriesFragment.newInstance(), false);
                break;
            case R.id.nav_statistics:
                if (!(currentFragment instanceof  StatisticsFragment)) replaceFragment(StatisticsFragment.newInstance(), false);
                break;
        }
    }
}
