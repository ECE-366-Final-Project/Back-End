package main.java.local.Casino.CardGames;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

enum Suit {
    HEARTS,
    SPADES,
    DIAMONDS,
    CLUBS;

    private static final List<Suit> SUITS = Collections.unmodifiableList(Arrays.asList(values()));
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
    JACK,
    QUEEN,
    KING,
    ACE;

    private static final List<Value> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
}

public class Card {

    public final Value cardValue;
    public final Suit cardSuit;

    public Card(Value cardValue, Suit cardSuit) {
        this.cardValue = cardValue;
        this.cardSuit = cardSuit;
    }

}