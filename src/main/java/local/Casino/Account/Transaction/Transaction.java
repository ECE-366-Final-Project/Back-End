package local.Casino.Account.Transaction;

import local.Casino.Account.Account;

public abstract class Transaction {

    protected enum TransactionType {
        DEPOSIT, // receiver
        WITHDRAWL, // sender
        PAYMENT; // sender -> receiver
    }

    protected TransactionType type;
    protected int amountSent;

    public Transaction (int amountSent) {
        this.amountSent = amountSent;
    }

}