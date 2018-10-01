package com.personalassistant.views.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.personalassistant.R;
import com.personalassistant.utils.Constants;
import com.personalassistant.views.fragments.ExpenseDetailsFragment;
import com.personalassistant.views.fragments.InComeDetailsFragment;
import com.personalassistant.views.fragments.InComeExpenseSummaryFragment;

public class BudgetDetailsActivity extends AppCompatActivity {
    Bundle args = new Bundle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBackgroundColor();
        setContentView(R.layout.activity_budget_details_layout);
        try {
            if (getIntent() != null) {
                String mFragmentName = getIntent().getStringExtra(Constants.BUDGET_FRAGMENT_NAME);
                onSelectedFragment(mFragmentName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBackgroundColor() {
        try {
            View decor = getWindow().getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.md_white_1000));
            } else {
                decor.setSystemUiVisibility(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSelectedFragment(String mFragmentName) {
        Fragment fragment = new Fragment();
        try {
            if (TextUtils.equals(mFragmentName, Constants.INCOME_DETAILS_FRAGMENT_NAME)) {
                fragment = new InComeDetailsFragment();
            } else if (TextUtils.equals(mFragmentName, Constants.EXPENSE_DETAILS_FRAGMENT_NAME)) {
                fragment = new ExpenseDetailsFragment();
            } else if (TextUtils.equals(mFragmentName, Constants.INCOME_EXPENSE_OVER_ALL_DETAILS_FRAGMENT_NAME)) {
                fragment = new InComeExpenseSummaryFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
