package com.personalassistant.views.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.personalassistant.MyApplication;
import com.personalassistant.R;
import com.personalassistant.adapter.CategoriesRecyclerViewAdapter;
import com.personalassistant.adapter.ExpenseAdapter;
import com.personalassistant.endpoint.EndPoints;
import com.personalassistant.endpoint.JsonRequest;
import com.personalassistant.enities.income.GetIncomeDetailsResult;
import com.personalassistant.interfaces.CategoriesInterfaces;
import com.personalassistant.interfaces.GetAllExpenseInterfaces;
import com.personalassistant.utils.BounceView;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;
import com.personalassistant.utils.DividerVerticalItemDecoration;
import com.personalassistant.utils.JsonUtils;
import com.personalassistant.utils.MyRecyclerScroll;
import com.personalassistant.utils.Toasty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ExpenseDetailsFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private String mTransactionType, mTransactionAmount, mTransactionDescription;
    private RecyclerView mAllExpenseRecyclerView, mExpenseCategoriesRecyclerView;
    private CategoriesRecyclerViewAdapter mCategoriesRecyclerViewAdapter;
    private ArrayList<String> ExpenseCategoriesArrayList;
    private ArrayList<GetIncomeDetailsResult> getExpenseDetailsResultsList = new ArrayList<>();
    private ExpenseAdapter mExpenseAdapter;
    private FloatingActionButton mAddExpenseFAB;
    private EditText mExpenseCategoriesNameET, mExpenseTransactionAmountET, mExpenseTransactionDescET;
    private TextInputLayout mExpenseCategoriesNameWrapper, mExpenseTransactionAmountWrapper, mExpenseTransactionDescWrapper;
    private Dialog dialog;
    private TextView mNoRecordFoundTV, mActualExpenseValueTV, mGoalExpenseValueTV;
    private int onSelectedItemPosition, fabMargin;
    View dialogView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_details_layout, container, false);
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        mContext = getActivity();
        fabMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        initViews(rootView);
        getExpenseDetailsResultsList = new ArrayList<>();
        onPrePareAllExpenseData();
        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.expense_fab_btn:
                onAddIncomeView();
                break;
            case R.id.income_submit_action_iv_btn:
                mTransactionType = mExpenseCategoriesNameET.getText().toString().trim();
                mTransactionDescription = mExpenseTransactionDescET.getText().toString().trim();
                mTransactionAmount = mExpenseTransactionAmountET.getText().toString().trim();
                if (Configuration.isEmptyValidation(mTransactionType)) {
                    mExpenseCategoriesNameWrapper.setError("Select Any one Category");
                    mExpenseCategoriesNameET.setFocusable(true);
                } else if (Configuration.isEmptyValidation(mTransactionAmount)) {
                    mExpenseTransactionAmountWrapper.setError("Invalid Amount");
                    mExpenseTransactionAmountET.setFocusable(true);
                } else if (Configuration.isEmptyZeroValidation(mTransactionAmount)) {
                    mExpenseTransactionAmountWrapper.setError("Invalid Amount");
                    mExpenseTransactionAmountET.setFocusable(true);
                } else {
                    onPutExpenseRequest(mTransactionType, mTransactionDescription, mTransactionAmount);
                }
                break;
        }

    }

    private void initViews(View rootView) {
        TextView mActualHintNameTV = rootView.findViewById(R.id.actual_name_tv);
        TextView mGoalHintNameTV = rootView.findViewById(R.id.goal_name_tv);
        TextView mMonthNameTV = rootView.findViewById(R.id.month_name_tv);
        mActualHintNameTV.setText(R.string.str_actual_expense);
        mGoalHintNameTV.setText(R.string.str_goals_expense);
        TextView mSelectedYearTV = rootView.findViewById(R.id.year_tv);
        if (!Configuration.isEmptyValidation(MyApplication.getYear())) {
            mSelectedYearTV.setText(MyApplication.getYear());
        }
        if (!Configuration.isEmptyValidation(MyApplication.getMonthName())) {
            mMonthNameTV.setText(MyApplication.getMonthName());
        }
        mNoRecordFoundTV = rootView.findViewById(R.id.no_record_found_tv);
        mNoRecordFoundTV.setVisibility(View.GONE);

        mActualExpenseValueTV = rootView.findViewById(R.id.actual_value_tv);
        mGoalExpenseValueTV = rootView.findViewById(R.id.goals_value_tv);
        mAddExpenseFAB = rootView.findViewById(R.id.expense_fab_btn);
        mAddExpenseFAB.setOnClickListener(this);

        mAllExpenseRecyclerView = rootView.findViewById(R.id.expense_details_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mAllExpenseRecyclerView.getContext());
        mAllExpenseRecyclerView.setLayoutManager(layoutManager);
        mAllExpenseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAllExpenseRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerVerticalItemDecoration(mContext);
        mAllExpenseRecyclerView.addItemDecoration(itemDecoration);
        mExpenseAdapter = new ExpenseAdapter(mContext, getExpenseDetailsResultsList, new GetAllExpenseInterfaces() {
            @Override
            public void onSelectedIncome(String transactionId, int transactionPosition) {
                onSelectedItemPosition = transactionPosition;
                onDeleteTransaction(transactionId);
            }
        });
        mAllExpenseRecyclerView.setAdapter(mExpenseAdapter);
        mAllExpenseRecyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                mAddExpenseFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                mAddExpenseFAB.animate().translationY(mAddExpenseFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });
    }

    private void onDeleteTransaction(String transactionId) {
        if (Configuration.isConnected(Objects.requireNonNull(getActivity()))) {
            Configuration.onAnimatedLoadingShow(mContext, getString(R.string.str_loading));
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestToDeleteTransactionJsonObject(transactionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest DeleteTransactionRequest = new JsonRequest(Request.Method.POST, EndPoints.DELETE_EXPENSE_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onDeletedSuccessResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onFailureResponse(error);
                }
            });
            DeleteTransactionRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            DeleteTransactionRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(DeleteTransactionRequest, Constants.GET_INCOME_REQUESTS_TAG);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));
        }


    }

    private void onDeletedSuccessResponse(JSONObject response) {

        try {
            String mMessage = response.getString(Constants.MESSAGE_TAG);
            onGetGoalIncomeData();
            getExpenseDetailsResultsList.remove(onSelectedItemPosition);
            if (getExpenseDetailsResultsList.size() > 0) {
                mExpenseAdapter.onNotifyDataSetChanged(getExpenseDetailsResultsList);
                mExpenseAdapter.notifyItemChanged(onSelectedItemPosition);
                mAllExpenseRecyclerView.scrollToPosition(onSelectedItemPosition);

                mAllExpenseRecyclerView.setVisibility(View.VISIBLE);
                mNoRecordFoundTV.setVisibility(View.GONE);
                Toasty.success(mContext, mMessage, 100, true).show();
            } else {
                mAllExpenseRecyclerView.setVisibility(View.GONE);
                mNoRecordFoundTV.setVisibility(View.VISIBLE);
            }

            Configuration.onAnimatedLoadingDismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }
    }

    private void onPrePareAllExpenseData() {

        if (Configuration.isConnected(Objects.requireNonNull(getActivity()))) {
            Configuration.onAnimatedLoadingShow(mContext, getString(R.string.str_loading));
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestToCommonTransactionJsonObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest AddExpenseRequest = new JsonRequest(Request.Method.POST, EndPoints.GET_EXPENSE_ALL_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
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
            AddExpenseRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            AddExpenseRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(AddExpenseRequest, Constants.GET_EXPENSE_REQUESTS_TAG);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));

        }

    }

    private void onPrepareToParser(JSONObject response) {
        Configuration.onAnimatedLoadingDismiss();
        getExpenseDetailsResultsList = new ArrayList<>();
        try {
            JSONArray mainArray = response.getJSONArray(Constants.INCOME_TRANSACTION_TAG);
            if (mainArray.length() > 0) {
                for (int i = 0; i < mainArray.length(); i++) {
                    JSONObject innerObject = mainArray.getJSONObject(i);
                    String transactionId = innerObject.getString(Constants.INCOME_TRANSACTION_ID_TAG);
                    String transactionType = innerObject.getString(Constants.INCOME_TRANSACTION_NAME_TAG);
                    String transactionDescription = innerObject.getString(Constants.INCOME_TRANSACTION_DESCRIPTION_TAG);
                    String transactionAmount = innerObject.getString(Constants.INCOME_TRANSACTION_AMOUNT_TAG);
                    String transactionAmountFormatted = innerObject.getString(Constants.INCOME_TRANSACTION_AMOUNT_FORMATTED_TAG);
                    getExpenseDetailsResultsList.add(new GetIncomeDetailsResult(transactionId, transactionType, transactionDescription,
                            transactionAmount, transactionAmountFormatted));
                }
                mAllExpenseRecyclerView.setVisibility(View.VISIBLE);
                mNoRecordFoundTV.setVisibility(View.GONE);
                onGetGoalIncomeData();
            }
            if (getExpenseDetailsResultsList.size() > 0) {
                mExpenseAdapter.onNotifyDataSetChanged(getExpenseDetailsResultsList);
                Configuration.runLayoutAnimation(mAllExpenseRecyclerView);
                mAllExpenseRecyclerView.setVisibility(View.VISIBLE);
                mNoRecordFoundTV.setVisibility(View.GONE);
            } else {
                mAllExpenseRecyclerView.setVisibility(View.GONE);
                mNoRecordFoundTV.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void onGetGoalIncomeData() {
        if (Configuration.isConnected(Objects.requireNonNull(getActivity()))) {
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestToCommonTransactionJsonObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest AddIncomeRequest = new JsonRequest(Request.Method.POST, EndPoints.GET_DASHBOARD_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onGetGoalIncomeResponse(response);
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

    private void onGetGoalIncomeResponse(JSONObject response) {

        try {
            JSONObject mGoalsJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_GOALS_TAG);
            //mGoalExpense = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_TAG);
            String mGoalExpenseFormatted = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_FORMATTED_TAG);
            JSONObject mActualJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_ACTUALS_TAG);
            //mActualExpense = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_TAG);
            String mActualExpenseFormatted = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_EXPENSE_FORMATTED_TAG);
            mActualExpenseValueTV.setText(mActualExpenseFormatted);
            mGoalExpenseValueTV.setText(mGoalExpenseFormatted);
            Configuration.onAnimatedLoadingDismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onAddIncomeView() {
        dialogView = View.inflate(mContext, R.layout.fragement_dialog_add_income, null);
        dialog = new Dialog(mContext, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);
        TextView textView = dialog.findViewById(R.id.tittle_name_tv);
        textView.setText(getString(R.string.str_add_expense));
        mExpenseCategoriesRecyclerView = dialogView.findViewById(R.id.category_recycler_view);
        mExpenseCategoriesNameET = dialogView.findViewById(R.id.category_name_et);
        mExpenseTransactionAmountET = dialogView.findViewById(R.id.income_amount_et);
        mExpenseTransactionDescET = dialogView.findViewById(R.id.income_description_et);
        mExpenseCategoriesNameWrapper = dialogView.findViewById(R.id.category_name_wrapper);
        mExpenseTransactionAmountWrapper = dialogView.findViewById(R.id.income_amount_wrapper);
        mExpenseTransactionDescWrapper = dialogView.findViewById(R.id.income_description_wrapper);
        Configuration.ResetTextInputLayout(mContext, mExpenseCategoriesNameWrapper, mExpenseCategoriesNameET);
        Configuration.ResetTextInputLayout(mContext, mExpenseTransactionAmountWrapper, mExpenseTransactionAmountET);
        Configuration.ResetTextInputLayout(mContext, mExpenseTransactionDescWrapper, mExpenseTransactionDescET);
        ImageButton mExpenseSubmitBTN = dialogView.findViewById(R.id.income_submit_action_iv_btn);
        mExpenseSubmitBTN.setOnClickListener(this);
        BounceView.addAnimTo(mExpenseSubmitBTN);
        ExpenseCategoriesArrayList = new ArrayList<>();
        ExpenseCategoriesArrayList.add("Food");
        ExpenseCategoriesArrayList.add("Transport");
        ExpenseCategoriesArrayList.add("Shopping");
        ExpenseCategoriesArrayList.add("Sport");
        ExpenseCategoriesArrayList.add("Clothing");
        ExpenseCategoriesArrayList.add("Billing");
        ExpenseCategoriesArrayList.add("Pet");
        ExpenseCategoriesArrayList.add("Electronics");
        ExpenseCategoriesArrayList.add("Education");
        ExpenseCategoriesArrayList.add("Health");
        ExpenseCategoriesArrayList.add("Gift");
        ExpenseCategoriesArrayList.add("Office");
        ExpenseCategoriesArrayList.add("Insurance");
        ExpenseCategoriesArrayList.add("Bike");
        ExpenseCategoriesArrayList.add("Rent");
        ExpenseCategoriesArrayList.add("Telephone");
        ExpenseCategoriesArrayList.add("Home");
        ExpenseCategoriesArrayList.add("Other");


        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mExpenseCategoriesRecyclerView.setLayoutManager(layoutManager);
        mExpenseCategoriesRecyclerView.setHasFixedSize(true);

        mCategoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(mContext, Constants.EXPENSE_CATEGORIES_COLORS, ExpenseCategoriesArrayList, new CategoriesInterfaces() {
            @Override
            public void onSelectedItem(int position, String CategoriesName) {
                mTransactionType = CategoriesName;
                mTransactionType = CategoriesName;
                if (!Configuration.isEmptyValidation(mTransactionType)) {
                    mExpenseCategoriesNameET.setText(mTransactionType);
                }
            }
        });
        mExpenseCategoriesRecyclerView.setAdapter(mCategoriesRecyclerViewAdapter);
        mCategoriesRecyclerViewAdapter.onNotifyDataSetChanged(ExpenseCategoriesArrayList);

        Configuration.runLayoutAnimation(mExpenseCategoriesRecyclerView);

        ImageView imageView = dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void onPutExpenseRequest(String transactionType, String mTransactionDesc, String mTransactionAmount) {
        if (Configuration.isConnected(Objects.requireNonNull(getActivity()))) {
            Configuration.onAnimatedLoadingShow(mContext, getResources().getString(R.string.str_loading));
            String url = EndPoints.PUT_EXPANSE_REQUEST;
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestPutTransactionJsonObject(transactionType, mTransactionDesc, mTransactionAmount);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest AddExpenseRequest = new JsonRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onSuccessResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onFailureResponse(error);
                }
            });
            AddExpenseRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            AddExpenseRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(AddExpenseRequest, Constants.PUT_INCOME_REQUESTS_TAG);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));
        }


    }

    private void onSuccessResponse(JSONObject response) {
        try {
            final String mMessage = response.getString(Constants.MESSAGE_TAG);
            Configuration.onAnimatedLoadingDismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExpenseCategoriesNameET.setText("");
                    mExpenseTransactionAmountET.setText("");
                    mExpenseTransactionDescET.setText("");
                    onUserConfirmationWarningAlert(mContext, mMessage);
                }
            }, 600);
        } catch (JSONException e) {
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

            mAllExpenseRecyclerView.setVisibility(View.GONE);
            mNoRecordFoundTV.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }

    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);
        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);
        int cx = (int) (mAddExpenseFAB.getX() + (mAddExpenseFAB.getWidth() / 2));
        int cy = (int) (mAddExpenseFAB.getY()) + mAddExpenseFAB.getHeight() + 56;
        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });
            anim.setDuration(700);
            anim.start();
        }
    }

    @SuppressLint("SetTextI18n")
    private void onUserConfirmationWarningAlert(Context mContext, String mMessage) {
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
                    mExpenseCategoriesNameET.setText("");
                    mExpenseTransactionAmountET.setText("");
                    mExpenseTransactionDescET.setText("");
                    mCategoriesRecyclerViewAdapter.onNotifyDataSetChanged(ExpenseCategoriesArrayList);

                }
            });
            Button CloseBTN = WarningAlertDialog.findViewById(R.id.negative_action_btn);
            Objects.requireNonNull(WarningAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WarningAlertDialog.setCancelable(false);
            WarningAlertDialog.setCanceledOnTouchOutside(false);
            WarningAlertMsgTV.setText(mMessage + ". Do You wish to add another Expense?");
            WarningAlertTittleTV.setText("Alert");
            CloseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExpenseCategoriesNameET.setText("");
                    mExpenseTransactionAmountET.setText("");
                    mExpenseTransactionDescET.setText("");
                    WarningAlertDialog.dismiss();
                    revealShow(dialogView, false, dialog);
                    onPrePareAllExpenseData();
                }
            });
            WarningAlertDialog.show();
            BounceView.addAnimTo(WarningAlertDialog);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
