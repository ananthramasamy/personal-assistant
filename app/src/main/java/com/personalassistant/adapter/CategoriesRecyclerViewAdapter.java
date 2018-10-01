package com.personalassistant.adapter;

import android.annotation.SuppressLint;
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
import com.personalassistant.interfaces.CategoriesInterfaces;
import com.personalassistant.utils.BounceView;

import java.util.ArrayList;

public class CategoriesRecyclerViewAdapter extends RecyclerView.Adapter<CategoriesRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> categoriesModelArrayList=new ArrayList<>();
    private CategoriesInterfaces categoriesInterfaces;
    private int lastCheckedPosition = -1;
    private int[] CategoriesColors;

    public CategoriesRecyclerViewAdapter(Context mContext, int[] CategoriesColors, ArrayList<String> mCategoriesModelArrayList, CategoriesInterfaces mCategoriesInterfaces) {
        super();
        this.mContext = mContext;
        this.categoriesModelArrayList = mCategoriesModelArrayList;
        this.categoriesInterfaces = mCategoriesInterfaces;
        this.CategoriesColors = CategoriesColors;
    }

    @NonNull
    @Override
    public CategoriesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_categories, parent, false);
        return new CategoriesRecyclerViewAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull final CategoriesRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        try {
            holder.mCategoriesBackgroundTV.setText(categoriesModelArrayList.get(position).substring(0, 1));
            holder.mCategoriesNameTV.setText(categoriesModelArrayList.get(position));
            int color = CategoriesColors[position];
            ((GradientDrawable) holder.mCategoriesBackgroundTV.getBackground()).setColor(color);
            BounceView.addAnimTo(holder.mCategoriesBackgroundTV);

            holder.mCategoriesBackgroundTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lastCheckedPosition = position;
                    notifyDataSetChanged();
                    categoriesInterfaces.onSelectedItem(position, categoriesModelArrayList.get(position));
                }
            });

            if (lastCheckedPosition == position) {
                holder.mSelectedIV.setVisibility(View.VISIBLE);
            } else {
                holder.mSelectedIV.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void onNotifyDataSetChanged(ArrayList<String> expenseList) {
        if (expenseList != null) {
            categoriesModelArrayList = new ArrayList<>();
            categoriesModelArrayList.addAll(expenseList);
            lastCheckedPosition = -1;
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemCount() {
        return categoriesModelArrayList.size();
    }

      class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCategoriesBackgroundTV, mCategoriesNameTV;
        private ImageView mSelectedIV;

          ViewHolder(View view) {
            super(view);
            mCategoriesBackgroundTV = itemView.findViewById(R.id.category_background_tv);
            mCategoriesNameTV = itemView.findViewById(R.id.category_tv);
            mSelectedIV = itemView.findViewById(R.id.checker_iv);
        }
    }
}

