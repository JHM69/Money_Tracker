package com.jhm69.money_tracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhm69.money_tracker.ExpenseTrackerApp;
import com.jhm69.money_tracker.R;
import com.jhm69.money_tracker.custom.BaseViewHolder;
import com.jhm69.money_tracker.entities.Category;
import com.jhm69.money_tracker.interfaces.IIncomesType;

import java.util.List;


public class CategoriesAdapter extends BaseRecyclerViewAdapter<CategoriesAdapter.CategoryViewHolder> {

    private List<Category> mCategoryList;
    private int lastPosition = -1;
    private final BaseViewHolder.RecyclerClickListener onRecyclerClickListener;

    public static class CategoryViewHolder extends BaseViewHolder {

        public TextView tvTitle;
        public ImageView income, expense;

        public CategoryViewHolder(View v, RecyclerClickListener onRecyclerClickListener) {
            super(v, onRecyclerClickListener);
            tvTitle = v.findViewById(R.id.tv_title);
            income = v.findViewById(R.id.income);
            expense = v.findViewById(R.id.expense);
        }

    }

    public CategoriesAdapter(List<Category> categoryList, BaseViewHolder.RecyclerClickListener recyclerClickListener) {
        this.mCategoryList = categoryList;
        this.onRecyclerClickListener = recyclerClickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_category_item, parent, false);
        return new CategoryViewHolder(v, onRecyclerClickListener);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.CategoryViewHolder holder, int position) {
        final Category category = mCategoryList.get(position);
        holder.tvTitle.setText(category.getName());
        holder.itemView.setTag(category);
        if(category.getType()== IIncomesType.MODE_INCOME){
            holder.income.setVisibility(View.VISIBLE);
        }else{
            holder.expense.setVisibility(View.VISIBLE);
        }
        holder.itemView.setSelected(isSelected(position));
        setAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void updateCategories(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    private void setAnimation(CategoryViewHolder holder, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ExpenseTrackerApp.getContext(), R.anim.push_left_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

}