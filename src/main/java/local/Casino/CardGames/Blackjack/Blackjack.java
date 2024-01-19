package main.java.local.Casino.CardGames.Blackjack;

import main.java.local.Casino.CardGames.Card;
import main.java.local.Casino.CardGames.Deck;

import main.java.local.Casino.Account;

public class Blackjack {

    private static final int MAX_PLAYER_COUNT = 6;

    private final static int DEALER_MIN_STAND = 18;

    final Account[] players;
    final private int playerCount;
    public Deck deck;
    public Card[] dealersCards;
    
    public Blackjack(Account[] players) throws Exception {
        if (players.length > 6) {
            throw new Exception("ERROR (Blackjack): Invalid Player Count (Exceeds Maximum of " + MAX_PLAYER_COUNT + ").");
        } else if (players.length <= 0) {
            throw new Exception("ERROR (Blackjack): Invalid Player Count (Not Enough Players).");
        }
        this.playerCount = players.length;
        this.players = players;
        deck = new Deck();
        deck.shuffle();


    }


}