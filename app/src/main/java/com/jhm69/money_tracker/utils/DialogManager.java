package com.jhm69.money_tracker.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.ArrayRes;
import com.google.android.material.snackbar.Snackbar;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.jhm69.money_tracker.ExpenseTrackerApp;
import com.jhm69.money_tracker.R;

import java.util.Calendar;
import java.util.Date;


public class DialogManager {

    private static DialogManager ourInstance = new DialogManager();

    public static DialogManager getInstance() {
        return ourInstance;
    }

    private DialogManager() {
    }

    public AlertDialog createEditTextDialog(Activity activity, String title, String confirmText, String negativeText, final DialogInterface.OnClickListener listener) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.layout_dialog_edit_text, null);
        return createAlertDialog(activity, title, dialogLayout, null, confirmText, negativeText, listener);
    }

    public void createCustomAcceptDialog(Activity activity, String title, String message, String confirmText, String negativeText, final DialogInterface.OnClickListener listener) {
        createAlertDialog(activity, title, null, message, confirmText, negativeText, listener);
    }

    public void createSinglePickDialog(Activity activity, String title, @ArrayRes int arrayId, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setItems(arrayId, listener);
        builder.create().show();
    }

    public void createTimePickerDialog(Activity activity, int hour, int minute, TimePickerDialog.OnTimeSetListener listener) {
        new TimePickerDialog(activity, listener, hour, minute, DateFormat.is24HourFormat(activity)).show();
    }

    private AlertDialog createAlertDialog(Activity activity, String title, View dialogLayout, String message, String confirmText, String negativeText, final DialogInterface.OnClickListener listener) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.DialogTheme);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctw);
        dialogBuilder.setTitle(title);
        if (dialogLayout != null) dialogBuilder.setView(dialogLayout);
        if (message != null) dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(confirmText, listener);
        dialogBuilder.setNegativeButton(negativeText, listener);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        return alertDialog;
    }

    public void showShortSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showShortToast(String message) {
        Toast.makeText(ExpenseTrackerApp.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener dateSetListener, Calendar calendar) {
        showDatePicker(context, dateSetListener, calendar,null, null);
    }

    public void showDatePicker(Context context, DatePickerDialog.OnDateSetListener dateSetListener, Calendar calendar, Date minDate, Date maxDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.DialogTheme, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (minDate != null) datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        if (maxDate != null) datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        datePickerDialog.show();
    }
}
