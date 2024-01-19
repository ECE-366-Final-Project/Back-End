package main.java.local.Casino.Blackjack;

import main.java.local.Casino.Blackjack.Card;
import main.java.local.Casino.Blackjack.Deck;

int DEALER_MIN_STAND = 18;

public class Blackjack {

    Account player;
    Deck deck;
    Card[] dealersCards;
    Card[] playerCards;
    
    public Blackjack(Account player) {
        this.player = player;
        deck = new Deck();
        deck.shuffle();


    }


}