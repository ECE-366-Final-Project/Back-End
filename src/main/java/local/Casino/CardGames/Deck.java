package main.java.local.Casino.CardGames;

import java.util.Random;

public class Deck {

    public Card[] mainDeck;
    private int numCards = 52;

    public Deck() {
        mainDeck = new Card[52];
        resetDeck();
    }

    public void resetDeck() {
        numCards = 52;
        int pos = 0;
        for (int s = 0; s < Card.Suit.SUITS.length; s++) {
            for (int v = 0; v < Card.Value.VALUES.length; v++) {
                mainDeck[pos++] = new Card(Card.Value.VALUES[s], Card.Suit.SUITS[v]);
            }
        }
    }

    public void shuffle() {
        // https://www.digitalocean.com/community/tutorials/shuffle-array-java
        Random rand = new Random();
		for (int i = 0; i < mainDeck.length; i++) {
			int randomIndexToSwap = rand.nextInt(mainDeck.length);
			Card temp = mainDeck[randomIndexToSwap];
			mainDeck[randomIndexToSwap] = mainDeck[i];
			mainDeck[i] = temp;
		}
    }

    // deals the top card
    public Card deal() {
        if (numCards > 0) {
            return mainDeck[--numCards];
        }
        return null;
    }

}