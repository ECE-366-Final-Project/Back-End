package local.Casino.CardGames;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardTest {

    @Test
    public void testToString() {
        Card card = new Card(Card.Value.ACE, Card.Suit.SPADES);
        String expected = "Ace of Spades";
        String actual = card.toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testToString_InvalidSuit() {
        Card card = new Card(Card.Value.TWO, null);
        String expected = "INVALID SUIT of Two";
        String actual = card.toString();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testToString_InvalidValue() {
        Card card = new Card(null, Card.Suit.HEARTS);
        String expected = "INVALID VALUE of Hearts";
        String actual = card.toString();
        Assertions.assertEquals(expected, actual);
    }
}
