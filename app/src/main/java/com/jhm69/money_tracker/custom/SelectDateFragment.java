package com.jhm69.money_tracker.custom;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.interfaces.ISelectDateFragment;
import com.jhm69.money_tracker.utils.DateManager;
import com.jhm69.money_tracker.utils.DateUtils;
import com.jhm69.money_tracker.utils.DialogManager;
import com.jhm69.money_tracker.utils.Util;

import java.util.Calendar;
import java.util.Date;


public class SelectDateFragment extends Fragment implements View.OnClickListener{

    private Button btnDateFrom;
    private Button btnDateTo;
    private TextView tvTotal;

    private ISelectDateFragment iSelectDateFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_date_range, container, false);
        btnDateFrom = (Button)rootView.findViewById(R.id.btn_date_from);
        btnDateTo = (Button)rootView.findViewById(R.id.btn_date_to);
        tvTotal = (TextView)rootView.findViewById(R.id.tv_amount);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnDateFrom.setOnClickListener(this);
        btnDateTo.setOnClickListener(this);
        updateDate(btnDateFrom, DateManager.getInstance().getDateFrom());
        updateDate(btnDateTo, DateManager.getInstance().getDateTo());
        iSelectDateFragment.updateData();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_date_from || v.getId() == R.id.btn_date_to) {
            showDateDialog(v.getId());
        }
    }

    private void showDateDialog(final int id) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(id == R.id.btn_date_from ? DateManager.getInstance().getDateFrom() : DateManager.getInstance().getDateTo());
        DialogManager.getInstance()
                .showDatePicker(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                DateUtils.setDateStartOfDay(calendar);
                                if (id == R.id.btn_date_from) {
                                    DateManager.getInstance().setDateFrom(calendar.getTime());
                                    updateDate(btnDateFrom, DateManager.getInstance().getDateFrom());
                                } else {
                                    DateManager.getInstance().setDateTo(calendar.getTime());
                                    updateDate(btnDateTo, DateManager.getInstance().getDateTo());
                                }
                                iSelectDateFragment.updateData();
                            }
                        },
                        calendar,
                        (R.id.btn_date_from == id) ? null : DateManager.getInstance().getDateFrom(),
                        (R.id.btn_date_from == id) ? DateManager.getInstance().getDateTo() : null);
    }

    private void updateDate(Button btn, Date date) {
        btn.setText(Util.formatDateToString(date, Util.getCurrentDateFormat()));
    }

    public TextView getTextViewTotal() {
        return tvTotal;
    }

    public void setSelectDateFragment(ISelectDateFragment iSelectDateFragment) {
        this.iSelectDateFragment = iSelectDateFragment;
    }

}
