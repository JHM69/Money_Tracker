package com.jhm69.money_tracker;

import android.app.Application;
import android.content.Context;


public class ExpenseTrackerApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

}
