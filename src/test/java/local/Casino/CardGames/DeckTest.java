package local.Casino.CardGames;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckTest {

    private Deck deck;

    @BeforeEach
    public void setUp() {
        deck = new Deck();
    }

    @Test
    public void testResetDeck() {
        deck.shuffle();
        deck.resetDeck();
        Assertions.assertEquals(52, deck.mainDeck.length);
    }

    @Test
    public void testShuffle() {
        Card[] initialDeck = deck.mainDeck.clone();
        deck.shuffle();

        Assertions.assertNotEquals(initialDeck, deck.mainDeck);
    }

    @Test
    public void testDeal() {
        Card card = deck.deal();
        Assertions.assertNotNull(card);
        Assertions.assertEquals(51, deck.mainDeck.length);
    }

    @Test
    public void testDeal_EmptyDeck() {
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        Card card = deck.deal();
        Assertions.assertNull(card);
    }
}
