package main.java.local.Casino.CardGames.Blackjack;

import main.java.local.Casino.CardGames.Card;
import main.java.local.Casino.CardGames.Deck;

import main.java.local.Casino.Account;

public class Blackjack {

    int DEALER_MIN_STAND = 18;

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