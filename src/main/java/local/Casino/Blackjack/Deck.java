package main.java.local.Casino.Deck;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    public Card[] MainDeck;
    private int numCards = 52;

    public Deck() {
       resetDeck();
    }

    public void resetDeck() {
        
    }

    public void shuffle() {

    }

    // deals the top card
    public Card deal() {
        if (numCards > 0) {
            return MainDeck[--numCards]
        }
        return null;
    }

}