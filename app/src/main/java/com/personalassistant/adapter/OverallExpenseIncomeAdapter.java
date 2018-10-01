package com.personalassistant.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.personalassistant.R;
import com.personalassistant.enities.dashboard.ExpenseDetailsObject;
import com.personalassistant.utils.BounceView;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;

import java.util.ArrayList;

import timber.log.Timber;

public class OverallExpenseIncomeAdapter extends RecyclerView.Adapter<OverallExpenseIncomeAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ExpenseDetailsObject> ExpenseDetailsObjectArrayList = new ArrayList<>();

    public OverallExpenseIncomeAdapter(Context mContext, ArrayList<ExpenseDetailsObject> mExpenseDetailsObjectArrayList) {
        this.mContext = mContext;
        this.ExpenseDetailsObjectArrayList = mExpenseDetailsObjectArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_over_all_income_expense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(holder, ExpenseDetailsObjectArrayList, position);
    }

    @Override
    public int getItemCount() {
        return ExpenseDetailsObjectArrayList.size();
    }

    public void onNotifyDataSetChanged(ArrayList<ExpenseDetailsObject> expenseList) {
        if (expenseList != null) {
            ExpenseDetailsObjectArrayList = new ArrayList<>();
            ExpenseDetailsObjectArrayList.addAll(expenseList);
            notifyDataSetChanged();
        } else {
            Timber.e("Adding empty list.");
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategoryNameTV, mCategoryFormattedPercentageTV, mCategoryFormattedAmount, mCategoriesViewTV;
        ViewHolder(View itemView) {
            super(itemView);
            mCategoryNameTV = itemView.findViewById(R.id.category_type_name);
            mCategoryFormattedPercentageTV = itemView.findViewById(R.id.over_all_category_percentage_formatted_tv);
            mCategoryFormattedAmount = itemView.findViewById(R.id.over_all_category_amount_formatted_tv);
            mCategoriesViewTV = itemView.findViewById(R.id.category_tv);
        }

        void bind(ViewHolder holder, final ArrayList<ExpenseDetailsObject> ExpenseDetailsObjectArrayList, final int position) {
            holder.mCategoryNameTV.setText(ExpenseDetailsObjectArrayList.get(position).getmTransactionType());
            holder.mCategoryFormattedPercentageTV.setText(ExpenseDetailsObjectArrayList.get(position).getmTransactionTypePercentageFormatted());
            holder.mCategoryFormattedAmount.setText(ExpenseDetailsObjectArrayList.get(position).getmTransactionTypeTotalFormatted());
            holder.mCategoriesViewTV.setText(ExpenseDetailsObjectArrayList.get(position).getmTransactionType().substring(0, 1));
            int color = Constants.EXPENSE_CATEGORIES_COLORS[Configuration.onGetIncomeCategoriesPosition(ExpenseDetailsObjectArrayList.get(position).getmTransactionType())];
            ((GradientDrawable) holder.mCategoriesViewTV.getBackground()).setColor(color);
            BounceView.addAnimTo(holder.mCategoriesViewTV);

        }


    }


}