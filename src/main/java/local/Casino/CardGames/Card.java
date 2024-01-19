package main.java.local.Casino.CardGames;

import java.util.Arrays;

public class Card {

    public static enum Suit {
        DIAMONDS,
        CLUBS,
        HEARTS,
        SPADES;

        public static final Suit[] SUITS = values();
    }

    public static enum Value {
        KING,
        QUEEN,
        JACK,
        TEN,
        NINE,
        EIGHT,
        SEVEN,
        SIX,
        FIVE,
        FOUR,
        THREE,
        TWO,
        ACE;

        public static final Value[] VALUES = values();
    }

    public final Value cardValue;
    public final Suit cardSuit;

    public Card(Value cardValue, Suit cardSuit) {
        this.cardValue = cardValue;
        this.cardSuit = cardSuit;
    }



    // https://www.scaler.com/topics/object-to-string-java/
    @Override
    public String toString() {

        String strSuit;
        String strValue;

        switch (cardSuit) {
            case HEARTS:
                strSuit = "Hearts";
                break;
            case SPADES:
                strSuit = "Spades";
                break;
            case DIAMONDS:
                strSuit = "Diamonds";
                break;
            case CLUBS:
                strSuit = "Clubs";
                break;
            default:
                strSuit = "INVALID SUIT";
                break;
        }

        switch (cardValue) {
            case TWO:
                strValue = "Two";
                break;
            case THREE:
                strValue = "Three";
                break;
            case FOUR:
                strValue = "Four";
                break;
            case FIVE:
                strValue = "Five";
                break;
            case SIX:
                strValue = "Six";
                break;
            case SEVEN:
                strValue = "Seven";
                break;
            case EIGHT:
                strValue = "Eight";
                break;
            case NINE:
                strValue = "Nine";
                break;
            case TEN:
                strValue = "Ten";
                break;
            case JACK:
                strValue = "Jack";
                break;
            case QUEEN:
                strValue = "Queen";
                break;
            case KING:
                strValue = "King";
                break;
            case ACE:
                strValue = "Ace";
                break;
            default:
                strValue = "INVALID VALUE";
                break;
        }

        return strValue + " of " + strSuit;
    }

}