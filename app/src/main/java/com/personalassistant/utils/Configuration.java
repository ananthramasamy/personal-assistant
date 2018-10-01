package com.personalassistant.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.personalassistant.R;

import java.util.ArrayList;
import java.util.Objects;

public class Configuration {
    private static Dialog LoadingProgressDialog;
    private static Dialog WarningAlertDialog;


    //progress dialog
    public static void onAnimatedLoadingShow(Context context, String message) {
        try {
            if (LoadingProgressDialog != null) {
                LoadingProgressDialog.cancel();
                LoadingProgressDialog.hide();
            }
            LoadingProgressDialog = new Dialog(context, R.style.AppCompatAlertDialogStyle);
            LoadingProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LoadingProgressDialog.setContentView(R.layout.dialog_progress_layout);
            TextView mContentMsgTV = LoadingProgressDialog.findViewById(R.id.message_tv);
            mContentMsgTV.setText(message);
            Objects.requireNonNull(LoadingProgressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            LoadingProgressDialog.setCancelable(false);
            LoadingProgressDialog.setCanceledOnTouchOutside(false);
            LoadingProgressDialog.show();
            BounceView.addAnimTo(LoadingProgressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onAnimatedLoadingDismiss() {
        try {
            if (LoadingProgressDialog != null) {
                LoadingProgressDialog.cancel();
                LoadingProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onWarningAlertDialog(Context context, String Tittle, String BodyMsg) {
        try {
            if (WarningAlertDialog != null) {
                WarningAlertDialog.dismiss();
            }
            WarningAlertDialog = new Dialog(context);
            WarningAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WarningAlertDialog.setContentView(R.layout.dialog_warning_layout);
            TextView WarningAlertTittleTV = WarningAlertDialog.findViewById(R.id.warning_tittle_tv);
            TextView WarningAlertMsgTV = WarningAlertDialog.findViewById(R.id.warning_msg_tv);
            Button CloseBTN = WarningAlertDialog.findViewById(R.id.close_action_btn);
            Objects.requireNonNull(WarningAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WarningAlertDialog.setCancelable(false);
            WarningAlertDialog.setCanceledOnTouchOutside(false);
            WarningAlertMsgTV.setText(BodyMsg);
            WarningAlertTittleTV.setText(Tittle);
            //.setText(ButtonText);
            CloseBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (WarningAlertDialog != null) {
                        WarningAlertDialog.cancel();
                        WarningAlertDialog.dismiss();
                    }
                }
            });
            WarningAlertDialog.show();
            BounceView.addAnimTo(WarningAlertDialog);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected(Activity headLineActivity) {
        ConnectivityManager cm = (ConnectivityManager) headLineActivity .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }
    public static boolean isEmptyValidation(String input) {
        return input == null || input.length() == 0 || input.equals("null") || input.equals("%20");
    }

    public static boolean isEmptyZeroValidation(String input) {
        return input == null ||  input.length() == 0 ||input.equals("null") ||input.equals("0") ||  input.equals("0.0")||input.equals("%20");
    }



    public static void ResetTextInputLayout(Context context, final TextInputLayout textInputLayout, EditText mEmailET) {

        mEmailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    textInputLayout.setErrorEnabled(false);
                    textInputLayout.setError(null);
                }
            }
        });

    }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

    public static int onGetMonthPosition(String monthName) {
        ArrayList<String> monthList = new ArrayList<>();
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
        int monthCount = 0;
        for (int i = 0; i < monthList.size(); i++) {
            if (monthName.equalsIgnoreCase(monthList.get(i).trim())) {
                monthCount = i;
                break;
            }
        }
        monthCount++;
        return monthCount;
    }



    public static int onGetIncomeCategoriesPosition(String incomeType) {
        ArrayList<String> CategoriesArrayList = new ArrayList<>();
        CategoriesArrayList.add("Salary");
        CategoriesArrayList.add("Investment");
        CategoriesArrayList.add("Dividends");
        CategoriesArrayList.add("Coupons");
        CategoriesArrayList.add("Refunds");
        CategoriesArrayList.add("Rental");
        CategoriesArrayList.add("Sales");
        CategoriesArrayList.add("Awards0124563780");
        CategoriesArrayList.add("Other");
        int monthCount = 0;
        for (int i = 0; i < CategoriesArrayList.size(); i++) {
            if (incomeType.equalsIgnoreCase(CategoriesArrayList.get(i).trim())) {
                monthCount = i;
                break;
            }
        }
        monthCount++;
        return monthCount;
    }


    public static int onGetExpenseCategoriesPosition(String expenseType) {
        ArrayList<String> ExpenseCategoriesArrayList = new ArrayList<>();
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
        int monthCount = 0;
        for (int i = 0; i < ExpenseCategoriesArrayList.size(); i++) {
            if (expenseType.equalsIgnoreCase(ExpenseCategoriesArrayList.get(i).trim())) {
                monthCount = i;
                break;
            }
        }
        monthCount++;
        return monthCount;
    }

    public static void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
