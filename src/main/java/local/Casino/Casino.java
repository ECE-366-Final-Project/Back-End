package Casino;

import java.util.Set;
import java.util.HashSet;

public class Casino {

    private Account[] accounts;
    private int numAccounts = 0;
    private int maxAccounts;
    private int nextID = 0;
    private Set<String> usernames;

    public Casino(int maxAccounts){
        accounts = new Account[maxAccounts];
        this.maxAccounts = maxAccounts;
        usernames = new HashSet<String>();
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
        accounts[numAccounts++] = new Account(getNextID(), username);
        return 0;
    }

    public int getNextID() {
        nextID++;
        return nextID-1;
    }
}
