package main.java.local.Casino.CardGames;

import java.util.Arrays;

public class Card {

    public static enum Suit {
        HEARTS,
        SPADES,
        DIAMONDS,
        CLUBS;

        public static final Suit[] SUITS = values();
    }

    public static enum Value {
        ONE,
        TWO,
        THREE,
        FOOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE;

        public static final Value[] VALUES = values();
    }

    public final Value cardValue;
    public final Suit cardSuit;

    public Card(Value cardValue, Suit cardSuit) {
        this.cardValue = cardValue;
        this.cardSuit = cardSuit;
    }

}