package local.Casino.Account.Transaction;

import local.Casino.Account.Account;

public class Withdrawal extends Transaction {

    public final Account sender;

    public Withdrawal(int amountSent, Account sender) {
        super(amountSent);
        this.type = TransactionType.WITHDRAWL;
        this.sender = sender;
        sender.addTransaction(this);
    }
}
    
