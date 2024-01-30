package local.Casino.Account.Transaction;

import local.Casino.Account.Account;

public class Deposit extends Transaction {

    private final Account receiver;

    public Deposit (int amountSent, Account receiver) {
        super(amountSent);
        this.type = TransactionType.DEPOSIT;
        this.receiver = receiver;
        receiver.addTransaction(this);
    }

}