package main.java.local.Casino.Account.Transaction;

import main.java.local.Casino.Account.Account;

public class Withdrawl extends Transaction {

    public final Account sender;

    public Withdrawl (int amountSent, Account sender) {
        super(amountSent);
        this.type = TransactionType.WITHDRAWL;
        this.sender = sender;
        sender.addTransaction(this);
    }
    
}