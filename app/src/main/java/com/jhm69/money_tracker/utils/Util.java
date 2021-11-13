package com.jhm69.money_tracker.utils;

import android.preference.PreferenceManager;
import android.widget.EditText;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.jhm69.money_tracker.ExpenseTrackerApp;
import com.jhm69.money_tracker.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Util {

    public static String formatDateToString(Date date, String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(date);
    }

    public static boolean isEmptyField(EditText et) {
        return (et.getText() == null || et.getText().toString().isEmpty());
    }

    public static List<Integer> getListColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        return colors;
    }

    public static String getFormattedCurrency(float number) {
        //String countryCode = PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getString(ExpenseTrackerApp.getContext().getString(R.string.pref_country_key), ExpenseTrackerApp.getContext().getString(R.string.default_country));
        Locale locale = new Locale("EN", "us");
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        String formattedNumber = numberFormat.format(number);
        String symbol = numberFormat.getCurrency().getSymbol(locale);
        return formattedNumber.replace(symbol, " ৳");
    }

    public static String getCurrentDateFormat() {
        return PreferenceManager.getDefaultSharedPreferences(ExpenseTrackerApp.getContext()).getString(ExpenseTrackerApp.getContext().getString(R.string.date_format_key), ExpenseTrackerApp.getContext().getString(R.string.default_date_format));
    }

}
