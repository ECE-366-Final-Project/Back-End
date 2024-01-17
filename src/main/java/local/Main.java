package main.java.local;

import main.java.local.Casino.Casino;
import main.java.local.Casino.Account;

public class Main {
    public static void main(String[] args) {

      Casino house = new Casino(10);
      house.createAccount("Evan R");

      Account evan = house.getAccount("Evan R"); 
      int balance = evan.getBalance();

      System.out.println(balance);

    }
}
