package main.java.local.Casino;

public class Account {
    
    private int balance = 0;
    private String username;
    private final int ID;

    public Account(int ID, String username) {
        this.ID = ID;
        this.username = username;
    }

    public int getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }

    protected int getID() {
        return ID;
    }

    public int increaseBalance(int inc) {
        balance += inc;
        return balance;
    }

    public int decreaseBalance(int dec) {
        balance -= dec;
        return balance;
    } 
}
