package main.java.local.Casino;

public class Account {
    
    private double balance = 0.0;
    private String username;
    private final int ID;

    public Account(int ID, String username) {
        this.ID = ID;
        this.username = username;
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
    
}
