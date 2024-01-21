package main.java.local.Casino.Account.Transaction;

import main.java.local.Casino.Account.Account;

public class Payment extends Transaction {

    public final Account sender;
    public final Account receiver;

    public Payment (int amountSent, Account sender, Account receiver) {
        super(amountSent);
        this.type = TransactionType.PAYMENT;
        this.sender = sender;
        this.receiver = receiver;
        sender.addTransaction(this);
        receiver.addTransaction(this);
    }

}