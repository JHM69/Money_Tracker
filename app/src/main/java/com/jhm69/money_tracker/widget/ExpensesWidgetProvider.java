package com.jhm69.money_tracker.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.entities.Expense;
import com.jhm69.money_tracker.interfaces.IDateMode;
import com.jhm69.money_tracker.ui.MainActivity;
import com.jhm69.money_tracker.ui.expenses.ExpenseDetailActivity;
import com.jhm69.money_tracker.utils.Util;


public class ExpensesWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds){
            Intent expenseDetailActivity = new Intent(context, ExpenseDetailActivity.class);
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            // Creating stack for builder item click
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addParentStack(MainActivity.class);
            taskStackBuilder.addNextIntent(mainActivityIntent);
            taskStackBuilder.addNextIntent(expenseDetailActivity);
            PendingIntent itemClickPendingIntent = taskStackBuilder.getPendingIntent(0 ,PendingIntent.FLAG_UPDATE_CURRENT);

            // Create pending intent for clicking the whole view, not an item
            PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent widgetServiceIntent = new Intent(context, ExpensesWidgetService.class);
//            widgetServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            widgetServiceIntent.setData(Uri.parse(widgetServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteView.setOnClickPendingIntent(R.id.general_layout, mainPendingIntent);

            remoteView.setEmptyView(R.id.listViewWidget, R.id.empty_view);
            remoteView.setRemoteAdapter(appWidgetId, R.id.listViewWidget, widgetServiceIntent);
            remoteView.setTextViewText(R.id.tv_amount, context.getString(R.string.today_expenses_total, Util.getFormattedCurrency(Expense.getTotalExpensesByDateMode(IDateMode.MODE_TODAY))));
            remoteView.setPendingIntentTemplate(R.id.listViewWidget, itemClickPendingIntent);
            ComponentName component=new ComponentName(context, ExpensesWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
            appWidgetManager.updateAppWidget(component, remoteView);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ExpensesWidgetService.UPDATE_WIDGET)) {
            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            int[] ids = gm.getAppWidgetIds(new ComponentName(context, ExpensesWidgetProvider.class));
            this.onUpdate(context, gm, ids);
        }
    }
}
