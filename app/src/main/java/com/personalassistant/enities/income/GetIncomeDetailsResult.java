package com.personalassistant.enities.income;

import android.os.Parcel;
import android.os.Parcelable;

public class GetIncomeDetailsResult implements Parcelable {

    private String transactionId;
    private String transactionType;
    private String transactionDescription;
    private String transactionAmount;
    private String transactionAmountFormatted;

    public GetIncomeDetailsResult(String transactionId, String transactionType, String transactionDescription, String transactionAmount, String transactionAmountFormatted) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.transactionDescription = transactionDescription;
        this.transactionAmount = transactionAmount;
        this.transactionAmountFormatted = transactionAmountFormatted;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }


    public String getTransactionAmountFormatted() {
        return transactionAmountFormatted;
    }

    public void setTransactionAmountFormatted(String transactionAmountFormatted) {
        this.transactionAmountFormatted = transactionAmountFormatted;
    }

    public GetIncomeDetailsResult(Parcel in) {
        this.transactionId = in.readString();
        this.transactionType = in.readString();
        this.transactionDescription = in.readString();
        this.transactionAmount = in.readString();
        this.transactionAmountFormatted = in.readString();


    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }


    public static final Creator<GetIncomeDetailsResult> CREATOR = new Creator<GetIncomeDetailsResult>() {
        @Override
        public GetIncomeDetailsResult createFromParcel(Parcel in) {
            return new GetIncomeDetailsResult(in);
        }

        @Override
        public GetIncomeDetailsResult[] newArray(int size) {
            return new GetIncomeDetailsResult[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionId);
        dest.writeString(transactionType);
        dest.writeString(transactionDescription);
        dest.writeString(transactionAmount);
        dest.writeString(transactionAmountFormatted);

    }
}
