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
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.personalassistant.MyApplication;
import com.personalassistant.R;
import com.personalassistant.adapter.CategoriesRecyclerViewAdapter;
import com.personalassistant.adapter.IncomeAdapter;
import com.personalassistant.endpoint.EndPoints;
import com.personalassistant.endpoint.JsonRequest;
import com.personalassistant.enities.income.GetIncomeDetailsResult;
import com.personalassistant.interfaces.CategoriesInterfaces;
import com.personalassistant.interfaces.GetAllIncomeInterfaces;
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

public class InComeDetailsFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private String mTransactionType, mTransactionAmount, mTransactionDescription;
    private RecyclerView mAllIncomeRecyclerView, mCategoriesRecyclerView;
    private IncomeAdapter mIncomeAdapter;
    private CategoriesRecyclerViewAdapter mCategoriesRecyclerViewAdapter;
    private ArrayList<String> CategoriesArrayList;
    private ArrayList<GetIncomeDetailsResult> getIncomeDetailsResultsList = new ArrayList<>();
    private FloatingActionButton mAddIncomeFAB;
    private EditText mCategoriesNameET, mTransactionAmountET, mTransactionDescET;
    private TextInputLayout mCategoriesNameWrapper, mTransactionAmountWrapper, mTransactionDescWrapper;
    private TextView mNoRecordFoundTV, mActualIncomeValueTV, mGoalIncomeValueTV;
    private Dialog dialog;
    private String mActualIncomeFormatted, mGoalIncomeFormatted;
    private View dialogView;
    private LinearLayout mTransitionsContainer;
    private int onSelectedItemPosition, fabMargin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_income_details_layout, container, false);
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
        onPrePareAllIncomeData();
        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_income_fab:
                onAddIncomeView();
                break;
            case R.id.income_submit_action_iv_btn:
                mTransactionType = mCategoriesNameET.getText().toString().trim();
                mTransactionDescription = mTransactionDescET.getText().toString().trim();
                mTransactionAmount = mTransactionAmountET.getText().toString().trim();
                if (Configuration.isEmptyValidation(mTransactionType)) {
                    mCategoriesNameWrapper.setError("Select Any one Category");
                    mCategoriesNameET.setFocusable(true);
                } else if (Configuration.isEmptyValidation(mTransactionAmount)) {
                    mTransactionAmountWrapper.setError("Invalid Amount");
                    mTransactionAmountET.setFocusable(true);
                } else if (Configuration.isEmptyZeroValidation(mTransactionAmount)) {
                    mTransactionAmountWrapper.setError("Invalid Amount");
                    mTransactionAmountET.setFocusable(true);
                } else {
                    onPutIncomeRequest(mTransactionType, mTransactionDescription, mTransactionAmount);
                }
                break;
        }

    }

    private void initViews(View rootView) {
        mNoRecordFoundTV = rootView.findViewById(R.id.no_record_found_tv);
        TextView mMonthNameTV = rootView.findViewById(R.id.month_name_tv);
        TextView mSelectedYearTV = rootView.findViewById(R.id.year_tv);
        if (!Configuration.isEmptyValidation(MyApplication.getYear())) {
            mSelectedYearTV.setText(MyApplication.getYear());
        }
        if (!Configuration.isEmptyValidation(MyApplication.getMonthName())) {
            mMonthNameTV.setText(MyApplication.getMonthName());
        }
        mGoalIncomeValueTV = rootView.findViewById(R.id.goals_value_tv);
        mActualIncomeValueTV = rootView.findViewById(R.id.actual_value_tv);
        if (!Configuration.isEmptyValidation(mActualIncomeFormatted)) {
            mActualIncomeValueTV.setText(mActualIncomeFormatted);
        }
        if (!Configuration.isEmptyValidation(mGoalIncomeFormatted)) {
            mGoalIncomeValueTV.setText(mGoalIncomeFormatted);
        }

        mAddIncomeFAB = rootView.findViewById(R.id.add_income_fab);
        mAddIncomeFAB.setOnClickListener(this);
        getIncomeDetailsResultsList = new ArrayList<>();
        mAllIncomeRecyclerView = rootView.findViewById(R.id.income_details_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mAllIncomeRecyclerView.getContext());
        mAllIncomeRecyclerView.setLayoutManager(layoutManager);
        mAllIncomeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAllIncomeRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerVerticalItemDecoration(mContext);
        mAllIncomeRecyclerView.addItemDecoration(itemDecoration);
        mIncomeAdapter = new IncomeAdapter(mContext, getIncomeDetailsResultsList, new GetAllIncomeInterfaces() {
            @Override
            public void onSelectedIncome(String transactionId, int onSeletedPosition) {
                onSelectedItemPosition = onSeletedPosition;
                onDeleteTransaction(transactionId);

            }
        });
        mAllIncomeRecyclerView.setAdapter(mIncomeAdapter);
        mAllIncomeRecyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                mAddIncomeFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                mAddIncomeFAB.animate().translationY(mAddIncomeFAB.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2)).start();
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

            JsonRequest DeleteTransactionRequest = new JsonRequest(Request.Method.POST, EndPoints.DELETE_INCOME_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
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
            getIncomeDetailsResultsList.remove(onSelectedItemPosition);
            if (getIncomeDetailsResultsList.size() > 0) {
                mIncomeAdapter.onNotifyDataSetChanged(getIncomeDetailsResultsList);
                mIncomeAdapter.notifyItemChanged(onSelectedItemPosition);
                mAllIncomeRecyclerView.scrollToPosition(onSelectedItemPosition);

                mAllIncomeRecyclerView.setVisibility(View.VISIBLE);
                mNoRecordFoundTV.setVisibility(View.GONE);
            } else {
                mAllIncomeRecyclerView.setVisibility(View.GONE);
                mNoRecordFoundTV.setVisibility(View.VISIBLE);
            }

            Toasty.success(mContext, mMessage, 100, true).show();
            Configuration.onAnimatedLoadingDismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }
    }

    private void onPrePareAllIncomeData() {
        if (Configuration.isConnected(Objects.requireNonNull(getActivity()))) {
            Configuration.onAnimatedLoadingShow(mContext, getString(R.string.str_loading));
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestToCommonTransactionJsonObject();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest AddIncomeRequest = new JsonRequest(Request.Method.POST, EndPoints.GET_INCOME_ALL_REQUEST, jsonObject, new Response.Listener<JSONObject>() {
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
        Configuration.onAnimatedLoadingDismiss();
        getIncomeDetailsResultsList = new ArrayList<>();
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
                    getIncomeDetailsResultsList.add(new GetIncomeDetailsResult(transactionId, transactionType, transactionDescription,
                            transactionAmount, transactionAmountFormatted));
                }


                mIncomeAdapter.onNotifyDataSetChanged(getIncomeDetailsResultsList);
                onGetGoalIncomeData();
            }
            if (getIncomeDetailsResultsList.size() > 0) {
                mIncomeAdapter.onNotifyDataSetChanged(getIncomeDetailsResultsList);
                Configuration.runLayoutAnimation(mAllIncomeRecyclerView);
                mAllIncomeRecyclerView.setVisibility(View.VISIBLE);
                mNoRecordFoundTV.setVisibility(View.GONE);
            } else {
                mAllIncomeRecyclerView.setVisibility(View.GONE);
                mNoRecordFoundTV.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void onGetGoalIncomeData() {
        Configuration.onAnimatedLoadingDismiss();
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
    }

    private void onGetGoalIncomeResponse(JSONObject response) {

        try {
            JSONObject mGoalsJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_GOALS_TAG);
            mGoalIncomeFormatted = mGoalsJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_FORMATTED_TAG);
            JSONObject mActualJsonObject = response.getJSONObject(Constants.DASHBOARD_TRANSACTION_ACTUALS_TAG);
            mActualIncomeFormatted = mActualJsonObject.getString(Constants.DASHBOARD_TRANSACTION_INCOME_FORMATTED_TAG);
            mActualIncomeValueTV.setText(mActualIncomeFormatted);
            mGoalIncomeValueTV.setText(mGoalIncomeFormatted);
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
        mTransitionsContainer = dialogView.findViewById(R.id.dialog);
        mCategoriesRecyclerView = dialogView.findViewById(R.id.category_recycler_view);
        mCategoriesNameET = dialogView.findViewById(R.id.category_name_et);
        mTransactionAmountET = dialogView.findViewById(R.id.income_amount_et);
        mTransactionDescET = dialogView.findViewById(R.id.income_description_et);
        mCategoriesNameWrapper = dialogView.findViewById(R.id.category_name_wrapper);
        mTransactionAmountWrapper = dialogView.findViewById(R.id.income_amount_wrapper);
        mTransactionDescWrapper = dialogView.findViewById(R.id.income_description_wrapper);
        Configuration.ResetTextInputLayout(mContext, mCategoriesNameWrapper, mCategoriesNameET);
        Configuration.ResetTextInputLayout(mContext, mTransactionAmountWrapper, mTransactionAmountET);
        Configuration.ResetTextInputLayout(mContext, mTransactionDescWrapper, mTransactionDescET);
        ImageButton mIncomeSubmitBTN = dialogView.findViewById(R.id.income_submit_action_iv_btn);
        mIncomeSubmitBTN.setOnClickListener(this);
        BounceView.addAnimTo(mIncomeSubmitBTN);
        CategoriesArrayList = new ArrayList<>();
        CategoriesArrayList.add("Salary");
        CategoriesArrayList.add("Investment");
        CategoriesArrayList.add("Dividends");
        CategoriesArrayList.add("Coupons");
        CategoriesArrayList.add("Refunds");
        CategoriesArrayList.add("Rental");
        CategoriesArrayList.add("Sales");
        CategoriesArrayList.add("Awards");
        CategoriesArrayList.add("Other");
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mCategoriesRecyclerView.setLayoutManager(layoutManager);
        mCategoriesRecyclerView.setHasFixedSize(true);
        mCategoriesRecyclerViewAdapter = new CategoriesRecyclerViewAdapter(mContext, Constants.INCOME_COLORS, CategoriesArrayList, new CategoriesInterfaces() {
            @Override
            public void onSelectedItem(int position, String CategoriesName) {
                mTransactionType = CategoriesName;
                if (!Configuration.isEmptyValidation(mTransactionType)) {
                    mCategoriesNameET.setText(mTransactionType);
                    mTransactionAmountET.setFocusable(true);
                }
            }
        });
        mCategoriesRecyclerView.setAdapter(mCategoriesRecyclerViewAdapter);
        mCategoriesRecyclerViewAdapter.onNotifyDataSetChanged(CategoriesArrayList);
        Configuration.runLayoutAnimation(mCategoriesRecyclerView);
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

    private void onPutIncomeRequest(String transactionType, String mTransactionDesc, String mTransactionAmount) {


        if (Configuration.isConnected(Objects.requireNonNull(getActivity()))) {
            Configuration.onAnimatedLoadingShow(mContext, getResources().getString(R.string.str_loading));
            String url = EndPoints.PUT_INCOME_REQUEST;
            JSONObject jsonObject = null;
            try {
                jsonObject = JsonUtils.RequestPutTransactionJsonObject(transactionType, mTransactionDesc, mTransactionAmount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest AddIncomeRequest = new JsonRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
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
            AddIncomeRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            AddIncomeRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(AddIncomeRequest, Constants.PUT_INCOME_REQUESTS_TAG);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));
        }

    }

    private void onSuccessResponse(JSONObject response) {
        try {
            final String mMessage = response.getString("message");
            Configuration.onAnimatedLoadingDismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onUserConfirmationWarningAlert(mContext, mMessage);

                }
            }, 1000);
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
            mAllIncomeRecyclerView.setVisibility(View.GONE);
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
        int cx = (int) (mAddIncomeFAB.getX() + (mAddIncomeFAB.getWidth() / 2));
        int cy = (int) (mAddIncomeFAB.getY()) + mAddIncomeFAB.getHeight() + 56;
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
                    mCategoriesNameET.setText("");
                    mTransactionAmountET.setText("");
                    mTransactionDescET.setText("");
                    mCategoriesRecyclerViewAdapter.onNotifyDataSetChanged(CategoriesArrayList);

                }
            });
            Button CloseBTN = WarningAlertDialog.findViewById(R.id.negative_action_btn);
            Objects.requireNonNull(WarningAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WarningAlertDialog.setCancelable(false);
            WarningAlertDialog.setCanceledOnTouchOutside(false);
            WarningAlertMsgTV.setText(mMessage + " Do You wish to add another income?");
            WarningAlertTittleTV.setText("Alert");
            CloseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCategoriesNameET.setText("");
                    mTransactionAmountET.setText("");
                    mTransactionDescET.setText("");
                    WarningAlertDialog.dismiss();
                    revealShow(dialogView, false, dialog);
                    onPrePareAllIncomeData();
                }
            });
            WarningAlertDialog.show();
            BounceView.addAnimTo(WarningAlertDialog);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
