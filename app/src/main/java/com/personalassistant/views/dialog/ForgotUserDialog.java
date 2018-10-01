package com.personalassistant.views.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.personalassistant.MyApplication;
import com.personalassistant.R;
import com.personalassistant.endpoint.EndPoints;
import com.personalassistant.endpoint.JsonRequest;
import com.personalassistant.utils.Configuration;
import com.personalassistant.utils.Constants;
import com.personalassistant.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotUserDialog extends DialogFragment implements View.OnClickListener {

    private String mEmail;
    private EditText mEmailET;
    private TextInputLayout mEmailWrapper;

    public static ForgotUserDialog getInstance() {
        return new ForgotUserDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = d.getWindow();
            if (window != null) {
                window.setLayout(width, height);
                window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.setWindowAnimations(R.style.dialogFragmentAnimation);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_forgot_password_layout, container, false);
        Toolbar mToolbar = view.findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initViews(view);
        return view;
    }

    private void initViews(View view) {

        TextView mRedirectToLogin = view.findViewById(R.id.redirect_to_login_tv);
        ImageButton mForgotPasswordActionBTN = view.findViewById(R.id.forgot_password_action_iv_btn);
        mEmailWrapper = view.findViewById(R.id.forgot_email_wrapper);
        mEmailET = view.findViewById(R.id.forgot_email_et);

        mRedirectToLogin.setOnClickListener(this);
        mForgotPasswordActionBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.redirect_to_login_tv:
                dismiss();
                break;
            case R.id.forgot_password_action_iv_btn:
                onSubmitAction();
                break;
        }
    }

    private void onSubmitAction() {
        mEmail = mEmailET.getText().toString().trim();
        if (Configuration.isEmptyValidation(mEmail)) {
            mEmailWrapper.setError(getText(R.string.error_required_email));
        }
        if (!mEmail.matches(Constants.EMAIL_PATTERN)) {
            mEmailWrapper.setErrorEnabled(true);
            mEmailWrapper.setError(getText(R.string.error_invalid_email));
        } else {
            onRequest();
        }
    }

    private void onRequest() {
        if (Configuration.isConnected(getActivity())) {
            Configuration.onAnimatedLoadingShow(getActivity(), "Loading");
            String url = "";
            JSONObject jsonObject = null;
            try {
                url = EndPoints.USER_FORGOT_EMAIL;
                jsonObject = JsonUtils.RequestForgotPwdJsonObject(mEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest ForgotPasswordRequest = new JsonRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Configuration.onAnimatedLoadingDismiss();
                    try {
                        String mMessage = response.getString("message");
                        Configuration.onAnimatedLoadingDismiss();
                        Configuration.onWarningAlertDialog(getActivity(), "Alert", mMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Configuration.onAnimatedLoadingDismiss();
                    Configuration.onWarningAlertDialog(getActivity(), "Alert", "We unable to process");
                }
            });
            ForgotPasswordRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            ForgotPasswordRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(ForgotPasswordRequest, Constants.FORGOT_REQUEST_TAG);
        } else {
            Configuration.onWarningAlertDialog(getActivity(), "Alert", getString(R.string.str_no_internet_connection));
        }


    }
}
