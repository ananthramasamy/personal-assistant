package com.personalassistant;

import android.app.Application;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.personalassistant.endpoint.OkHttpStack;

import static android.content.ContentValues.TAG;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private RequestQueue mRequestQueue;
    public static String UserName;
    public static int Month;
    public static String MonthName;
    public static String Year;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    //////////////////////// Volley request ///////////////////////////////////////////////////////////////////////////////////////

    public static DefaultRetryPolicy getDefaultRetryPolice() {
        return new DefaultRetryPolicy(14000, 2, 1);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this, new OkHttpStack());
        }
        return mRequestQueue;
    }

    @VisibleForTesting
    public void setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static String getUserName() {
        return UserName;
    }

    public static void setUserName(String userName) {
        UserName = userName;
    }

    public static int getMonth() {
        return Month;
    }

    public static void setMonth(int month) {
        Month = month;
    }

    public static String getYear() {
        return Year;
    }

    public static void setYear(String year) {
        Year = year;
    }

    public static String getMonthName() {
        return MonthName;
    }

    public static void setMonthName(String monthName) {
        MonthName = monthName;
    }

}


