package com.jhm69.money_tracker.interfaces;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@IntDef({IUserActionsMode.MODE_CREATE, IUserActionsMode.MODE_UPDATE})
@Retention(RetentionPolicy.SOURCE)
public @interface IUserActionsMode{
    int MODE_CREATE = 0;
    int MODE_UPDATE = 1;
    String MODE_TAG = "_user_action_mode";
}
