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
import com.personalassistant.interfaces.GetAllIncomeInterfaces;
import com.personalassistant.utils.BounceView;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;

import java.util.ArrayList;

import timber.log.Timber;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<GetIncomeDetailsResult> incomeTransactionArrayList = new ArrayList<>();
    private GetAllIncomeInterfaces mGetAllIncomeInterfaces;

    public IncomeAdapter(Context mContext, ArrayList<GetIncomeDetailsResult> mIncomeTransactionArrayList, GetAllIncomeInterfaces getAllIncomeInterfaces) {

        this.mContext = mContext;
        this.incomeTransactionArrayList = mIncomeTransactionArrayList;
        this.mGetAllIncomeInterfaces = getAllIncomeInterfaces;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_get_all_income_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(holder, incomeTransactionArrayList, position);
    }

    @Override
    public int getItemCount() {
        return incomeTransactionArrayList.size();
    }

    public void onNotifyDataSetChanged(ArrayList<GetIncomeDetailsResult> expenseList) {
        if (expenseList != null) {
            incomeTransactionArrayList = new ArrayList<>();
            incomeTransactionArrayList.addAll(expenseList);
            notifyDataSetChanged();
        } else {
            Timber.e("Adding empty list.");
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategoriesViewTV, mIncomeCategoryNameTV, mIncomeCategoryFormattedAmount, mIncomeCategoryDescTV;
        private ImageView mIncomeItemDeleteIV;

        ViewHolder(View itemView) {
            super(itemView);
            mIncomeCategoryNameTV = itemView.findViewById(R.id.income_transaction_name_tv);
            mIncomeCategoryDescTV = itemView.findViewById(R.id.income_transaction_desc_name_tv);
            mIncomeCategoryFormattedAmount = itemView.findViewById(R.id.income_transaction_formatted_amount_name_tv);
            mCategoriesViewTV = itemView.findViewById(R.id.income_categories_tv);
            mIncomeItemDeleteIV = itemView.findViewById(R.id.income_categories_delete_iv);
        }

        void bind(ViewHolder holder, final ArrayList<GetIncomeDetailsResult> incomeTransactionArrayList, final int position) {
            holder.mIncomeCategoryNameTV.setText(incomeTransactionArrayList.get(position).getTransactionType());
            holder.mIncomeCategoryDescTV.setText(incomeTransactionArrayList.get(position).getTransactionDescription());
            holder.mIncomeCategoryFormattedAmount.setText(incomeTransactionArrayList.get(position).getTransactionAmountFormatted());
            int color = Constants.INCOME_COLORS[Configuration.onGetIncomeCategoriesPosition(incomeTransactionArrayList.get(position).getTransactionType())];
            holder.mCategoriesViewTV.setText(incomeTransactionArrayList.get(position).getTransactionType().substring(0, 1));
            ((GradientDrawable) holder.mCategoriesViewTV.getBackground()).setColor(color);
            BounceView.addAnimTo(holder.mCategoriesViewTV);
            holder.mIncomeItemDeleteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGetAllIncomeInterfaces.onSelectedIncome(incomeTransactionArrayList.get(position).getTransactionId(),position);
                }
            });
        }
    }
}