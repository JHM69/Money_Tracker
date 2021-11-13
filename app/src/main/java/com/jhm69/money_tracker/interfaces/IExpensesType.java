package com.jhm69.money_tracker.interfaces;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({IExpensesType.MODE_EXPENSES})
@Retention(RetentionPolicy.SOURCE)
public @interface IExpensesType{
    int MODE_EXPENSES = 0;
}

