package local.Casino;
import local.Casino.Account.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CasinoTest {

    private Casino casino;

    @BeforeEach
    public void setUp() {
        casino = new Casino(10);
    }

    @Test
    public void testCreateAccount() {
        int result = casino.createAccount("user1");
        Assertions.assertEquals(0, result);
        Assertions.assertNotNull(casino.getAccount("user1"));
    }

    @Test
    public void testCreateAccount_MaxAccountsReached() {
        for (int i = 0; i < 10; i++) {
            casino.createAccount("user" + i);
        }
        int result = casino.createAccount("user11");
        Assertions.assertEquals(1, result);
        Assertions.assertNull(casino.getAccount("user11"));
    }

    @Test
    public void testCreateAccount_InvalidUsername() {
        casino.createAccount("user1");
        int result = casino.createAccount("user1");
        Assertions.assertEquals(2, result);
    }

    @Test
    public void testGetAccount() {
        casino.createAccount("user1");
        Account account = casino.getAccount("user1");
        Assertions.assertNotNull(account);
        Assertions.assertEquals("user1", account.getUsername());
    }

    @Test
    public void testGetAccount_NonexistentUsername() {
        Account account = casino.getAccount("user1");
        Assertions.assertNull(account);
    }
}
