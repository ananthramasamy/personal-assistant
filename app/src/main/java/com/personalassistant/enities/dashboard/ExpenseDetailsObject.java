package com.personalassistant.enities.dashboard;

public class ExpenseDetailsObject {
    private String mTransactionType, mTransactionTypePercentage, mTransactionTypeTotal, mTransactionTypePercentageFormatted, mTransactionTypeTotalFormatted;

    public ExpenseDetailsObject(String mTransactionType, String mTransactionTypePercentage, String mTransactionTypeTotal, String mTransactionTypePercentageFormatted, String mTransactionTypeTotalFormatted) {
        this.mTransactionType = mTransactionType;
        this.mTransactionTypePercentage = mTransactionTypePercentage;
        this.mTransactionTypeTotal = mTransactionTypeTotal;
        this.mTransactionTypePercentageFormatted = mTransactionTypePercentageFormatted;
        this.mTransactionTypeTotalFormatted = mTransactionTypeTotalFormatted;
    }

    public String getmTransactionType() {
        return mTransactionType;
    }

    public void setmTransactionType(String mTransactionType) {
        this.mTransactionType = mTransactionType;
    }

    public String getmTransactionTypePercentage() {
        return mTransactionTypePercentage;
    }

    public void setmTransactionTypePercentage(String mTransactionTypePercentage) {
        this.mTransactionTypePercentage = mTransactionTypePercentage;
    }

    public String getmTransactionTypeTotal() {
        return mTransactionTypeTotal;
    }

    public void setmTransactionTypeTotal(String mTransactionTypeTotal) {
        this.mTransactionTypeTotal = mTransactionTypeTotal;
    }

    public String getmTransactionTypePercentageFormatted() {
        return mTransactionTypePercentageFormatted;
    }

    public void setmTransactionTypePercentageFormatted(String mTransactionTypePercentageFormatted) {
        this.mTransactionTypePercentageFormatted = mTransactionTypePercentageFormatted;
    }

    public String getmTransactionTypeTotalFormatted() {
        return mTransactionTypeTotalFormatted;
    }

    public void setmTransactionTypeTotalFormatted(String mTransactionTypeTotalFormatted) {
        this.mTransactionTypeTotalFormatted = mTransactionTypeTotalFormatted;
    }


}
