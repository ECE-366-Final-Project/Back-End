package local.Casino.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountTest {
 
    private Account account;
    
    @BeforeEach
    public void setUp() {
        int ID = 1;
        String username = "testuser";
        account = new Account(ID, username);
    }
    
    @Test
    public void testGetBalance() {
        double expectedBalance = 0.0;
        double actualBalance = account.getBalance();
        Assertions.assertEquals(expectedBalance, actualBalance);
    }
    
    @Test
    public void testGetUsername() {
        String expectedUsername = "testuser";
        String actualUsername = account.getUsername();
        Assertions.assertEquals(expectedUsername, actualUsername);
    }
    
    @Test
    public void testIncreaseBalance() {
        double increment = 100.0;
        double expectedBalance = 100.0;
        double actualBalance = account.increaseBalance(increment);
        Assertions.assertEquals(expectedBalance, actualBalance);
    }
    
    @Test
    public void testDecreaseBalance() {
        double decrement = 50.0;
        double expectedBalance = -50.0;
        double actualBalance = account.decreaseBalance(decrement);
        Assertions.assertEquals(expectedBalance, actualBalance);
    }

}
