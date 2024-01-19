package main.java.local.Casino.Card;

public class Card {

    enum Suit {
        HEARTS,
        SPADES,
        DIAMONDS,
        CLUBS;

        private static final List<Symbol> SUITS = Collections.unmodifiableList(Arrays.asList(values()));
    }

    enum Value {
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
        JACK
        QUEEN
        KING
        ACE;

        private static final List<Symbol> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    }

    public final Value cardValue;
    public final Suit cardSuit;

    public Card(Value cardValue, Suit cardSuit) {
        this.cardValue = cardValue;
        this.cardSuit = cardSuit;
    }

}