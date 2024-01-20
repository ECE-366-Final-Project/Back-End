package main.java.local.Casino.Account.Transaction;

enum TransactionType {
    DEPOSIT,
    WITHDRAWL,
    PAYMENT;
}

public class Transaction {

    public final Account sender;

    public final int amountSent;

    public Transaction(int amountSent, Account sender) {
        this.sender = sender;
        this.amountSent = amountSent;
    }

}