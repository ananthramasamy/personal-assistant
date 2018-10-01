package com.personalassistant.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.personalassistant.R;
import com.personalassistant.enities.income.GetIncomeDetailsResult;
import com.personalassistant.interfaces.GetAllExpenseInterfaces;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<GetIncomeDetailsResult> getExpenseArrayList = new ArrayList<>();
    private GetAllExpenseInterfaces mGetAllExpenseInterfaces;

    public ExpenseAdapter(Context mContext, ArrayList<GetIncomeDetailsResult> mIncomeTransactionArrayList, GetAllExpenseInterfaces getAllIncomeInterfaces) {
        this.mContext = mContext;
        this.getExpenseArrayList = mIncomeTransactionArrayList;
        this.mGetAllExpenseInterfaces = getAllIncomeInterfaces;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_expense_layout, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final ExpenseAdapter.ViewHolder holder, final int position) {
        holder.bind(holder, getExpenseArrayList, position);
    }

    @Override
    public int getItemCount() {
        return getExpenseArrayList.size();
    }

    public void onNotifyDataSetChanged(ArrayList<GetIncomeDetailsResult> expenseList) {
        if (expenseList != null) {
            getExpenseArrayList = new ArrayList<>();
            getExpenseArrayList.addAll(expenseList);
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mExpenseCategoryTypeTV, mExpenseCategoryNameTV, mExpenseCategoryFormattedAmount, mExpenseCategoryDescTV;
        private ImageView mExpenseRemoveTransactionIV;

        ViewHolder(View itemView) {
            super(itemView);
            mExpenseCategoryNameTV = itemView.findViewById(R.id.expense_category_type_tv);
            mExpenseCategoryFormattedAmount = itemView.findViewById(R.id.expense_category_total_amount_tv);
            mExpenseCategoryDescTV = itemView.findViewById(R.id.expense_category_transaction_details_tv);
            mExpenseRemoveTransactionIV = itemView.findViewById(R.id.expense_categories_delete_iv);
            mExpenseCategoryTypeTV = itemView.findViewById(R.id.category_tv);
        }

        void bind(ExpenseAdapter.ViewHolder holder, final ArrayList<GetIncomeDetailsResult> incomeTransactionArrayList, final int position) {
            holder.mExpenseCategoryNameTV.setText(incomeTransactionArrayList.get(position).getTransactionType());
            holder.mExpenseCategoryDescTV.setText(incomeTransactionArrayList.get(position).getTransactionDescription());
            holder.mExpenseCategoryFormattedAmount.setText(incomeTransactionArrayList.get(position).getTransactionAmountFormatted());
            holder.mExpenseCategoryTypeTV.setText(incomeTransactionArrayList.get(position).getTransactionType().substring(0, 1));
            int color = Constants.EXPENSE_CATEGORIES_COLORS[Configuration.onGetExpenseCategoriesPosition(incomeTransactionArrayList.get(position).getTransactionType())];
            ((GradientDrawable) holder.mExpenseCategoryTypeTV.getBackground()).setColor(color);
            holder.mExpenseRemoveTransactionIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGetAllExpenseInterfaces.onSelectedIncome(incomeTransactionArrayList.get(position).getTransactionId(),position);
                }
            });

        }
    }
}