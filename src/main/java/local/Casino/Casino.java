package main.java.local.Casino;

import main.java.local.Casino.Slots.Slots;

import java.util.Set;
import java.util.HashSet;

import java.util.HashMap;

public class Casino {

    private Account[] accounts;
    private int numAccounts = 0;
    private int maxAccounts;
    private int nextID = 0;
    private HashSet<String> usernames;
    private HashMap<String, Integer> userIndex;

    public Casino(int maxAccounts){
        accounts = new Account[maxAccounts];
        this.maxAccounts = maxAccounts;
        usernames = new HashSet<String>();
        userIndex = new HashMap<String, Integer>();
    }

    // TODO
    private boolean isValidUsername(String username) {
        if (usernames.contains(username)) {
            return false;
        } 
        return true;
    }

    // RETURNS:
    //  0: OK
    //  1: MAX ACCOUNTS REACHED
    //  2: INVALID USERNAME
    public int createAccount(String username) {
        if (numAccounts >= maxAccounts) {
            return 1;
        }
        if (!isValidUsername(username)) {
            return 2;
        }
        accounts[numAccounts] = new Account(getNextID(), username);
        usernames.add(username);
        userIndex.put(username, numAccounts);
        numAccounts++;
        return 0;
    }

    public int getNextID() {
        nextID++;
        return nextID-1;
    }

    public Account getAccount(String username) {
        if (usernames.contains(username)) {  
            return accounts[userIndex.get(username)];
        }
        return null;
    }

}
