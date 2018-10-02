package com.personalassistant.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.personalassistant.MyApplication;
import com.personalassistant.R;
import com.personalassistant.adapter.OverallExpenseIncomeAdapter;
import com.personalassistant.endpoint.EndPoints;
import com.personalassistant.endpoint.JsonRequest;
import com.personalassistant.enities.dashboard.ExpenseDetailsObject;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;
import com.personalassistant.utils.DividerVerticalItemDecoration;
import com.personalassistant.utils.JsonUtils;
import com.suke.widget.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class InComeExpenseSummaryFragment extends Fragment {
    private Context mContext;
    private RecyclerView mIncomeExpenseRecyclerView;
    private OverallExpenseIncomeAdapter mOverallExpenseIncomeAdapter;
    private ArrayList<ExpenseDetailsObject> ExpenseDetailsObjectArrayList = new ArrayList<>();
    private TextView mEmptyViewTV, mGraphHeaderTV;
    private String mActualOverallIncomeFormatted, mActualOverallExpenseFormatted, mGoalsOverallIncomeFormatted, mGoalsOverallExpenseFormatted,mTransactionStatus;
    private TextView mOverallGoalsIncomeTV, mOverallGoalsExpenseTV, mOverallActualIncomeTV, mOverActualExpenseTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overall_expense_income_details_layout, container, false);
        android.support.v7.widget.Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        mContext = getActivity();
         mTransactionStatus=getString(R.string.str_income);
        initViews(rootView);
        onPrepareOverAllResponse();
        return rootView;
    }


    private void initViews(View rootView) {
        mGraphHeaderTV = rootView.findViewById(R.id.graph_header_tv);
        mEmptyViewTV = rootView.findViewById(R.id.no_data_available_tv);
        mOverallGoalsIncomeTV = rootView.findViewById(R.id.goal_income_value_tv);
        mOverallGoalsExpenseTV = rootView.findViewById(R.id.goals_expense_value_tv);
        mOverallActualIncomeTV = rootView.findViewById(R.id.actual_income_value_tv);
        mOverActualExpenseTV = rootView.findViewById(R.id.actual_expense_value_tv);
        TextView mMonthNameTV = rootView.findViewById(R.id.month_name_tv);
        TextView mSelectedYearTV = rootView.findViewById(R.id.year_tv);
        if (!Configuration.isEmptyValidation(MyApplication.getYear())) {
            mSelectedYearTV.setText(MyApplication.getYear());
        }
        if (!Configuration.isEmptyValidation(MyApplication.getMonthName())) {
            mMonthNameTV.setText(MyApplication.getMonthName());
        }
        SwitchButton switchButton = rootView.findViewById(R.id.switch_button);
        switchButton.isChecked();
        switchButton.setShadowEffect(true);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    mTransactionStatus = getString(R.string.str_expense);
                } else {
                    mTransactionStatus = getString(R.string.str_income);
                }
                mGraphHeaderTV.setText(mTransactionStatus);
                onPrepareOverAllResponse();
            }
        });
        mIncomeExpenseRecyclerView = rootView.findViewById(R.id.over_all_income_expense_recycler_view);
        ExpenseDetailsObjectArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mIncomeExpenseRecyclerView.getContext());
        mIncomeExpenseRecyclerView.setLayoutManager(layoutManager);
        mIncomeExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mIncomeExpenseRecyclerView.setHasFixedSize(true);
        mOverallExpenseIncomeAdapter = new OverallExpenseIncomeAdapter(mTransactionStatus,mContext, ExpenseDetailsObjectArrayList);
        mIncomeExpenseRecyclerView.setAdapter(mOverallExpenseIncomeAdapter);
        Configuration.runLayoutAnimation(mIncomeExpenseRecyclerView);

    }

    private void onPrepareOverAllResponse() {
        if (Configuration.isConnected(getActivity())) {
            Configuration.onAnimatedLoadingShow(mContext, getString(R.string.str_fetching));
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestToCommonTransactionJsonObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest AddIncomeRequest = new JsonRequest(Request.Method.POST, EndPoints.GET_DASHBOARD_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onPrepareToParser(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onFailureResponse(error);
                }
            });
            AddIncomeRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            AddIncomeRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(AddIncomeRequest, Constants.GET_INCOME_REQUESTS_TAG);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));
        }
    }

    private void onPrepareToParser(JSONObject response) {
        try {
            ExpenseDetailsObjectArrayList = new ArrayList<>();
            JSONObject mGoalsJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_GOALS_TAG);
            mGoalsOverallIncomeFormatted = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_FORMATTED_TAG);
            mGoalsOverallExpenseFormatted = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_FORMATTED_TAG);
            JSONObject mActualJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_ACTUALS_TAG);
            mActualOverallIncomeFormatted = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_FORMATTED_TAG);
            mActualOverallExpenseFormatted = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_FORMATTED_TAG);
            JSONArray responseJSONArray;
            if (TextUtils.equals(mTransactionStatus, getString(R.string.str_expense))) {
                responseJSONArray = response.getJSONArray(Constants.DASHBOARD_TRANSACTION_EXPENSE_DETAILS_TAG);
            } else {
                responseJSONArray = response.getJSONArray(Constants.DASHBOARD_TRANSACTION_INCOME_DETAILS_TAG);
            }
            if (responseJSONArray.length() > 0) {
                for (int i = 0; i < responseJSONArray.length(); i++) {
                    JSONObject ExpenseInnerObject = responseJSONArray.getJSONObject(i);
                    String mTempTransactionType = ExpenseInnerObject.getString(Constants.DASHBOARD_TRANSACTION_TYPE_TAG);
                    String mTempTransactionTypePercentage = ExpenseInnerObject.getString(Constants.DASHBOARD_TRANSACTION_TYPE_PERCENTAGE_TAG);
                    String mTempTransactionTypeTotal = ExpenseInnerObject.getString(Constants.DASHBOARD_TRANSACTION_TYPE_TOTAL_TAG);
                    String mTempTransactionTypeTotalPercentageFormatted = ExpenseInnerObject.getString(Constants.DASHBOARD_TRANSACTION_TYPE_PERCENTAGE_FORMATTED_TAG);
                    String mTempTransactionTypeTotalFormatted = ExpenseInnerObject.getString(Constants.DASHBOARD_TRANSACTION_TYPE_TOTAL_FORMATTED_TAG);
                    ExpenseDetailsObjectArrayList.add(new ExpenseDetailsObject(mTempTransactionType, mTempTransactionTypePercentage,
                            mTempTransactionTypeTotal, mTempTransactionTypeTotalPercentageFormatted, mTempTransactionTypeTotalFormatted));
                }
                mIncomeExpenseRecyclerView.setVisibility(View.VISIBLE);
                mEmptyViewTV.setVisibility(View.GONE);
                mOverallExpenseIncomeAdapter.onNotifyDataSetChanged(mTransactionStatus,ExpenseDetailsObjectArrayList);
                Configuration.runLayoutAnimation(mIncomeExpenseRecyclerView);
            } else {
                mIncomeExpenseRecyclerView.setVisibility(View.GONE);
                mEmptyViewTV.setVisibility(View.VISIBLE);
            }
            onSetDashboardResult();
            Configuration.onAnimatedLoadingDismiss();


        } catch (Exception e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }
    }

    private void onFailureResponse(VolleyError error) {
        String errorData = new String(error.networkResponse.data);
        try {
            JSONObject jsonObject = new JSONObject(errorData);
            String mMessage = jsonObject.getString("message");
            Configuration.onAnimatedLoadingDismiss();
            Configuration.onWarningAlertDialog(mContext, getString(R.string.str_alert), mMessage);
        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }
    }

    private void onSetDashboardResult() {
        mOverallGoalsIncomeTV.setText(mGoalsOverallIncomeFormatted);
        mOverallGoalsExpenseTV.setText(mGoalsOverallExpenseFormatted);
        mOverallActualIncomeTV.setText(mActualOverallIncomeFormatted);
        mOverActualExpenseTV.setText(mActualOverallExpenseFormatted);

    }
}
