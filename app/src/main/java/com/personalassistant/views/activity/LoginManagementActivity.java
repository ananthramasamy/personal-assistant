package com.personalassistant.views.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.personalassistant.MyApplication;
import com.personalassistant.R;
import com.personalassistant.endpoint.EndPoints;
import com.personalassistant.endpoint.JsonRequest;
import com.personalassistant.utils.BounceView;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;
import com.personalassistant.utils.JsonUtils;
import com.personalassistant.utils.Toasty;
import com.personalassistant.views.dialog.ForgotUserDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginManagementActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout mUserLoginLayout, mRegisterLayout;
    private TextInputLayout mEmailWrapper, mPasswordWrapper, mConfirmPasswordWrapper, mMobileNoWrapper;
    private TextInputLayout mLoginEmailWrapper, mLoginPasswordWrapper;
    private EditText mEmailET, mPasswordET, mConfirmPasswordET, mMobileNoET;
    private EditText mLoginEmailET, mLoginPasswordET;
    private ImageButton LoginActionBTN;
    private TextView mLoginRedirectTV, mRegisterRedirectTV, mForgotPasswordRedirectTV;
    private String mEmail, mPassword, mConfirmPassword, mMobileNo;
    private Context mContext;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main_layout);
        mContext = LoginManagementActivity.this;
        initViews();
        setStatusBackgroundColor();
        onEnableLoginView(mUserLoginLayout);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
       // setSupportActionBar(mToolbar);
      //  Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Login");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRegisterLayout.getVisibility() == View.VISIBLE) {
                    onEnableBackAction(mUserLoginLayout.getId());

                } else if (mUserLoginLayout.getVisibility() == View.VISIBLE) {
                    onUserConfirmationWarningAlert(mContext);
                }
            }
        });
        mUserLoginLayout = findViewById(R.id.user_login_main_layout);
        mRegisterLayout = findViewById(R.id.user_register_main_layout);

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

    private void onEnableLoginView(LinearLayout mLoginLayout) {
        try {
            onEnableBackAction(mUserLoginLayout.getId());
            if (mUserLoginLayout != null) {
                mUserLoginLayout.removeAllViews();
            }
            LayoutInflater viewInflater = getLayoutInflater();
            @SuppressLint("InflateParams") View mRootView = viewInflater.inflate(R.layout.fragment_user_login_layout, null);
            mLoginEmailWrapper = mRootView.findViewById(R.id.login_email_wrapper);
            mLoginPasswordWrapper = mRootView.findViewById(R.id.login_password_wrapper);
            mLoginEmailET = mRootView.findViewById(R.id.login_email_et);
            mLoginPasswordET = mRootView.findViewById(R.id.login_password_et);
            LoginActionBTN = mRootView.findViewById(R.id.login_action_iv_btn);
            mRegisterRedirectTV = mRootView.findViewById(R.id.register_redirect_tv);
            mForgotPasswordRedirectTV = mRootView.findViewById(R.id.forgot_password_redirect_tv);
            LoginActionBTN.setOnClickListener(this);
            mRegisterRedirectTV.setOnClickListener(this);
            mForgotPasswordRedirectTV.setOnClickListener(this);
            Configuration.ResetTextInputLayout(mContext, mLoginEmailWrapper, mLoginEmailET);
            Configuration.ResetTextInputLayout(mContext, mLoginPasswordWrapper, mLoginPasswordET);
            mLoginLayout.addView(mRootView);
            BounceView.addAnimTo(mRootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onEnableBackAction(int id) {
        try {
            switch (id) {
                case R.id.user_login_main_layout:
                    mUserLoginLayout.setVisibility(View.VISIBLE);
                    mRegisterLayout.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(mUserLoginLayout);
                    mToolbar.setTitle("Login");
                    break;
                case R.id.user_register_main_layout:
                    mUserLoginLayout.setVisibility(View.GONE);
                    mRegisterLayout.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(mRegisterLayout);
                    mToolbar.setTitle("Register");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onEnableRegisterView(LinearLayout RegisterLayout) {
        try {
            onEnableBackAction(mRegisterLayout.getId());
            if (mRegisterLayout != null) {
                mRegisterLayout.removeAllViews();
            }
            LayoutInflater viewInflater = getLayoutInflater();
            @SuppressLint("InflateParams") View rootView = viewInflater.inflate(R.layout.fragment_create_user_layout, null);
            mEmailWrapper = rootView.findViewById(R.id.register_email_wrapper);
            mPasswordWrapper = rootView.findViewById(R.id.register_password_wrapper);
            mConfirmPasswordWrapper = rootView.findViewById(R.id.register_confirm_password_wrapper);
            mMobileNoWrapper = rootView.findViewById(R.id.register_mobile_no_wrapper);
            mEmailET = rootView.findViewById(R.id.register_email_et);
            mPasswordET = rootView.findViewById(R.id.register_password_et);
            mConfirmPasswordET = rootView.findViewById(R.id.register_confirm_password_et);
            mMobileNoET = rootView.findViewById(R.id.register_mobile_no_et);
            ImageButton mRegisterActionBTN = rootView.findViewById(R.id.register_action_iv_btn);
            mLoginRedirectTV = rootView.findViewById(R.id.login_page_redirect_action_tv);
            mRegisterActionBTN.setOnClickListener(this);
            mLoginRedirectTV.setOnClickListener(this);
            Configuration.ResetTextInputLayout(mContext, mEmailWrapper, mEmailET);
            Configuration.ResetTextInputLayout(mContext, mPasswordWrapper, mPasswordET);
            Configuration.ResetTextInputLayout(mContext, mConfirmPasswordWrapper, mConfirmPasswordET);
            Configuration.ResetTextInputLayout(mContext, mMobileNoWrapper, mMobileNoET);
            RegisterLayout.addView(rootView);
            BounceView.addAnimTo(RegisterLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_page_redirect_action_tv:
                onEnableLoginView(mUserLoginLayout);
                break;
            case R.id.register_action_iv_btn:
                onRegistrationSubmitAction();
                break;
            case R.id.login_action_iv_btn:
                onLoginSubmitAction();
                break;
            case R.id.register_redirect_tv:
                onEnableRegisterView(mRegisterLayout);
                break;
            case R.id.forgot_password_redirect_tv:
                Bundle args = new Bundle();
                ForgotUserDialog bottomSheetDialog = ForgotUserDialog.getInstance();
                bottomSheetDialog.setArguments(args);
                bottomSheetDialog.show(getFragmentManager(), "ForgotUserDialog");
                break;


        }
    }

    private void onLoginSubmitAction() {
        mEmail = mLoginEmailET.getText().toString().trim();
        mPassword = mLoginPasswordET.getText().toString().trim();
        if (Configuration.isEmptyValidation(mEmail)) {
            mLoginEmailWrapper.setErrorEnabled(true);
            mLoginEmailWrapper.setError(getText(R.string.error_required_email));
        } else if (Configuration.isEmptyValidation(mPassword)) {
            mLoginPasswordWrapper.setErrorEnabled(true);
            mLoginPasswordWrapper.setError(getText(R.string.error_required_password));
        } else {
            if (!mEmail.matches(Constants.EMAIL_PATTERN)) {
                mLoginEmailWrapper.setErrorEnabled(true);
                mLoginEmailWrapper.setError(getText(R.string.error_invalid_email));
            } else {
                onRequest(Constants.LOGIN_REQUEST_TAG);
            }
        }
    }

    private void onRegistrationSubmitAction() {
        mEmail = mEmailET.getText().toString().trim();
        mPassword = mPasswordET.getText().toString().trim();
        mConfirmPassword = mConfirmPasswordET.getText().toString().trim();
        mMobileNo = mMobileNoET.getText().toString().trim();
        if (Configuration.isEmptyValidation(mEmail)) {
            mEmailWrapper.setErrorEnabled(true);
            mEmailWrapper.setError(getText(R.string.error_required_email));
        } else if (Configuration.isEmptyValidation(mPassword)) {
            mPasswordWrapper.setErrorEnabled(true);
            mPasswordWrapper.setError(getText(R.string.error_required_password));
        } else if (Configuration.isEmptyValidation(mConfirmPassword)) {
            mConfirmPasswordWrapper.setErrorEnabled(true);
            mConfirmPasswordWrapper.setError(getText(R.string.error_required_confirm_email));
        } else if (!TextUtils.equals(mPassword, mConfirmPassword)) {
            mConfirmPasswordWrapper.setErrorEnabled(true);
            mConfirmPasswordWrapper.setError(getText(R.string.error_mismatch_password));
        } else if (mMobileNo.length() < 10) {
            mMobileNoWrapper.setErrorEnabled(true);
            mMobileNoWrapper.setError(getText(R.string.error_invalid_mobile_no));
        } else {
            if (!mEmail.matches(Constants.EMAIL_PATTERN)) {
                mEmailWrapper.setErrorEnabled(true);
                mEmailWrapper.setError(getText(R.string.error_invalid_email));
            } else {
                onRequest(Constants.REGISTER_REQUEST_TAG);

            }
        }
    }

    private void onProceedForMainActivity() {
        MyApplication.setUserName(mEmail);
        startActivity(new Intent(LoginManagementActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.right_to_left_in, R.anim.right_to_left_out);
        finish();
    }


    @Override
    public void onBackPressed() {
        try {
            if (mRegisterLayout.getVisibility() == View.VISIBLE) {
                onEnableBackAction(mUserLoginLayout.getId());
            } else if (mUserLoginLayout.getVisibility() == View.VISIBLE) {
                onUserConfirmationWarningAlert(mContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onRequest(final String mRequestType) {
        if (Configuration.isConnected(LoginManagementActivity.this)) {
            Configuration.onAnimatedLoadingShow(mContext, getResources().getString(R.string.str_loading));
            String url = "";
            JSONObject jsonObject = null;
            try {
                if (TextUtils.equals(mRequestType, Constants.FORGOT_REQUEST_TAG)) {
                    jsonObject = JsonUtils.RequestForgotPwdJsonObject(mEmail);
                } else if (TextUtils.equals(mRequestType, Constants.LOGIN_REQUEST_TAG)) {
                    url = EndPoints.USER_LOGIN_EMAIL;
                    jsonObject = JsonUtils.RequestLoginJsonObject(mEmail, mPassword);
                } else if (TextUtils.equals(mRequestType, Constants.REGISTER_REQUEST_TAG)) {
                    url = EndPoints.GET_REGISTER;
                    jsonObject = JsonUtils.RequestRegisterJsonObject(mEmail, mPassword, mMobileNo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest ForgotPasswordRequest = new JsonRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onSuccessResponse(mRequestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onFailureResponse(error);
                }
            });
            ForgotPasswordRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            ForgotPasswordRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(ForgotPasswordRequest, Constants.USER_MASTER_REQUESTS_TAG);
        } else {
            Configuration.onWarningAlertDialog(mContext, "Alert", getString(R.string.str_no_internet_connection));
        }


    }

    private void onFailureResponse(VolleyError error) {
        String errorData = new String(error.networkResponse.data);
        try {
            JSONObject jsonObject = new JSONObject(errorData);
            String mMessage = jsonObject.getString("message");
            MyApplication.setUserName("");
            Toasty.error(mContext, mMessage, 100, true).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
            MyApplication.setUserName("");
        }
        Configuration.onAnimatedLoadingDismiss();

    }

    private void onSuccessResponse(String mRequestType, JSONObject response) {
        try {
            String mMessage = response.getString("message");
            if (TextUtils.equals(mRequestType, Constants.LOGIN_REQUEST_TAG)) {
                onProceedForMainActivity();
            } else if (TextUtils.equals(mRequestType, Constants.REGISTER_REQUEST_TAG)) {
                Toasty.success(mContext, mMessage, 100, true).show();
                onEnableLoginView(mUserLoginLayout);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Configuration.onAnimatedLoadingDismiss();
        }
        Configuration.onAnimatedLoadingDismiss();
    }

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

                }
            });
            Button CloseBTN = WarningAlertDialog.findViewById(R.id.negative_action_btn);
            Objects.requireNonNull(WarningAlertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WarningAlertDialog.setCancelable(false);
            WarningAlertDialog.setCanceledOnTouchOutside(false);
            WarningAlertMsgTV.setText("Do you want to exit the application?");
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
    //Request

}
