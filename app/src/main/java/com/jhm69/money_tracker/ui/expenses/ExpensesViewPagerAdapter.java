package com.jhm69.money_tracker.ui.expenses;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ExpensesViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<ExpensesFragment> mFragmentList = new ArrayList<>();
    private List<String> pageTitles = new ArrayList<>();

    public ExpensesViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public ExpensesFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(ExpensesFragment fragment, String title) {
        mFragmentList.add(fragment);
        pageTitles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }

    public List<ExpensesFragment> getFragmentList() {
        return mFragmentList;
    }

}