package local.Casino.Account.Transaction;
import local.Casino.Account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaymentTest {

    private Account sender;
    private Account receiver;
    @BeforeEach
    public void setUp() {
        sender = new Account(1, "sender");
        receiver = new Account(2, "receiver");
        int amountSent = 100;
        new Payment(amountSent, sender, receiver);
    }

    @Test
    public void testConstructor() {
    }
}
