package com.personalassistant.endpoint;

public class EndPoints {

    private static final String API_URL = "http://testing.newlogics.in/";    // staging
    public static final String USER_FORGOT_EMAIL = API_URL.concat("Authentication/login");
    public static final String USER_LOGIN_EMAIL = API_URL.concat("Authentication/login");
    public static final String GET_REGISTER = API_URL.concat("Authentication/register");
    public static final String PUT_INCOME_REQUEST = API_URL.concat("income/add");
    public static final String GET_INCOME_ALL_REQUEST = API_URL.concat("income/getincome");
    public static final String PUT_EXPANSE_REQUEST = API_URL.concat("expense/add");
    public static final String GET_EXPENSE_ALL_REQUEST = API_URL.concat("expense/getexpense");
    public static final String DELETE_INCOME_REQUEST = API_URL.concat("income/deleteincome");
    public static final String DELETE_EXPENSE_REQUEST = API_URL.concat("expense/deleteexpense");
    public static final String GET_DASHBOARD_REQUEST = API_URL.concat("dashboard");
    public static final String PUT_GOALS_REQUEST = API_URL.concat("goal/addorupdategoal");

}
