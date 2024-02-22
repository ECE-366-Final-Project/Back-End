package local.Casino.Account.Transaction;
import local.Casino.Account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WithdrawalTest {

    private Account sender;

    @BeforeEach
    public void setUp() {
        sender = new Account(1, "sender");
        int amountSent = 100;
        withdrawal = new Withdrawal(amountSent, sender);
    }

    @Test
    public void testConstructor() {
    }
}
