package com.personalassistant.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.personalassistant.MyApplication;
import com.personalassistant.R;
import com.personalassistant.adapter.MonthScrollViewAdapter;
import com.personalassistant.endpoint.EndPoints;
import com.personalassistant.endpoint.JsonRequest;
import com.personalassistant.enities.dashboard.ExpenseDetailsObject;
import com.personalassistant.utils.BottomNavigationViewHelper;
import com.personalassistant.utils.BounceView;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;
import com.personalassistant.utils.JsonUtils;
import com.personalassistant.utils.Toasty;
import com.suke.widget.SwitchButton;
import com.whiteelephant.monthpicker.MonthPickerDialog;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>, View.OnClickListener {

    private Context mContext;
    private TextView mYearTV;
    private ArrayList<String> monthList;
    private PieChart mChart;
    ArrayList<Integer> colors = new ArrayList<>();
    private ArrayList<ExpenseDetailsObject> ExpenseDetailsObjectArrayList = new ArrayList<>();
    BottomNavigationView bottomNavigationView;
    private String mGoalsOverallIncome, mGoalsOverallExpense, mGoalsOverallIncomeFormatted, mGoalsOverallExpenseFormatted;
    private String mActualOverallIncome, mActualOverallExpense, mActualOverallIncomeFormatted, mActualOverallExpenseFormatted;
    private TextView mOverallGoalsIncomeTV, mOverallGoalsExpenseTV, mOverallActualIncomeTV, mOverActualExpenseTV, mGraphHeaderTV, mGraphEmptyContentTV;
    private String mGoalIncome, mGoalExpense;
    private EditText mGoalIncomeET, mGoalExpenseET;
    private TextInputLayout mGoalIncomeWrapper, mGoalExpenseWrapper;
    private String mGraphDrawEnabled, mTransactionStatus ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = new Intent();
            switch (item.getItemId()) {
                case R.id.action_bottom_bar_home:
                    return true;
                case R.id.action_bottom_bar_income:
                    intent.putExtra(Constants.BUDGET_FRAGMENT_NAME, Constants.INCOME_DETAILS_FRAGMENT_NAME);
                    startActivityForResult(new Intent(MainActivity.this, BudgetDetailsActivity.class).putExtras(intent), Constants.INCOME_TRANSACTION_REQUEST_CODE);
                    return true;
                case R.id.action_bottom_bar_expense:
                    intent.putExtra(Constants.BUDGET_FRAGMENT_NAME, Constants.EXPENSE_DETAILS_FRAGMENT_NAME);
                    startActivityForResult(new Intent(MainActivity.this, BudgetDetailsActivity.class).putExtras(intent), Constants.EXPENSE_TRANSACTION_REQUEST_CODE);
                    return true;
                case R.id.action_bottom_bar_contact:
                    intent.putExtra(Constants.BUDGET_FRAGMENT_NAME, Constants.INCOME_EXPENSE_OVER_ALL_DETAILS_FRAGMENT_NAME);
                    startActivityForResult(new Intent(MainActivity.this, BudgetDetailsActivity.class).putExtras(intent), Constants.EXPENSE_TRANSACTION_REQUEST_CODE);
                    return true;
            }

            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        mTransactionStatus = getString(R.string.str_income);
        initViews();
        onPrePareDashboard();
    }


    private void initViews() {
        mYearTV = findViewById(R.id.year_tv);
        BounceView.addAnimTo(mYearTV);
        MyApplication.setYear("");
        MyApplication.setYear(String.valueOf(mYearTV.getText().toString()));
        DiscreteScrollView mMonthScrollView = findViewById(R.id.month_picker);
        ImageView mLogoutIV = findViewById(R.id.ic_logout_iv);
        mLogoutIV.setOnClickListener(this);
        BounceView.addAnimTo(mLogoutIV);
        mOverallActualIncomeTV = findViewById(R.id.over_all_actual_income_tv);
        mOverActualExpenseTV = findViewById(R.id.over_all_actual_expense_tv);
        mOverallGoalsIncomeTV = findViewById(R.id.over_all_income_goal_value_tv);
        mOverallGoalsExpenseTV = findViewById(R.id.over_all_expense_goal_value_tv);
        bottomNavigationView = findViewById(R.id.navigation);
        ImageButton mIncomeViewEnableBTN = findViewById(R.id.actual_income_expense_edit_iv);
        mYearTV.setOnClickListener(this);
        mMonthScrollView.setSlideOnFling(true);
        BounceView.addAnimTo(mIncomeViewEnableBTN);
        mIncomeViewEnableBTN.setOnClickListener(this);
        monthList = new ArrayList<>();
        monthList.add("January");
        monthList.add("February");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");
        mMonthScrollView.setAdapter(new MonthScrollViewAdapter(monthList));
        mMonthScrollView.addOnItemChangedListener(this);
        mMonthScrollView.addScrollStateChangeListener(this);
        mMonthScrollView.scrollToPosition(9);
        MyApplication.setMonth(9);
        MyApplication.setMonthName("October");
        mMonthScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.7f)
                .build());
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        mChart = findViewById(R.id.chart_view);
        mGraphHeaderTV = findViewById(R.id.graph_header_tv);
        mGraphEmptyContentTV = findViewById(R.id.no_data_available_tv);
        mGraphEmptyContentTV.setVisibility(View.GONE);
        SwitchButton switchButton = findViewById(R.id.switch_button);
        switchButton.isChecked();
        switchButton.setShadowEffect(true);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    mTransactionStatus = getString(R.string.str_expense);
                    mGraphDrawEnabled = getString(R.string.str_expense);
                } else {
                    mTransactionStatus = getString(R.string.str_income);
                    mGraphDrawEnabled = getString(R.string.str_income);
                }
                mGraphHeaderTV.setText(mGraphDrawEnabled);
                onPrePareDashboard();
            }
        });


    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder holder, int position) {
        if (holder != null) {
            MyApplication.setMonth(Configuration.onGetMonthPosition(monthList.get(position)));
            MyApplication.setMonthName(monthList.get(position));
            onPrePareDashboard();
        }
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float position, int currentIndex, int newIndex, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actual_income_expense_edit_iv:
                onUpdateIncomeExpenseGoal(mContext);
                break;
            case R.id.year_tv:
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        mYearTV.setText(Integer.toString(selectedYear));
                        MyApplication.setYear("");
                        MyApplication.setYear(String.valueOf(selectedYear));
                        onPrePareDashboard();
                    }
                }, 2018, 0);
                builder.showYearOnly()
                        .setYearRange(1990, 2030)
                        .build()
                        .show();

                break;
            case R.id.ic_logout_iv:
                onUserConfirmationWarningAlert(mContext);
                break;

        }

    }

    private void onUpdateGoalRequest(String mGoalIncome, String mGoalExpense) {
        if (Configuration.isConnected(MainActivity.this)) {
            Configuration.onAnimatedLoadingShow(mContext, getString(R.string.str_loading));
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.UpdateGoalsJsonObject(mGoalIncome, mGoalExpense);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest DeleteTransactionRequest = new JsonRequest(Request.Method.POST, EndPoints.PUT_GOALS_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onGoalUpdateResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onFailureResponse(error);
                }
            });
            DeleteTransactionRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            DeleteTransactionRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(DeleteTransactionRequest, Constants.EXPENSE_GOAL);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));
        }

    }

    private void onGoalUpdateResponse(JSONObject response) {
        try {
            String mMessage = response.getString(Constants.MESSAGE_TAG);
            Toasty.success(mContext, mMessage, 100, true).show();
            Configuration.onAnimatedLoadingDismiss();
            onPrePareDashboard();
        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }

    }

    @Override
    public void onBackPressed() {

        onUserConfirmationWarningAlert(mContext);
    }

    private void onPrePareDashboard() {
        if (Configuration.isConnected(MainActivity.this)) {
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

    private void onPrepareToParser(JSONObject response) {
        try {
            ExpenseDetailsObjectArrayList = new ArrayList<>();
            JSONObject mGoalsJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_GOALS_TAG);
            mGoalsOverallIncome = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_TAG);
            mGoalsOverallExpense = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_TAG);
            mGoalsOverallIncomeFormatted = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_FORMATTED_TAG);
            mGoalsOverallExpenseFormatted = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_FORMATTED_TAG);
            JSONObject mActualJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_ACTUALS_TAG);
            mActualOverallIncome = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_TAG);
            mActualOverallExpense = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_TAG);
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
                mChart.setVisibility(View.VISIBLE);
                mGraphEmptyContentTV.setVisibility(View.GONE);
                onDrawPieChart();
            } else {
                mChart.setVisibility(View.GONE);
                mGraphEmptyContentTV.setVisibility(View.VISIBLE);
            }
            Configuration.onAnimatedLoadingDismiss();
            onSetDashboardResult();

        } catch (Exception e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }
    }

    private void onSetDashboardResult() {
        mOverallGoalsExpenseTV.setText(mGoalsOverallExpenseFormatted);
        mOverallGoalsIncomeTV.setText(mGoalsOverallIncomeFormatted);
        mOverActualExpenseTV.setText(mActualOverallExpenseFormatted);
        mOverallActualIncomeTV.setText(mActualOverallIncomeFormatted);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onPrePareDashboard();
        bottomNavigationView.setSelectedItemId(R.id.action_bottom_bar_home);
    }

    private void onUpdateIncomeExpenseGoal(Context context) {
        try {

            final Dialog dialog = new Dialog(context, R.style.AppCompatAlertDialogStyle);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_goal_layout);

            mGoalIncomeET = dialog.findViewById(R.id.goal_income_et);
            mGoalExpenseET = dialog.findViewById(R.id.goal_expense_et);
            mGoalIncomeWrapper = dialog.findViewById(R.id.goal_income_wrapper);
            mGoalExpenseWrapper = dialog.findViewById(R.id.goal_expense_wrapper);
            Button mSubmitGoalBTN = dialog.findViewById(R.id.submit_goals_btn);
            ImageView mCloseBTN = dialog.findViewById(R.id.close_btn);
            mGoalIncomeET.setText(mGoalsOverallIncome);
            mGoalExpenseET.setText(mGoalsOverallExpense);
            mSubmitGoalBTN.setOnClickListener(this);
            Configuration.ResetTextInputLayout(mContext, mGoalIncomeWrapper, mGoalIncomeET);
            Configuration.ResetTextInputLayout(mContext, mGoalExpenseWrapper, mGoalExpenseET);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            mSubmitGoalBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoalIncome = mGoalIncomeET.getText().toString().trim();
                    mGoalExpense = mGoalExpenseET.getText().toString().trim();

                    if (Configuration.isEmptyValidation(mGoalIncome)) {
                        mGoalIncomeWrapper.setError("Invalid Income");
                    } else if (Configuration.isEmptyValidation(mGoalExpense)) {
                        mGoalExpenseWrapper.setError("Invalid Expense");
                    } else {
                        dialog.dismiss();
                        onUpdateGoalRequest(mGoalIncome, mGoalExpense);
                    }
                }
            });
            mCloseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            BounceView.addAnimTo(dialog);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void onUserConfirmationWarningAlert(final Context mContext) {
        final Dialog WarningAlertDialog;
        try {
            WarningAlertDialog = new Dialog(mContext);
            WarningAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WarningAlertDialog.setContentView(R.layout.dialog_warning_confirmation_layout);
            TextView WarningAlertTittleTV = WarningAlertDialog.findViewById(R.id.warning_tittle_tv);
            TextView WarningAlertMsgTV = WarningAlertDialog.findViewById(R.id.warning_msg_tv);
            Button mPositiveActionBTN = WarningAlertDialog.findViewById(R.id.positive_action_btn);
            mPositiveActionBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WarningAlertDialog.dismiss();
                    finish();
                    startActivity(new Intent(mContext, LoginManagementActivity.class));
                    overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out);
                }
            });
            Button CloseBTN = WarningAlertDialog.findViewById(R.id.negative_action_btn);
            Objects.requireNonNull(WarningAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WarningAlertDialog.setCancelable(false);
            WarningAlertDialog.setCanceledOnTouchOutside(false);
            WarningAlertMsgTV.setText("Do You wish to logout?");
            WarningAlertTittleTV.setText("Alert");
            CloseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WarningAlertDialog.dismiss();
                }
            });
            WarningAlertDialog.show();
            BounceView.addAnimTo(WarningAlertDialog);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onDrawPieChart() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(60f);
        mChart.setTransparentCircleRadius(100f);
        mChart.setDrawCenterText(false);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.getLegend().setWordWrapEnabled(true);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(1f);
        //l.setYOffset(0f);
        mChart.setDrawEntryLabels(false);
        //mChart.setEntryLabelColor(Color.TRANSPARENT);
       // mChart.setEntryLabelTextSize(12f);
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < ExpenseDetailsObjectArrayList.size(); i++) {
            entries.add(new PieEntry(Float.parseFloat(ExpenseDetailsObjectArrayList.get(i).getmTransactionTypeTotal()), ExpenseDetailsObjectArrayList.get(i).getmTransactionType()));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(2f);
        dataSet.setIconsOffset(new MPPointF(0, 60));
        dataSet.setSelectionShift(5f);
        if (TextUtils.equals(mTransactionStatus, getString(R.string.str_income))) {
            for (int c : Constants.INCOME_COLORS)
                colors.add(c);
        } else {
            for (int c : Constants.EXPENSE_CATEGORIES_COLORS)
                colors.add(c);
        }

        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();

    }
}

