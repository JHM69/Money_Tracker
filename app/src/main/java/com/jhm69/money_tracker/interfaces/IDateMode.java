package com.jhm69.money_tracker.interfaces;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({IDateMode.MODE_TODAY, IDateMode.MODE_WEEK, IDateMode.MODE_MONTH})
@Retention(RetentionPolicy.SOURCE)
public @interface IDateMode {
    int MODE_TODAY = 100;
    int MODE_WEEK = 101;
    int MODE_MONTH = 102;
    String DATE_TODAY_TAG = "_today";
    String DATE_WEEK_TAG = "_week";
    String DATE_MONTH_TAG = "_month";
    String DATE_MODE_TAG = "_date_user_mode";
}

