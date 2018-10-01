package com.personalassistant.utils;

import com.personalassistant.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    private static String TAG_USER_NAME = "email";
    private static String TAG_PASSWORD = "password";
    private static String TAG_MOBILE_NO = "mobileno";
    private static String TAG_MONTH = "month";
    private static String TAG_YEAR = "year";
    private static String TAG_TRANSACTION_TYPE = "transaction_type";
    private static String TAG_TRANSACTION_DESCRIPTION = "transaction_description";
    private static String TAG_TRANSACTION_AMOUNT = "transaction_amount";
    private static String TAG_TRANSACTION_ID = "transaction_id";

    private static String TAG_INCOME = "income";
    private static String TAG_EXPENSE = "expense";


    public static JSONObject RequestRegisterJsonObject(String userName, String password, String mobileNo) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, userName);
        jo.put(JsonUtils.TAG_PASSWORD, password);
        jo.put(JsonUtils.TAG_MOBILE_NO, password);
        return jo;
    }

    public static JSONObject RequestLoginJsonObject(String userName, String password) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, userName);
        jo.put(JsonUtils.TAG_PASSWORD, password);
        return jo;
    }

    public static JSONObject RequestForgotPwdJsonObject(String userName) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, userName);

        return jo;
    }

    public static JSONObject RequestPutTransactionJsonObject(String transactionType,
                                                             String transactionDescription, String transactionAmount) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, MyApplication.getUserName());
        jo.put(JsonUtils.TAG_MONTH, MyApplication.getMonth());
        jo.put(JsonUtils.TAG_YEAR, MyApplication.getYear());
        jo.put(JsonUtils.TAG_TRANSACTION_TYPE, transactionType);
        jo.put(JsonUtils.TAG_TRANSACTION_DESCRIPTION, transactionDescription);
        jo.put(JsonUtils.TAG_TRANSACTION_AMOUNT, transactionAmount);
        return jo;
    }

    public static JSONObject RequestToCommonTransactionJsonObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, MyApplication.getUserName());
        jo.put(JsonUtils.TAG_MONTH, MyApplication.getMonth());
        jo.put(JsonUtils.TAG_YEAR, MyApplication.getYear());
        return jo;
    }

    public static JSONObject RequestToDeleteTransactionJsonObject(String transactionId) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, MyApplication.getUserName());
        jo.put(JsonUtils.TAG_TRANSACTION_ID, transactionId);
        return jo;
    }

    public static JSONObject UpdateGoalsJsonObject(String income, String expense) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put(JsonUtils.TAG_USER_NAME, MyApplication.getUserName());
        jo.put(JsonUtils.TAG_MONTH, MyApplication.getMonth());
        jo.put(JsonUtils.TAG_YEAR, MyApplication.getYear());
        jo.put(JsonUtils.TAG_INCOME, income);
        jo.put(JsonUtils.TAG_EXPENSE, expense);
        return jo;
    }

}
