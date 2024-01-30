package local.Casino.Account;

import local.Casino.Account.Transaction.Transaction;

import java.util.ArrayList;

public class Account {
    
    private double balance = 0.0;
    private String username;
    private final int ID;

    private ArrayList<Transaction> transactionHistory;

    public Account(int ID, String username) {
        this.ID = ID;
        this.username = username;
        transactionHistory = new ArrayList<Transaction>();
    }

    public double getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }

    protected int getID() {
        return ID;
    }

    public double increaseBalance(double inc) {
        balance += inc;
        return balance;
    }

    public double decreaseBalance(double dec) {
        balance -= dec;
        return balance;
    } 
    
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

}
