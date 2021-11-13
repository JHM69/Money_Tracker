package com.jhm69.money_tracker.interfaces;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;


public interface IFragmentListener {

    void replaceFragment(Fragment fragment, boolean addToBackStack);
    void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack);
    void setResultWithData(int status, Intent intent);
    void setToolbar(Toolbar toolbar);
    void closeActivity();

}
