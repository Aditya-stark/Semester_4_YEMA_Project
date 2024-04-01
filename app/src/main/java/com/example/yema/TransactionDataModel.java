package com.example.yema;

public class TransactionDataModel {
    public int cardIcon;
    public String category, description, transactionAmount, transactionTime;

    public TransactionDataModel(int cardIcon, String category, String description, String transactionAmount, String transactionTime) {
        this.cardIcon = cardIcon;
        this.category = category;
        this.description = description;
        this.transactionAmount = transactionAmount;
        this.transactionTime = transactionTime;
    }

    public int getCardIcon() {
        return cardIcon;
    }

    public void setCardIcon(int cardIcon) {
        this.cardIcon = cardIcon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }
}
