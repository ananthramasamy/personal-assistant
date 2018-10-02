package com.personalassistant.utils;

public class Constants {

    public static String EXCEPTION_MESSAGE = "We are unable process";
    public static String SUCCESS_MESSAGE = "Success";
    public static String NETWORK_LOST_MESSAGE = "NetWork Lost.\nCheck internet Connections";
    public static int CREATE_USER_RESULT_CODE = 10;
    public static int LOGIN_RESULT_CODE = 11;
    public static int FORGOT_RESULT_CODE = 12;

    public static int INCOME_TRANSACTION_REQUEST_CODE=50;
    public static int EXPENSE_TRANSACTION_REQUEST_CODE=51;


    public static String FORGOT_REQUEST_TAG = "forgot_request_tag";
    public static String LOGIN_REQUEST_TAG = "login_request_tag";
    public static String REGISTER_REQUEST_TAG = "register_request_tag";
    public static final int MissingStatusCode = 9999;
    public static final String USER_MASTER_REQUESTS_TAG = "user_master_requests";
    public static final String PUT_EXPENSE_REQUESTS_TAG = "put_expense_request";
    public static final String PUT_INCOME_REQUESTS_TAG = "put_income_request";
    public static final String GET_INCOME_REQUESTS_TAG = "get_income_request";
    public static final String GET_EXPENSE_REQUESTS_TAG = "get_expense_request";
    public static String BUDGET_FRAGMENT_NAME = "budget_fragment_name";
    public static String INCOME_DETAILS_FRAGMENT_NAME = "income_fragment_details";
    public static String EXPENSE_DETAILS_FRAGMENT_NAME = "expense_fragment_details";
    public static String INCOME_EXPENSE_OVER_ALL_DETAILS_FRAGMENT_NAME = "income_expense_over_all_fragment_details";
    public static String EXPENSE_GOAL = "expense_goal_details";

    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-z.A-Z.]+";
    public static int[] MATERIAL_COLORS = {
            Configuration.rgb("#00E099"),
            Configuration.rgb("#FF3892"),
            Configuration.rgb("#FF9600"),
            Configuration.rgb("#02D1AC"),
            Configuration.rgb("#FFD200"),
            Configuration.rgb("#00C6FF"),
            Configuration.rgb("#E53935"),
            Configuration.rgb("#8E24AA"),
            Configuration.rgb("#3F51B5"),
            Configuration.rgb("#1DE9B6"),
            Configuration.rgb("#4CAF50"),
            Configuration.rgb("#F57F17")
    };

    public static int[] INCOME_COLORS = {
            Configuration.rgb("#FF5722"),
            Configuration.rgb("#607D8B"),
            Configuration.rgb("#FF9600"),
            Configuration.rgb("#4CAF50"),
            Configuration.rgb("#009688"),
            Configuration.rgb("#00BCD4"),
            Configuration.rgb("#3F51B5"),
            Configuration.rgb("#E91E63"),
            Configuration.rgb("#005688"),
            Configuration.rgb("#BDBDBD"),
            Configuration.rgb("#BDBDBD"),
    };

    public static int[] EXPENSE_CATEGORIES_COLORS = {
            Configuration.rgb("#64DD17"),
            Configuration.rgb("#C51162"),
            Configuration.rgb("#D50000"),
            Configuration.rgb("#AA00FF"),
            Configuration.rgb("#6200EA"),
            Configuration.rgb("#304FFE"),
            Configuration.rgb("#2962FF"),
            Configuration.rgb("#0091EA"),
            Configuration.rgb("#00B8D4"),
            Configuration.rgb("#00BFA5"),
            Configuration.rgb("#00C853"),
            Configuration.rgb("#64DD17"),
            Configuration.rgb("#FFD600"),
            Configuration.rgb("#AEEA00"),
            Configuration.rgb("#00C853"),
            Configuration.rgb("#DD2600"),
            Configuration.rgb("#BDBDBD"),
            Configuration.rgb("#6200EA"),
            Configuration.rgb("#304FFE"),
            Configuration.rgb("#2962FF"),

    };

    //response tag;
    public static String INCOME_TRANSACTION_TAG = "transactions";
    public static String INCOME_TRANSACTION_ID_TAG = "transaction_id";
    public static String INCOME_TRANSACTION_NAME_TAG = "transaction_type";
    public static String INCOME_TRANSACTION_AMOUNT_TAG = "transaction_amount";
    public static String INCOME_TRANSACTION_DESCRIPTION_TAG = "transaction_description";
    public static String INCOME_TRANSACTION_AMOUNT_FORMATTED_TAG = "transaction_amount_formatted";
    public static String MESSAGE_TAG = "message";


    public static String DASHBOARD_TRANSACTION_GOALS_TAG = "goals";
    public static String DASHBOARD_TRANSACTION_INCOME_TAG = "income";
    public static String DASHBOARD_TRANSACTION_EXPENSE_TAG = "expense";
    public static String DASHBOARD_TRANSACTION_INCOME_FORMATTED_TAG = "income_formatted";
    public static String DASHBOARD_TRANSACTION_EXPENSE_FORMATTED_TAG = "expense_formatted";
    public static String DASHBOARD_TRANSACTION_ACTUALS_TAG = "actuals";
    public static String DASHBOARD_TRANSACTION_INCOME_DETAILS_TAG = "income_details";
    public static String DASHBOARD_TRANSACTION_EXPENSE_DETAILS_TAG = "expense_details";
    public static String DASHBOARD_TRANSACTION_TYPE_TAG = "transaction_type";
    public static String DASHBOARD_TRANSACTION_TYPE_PERCENTAGE_TAG = "transaction_type_percentage";
    public static String DASHBOARD_TRANSACTION_TYPE_TOTAL_TAG = "transaction_type_total";
    public static String DASHBOARD_TRANSACTION_TYPE_PERCENTAGE_FORMATTED_TAG = "transaction_type_percentage_formatted";
    public static String DASHBOARD_TRANSACTION_TYPE_TOTAL_FORMATTED_TAG = "transaction_type_total_formatted";

    public static String BUNDLE_ACTUAL_INCOME = "bundle_actual_income";
    public static String BUNDLE_GOAL_INCOME = "bundle_goal_income";
    public static String BUNDLE_ACTUAL_INCOME_FORMATTED = "bundle_actual_income_formatted";
    public static String BUNDLE_GOAL_INCOME_FORMATTED = "bundle_goal_income_formatted";
    public static String BUNDLE_ACTUAL_EXPENSE = "bundle_actual_expense";
    public static String BUNDLE_GOAL_EXPENSE = "bundle_goal_expense";
    public static String BUNDLE_ACTUAL_EXPENSE_FORMATTED = "bundle_actual_expense_formatted";
    public static String BUNDLE_GOAL_EXPENSE_FORMATTED = "bundle_goal_expense_formatted";


}
