package main.java.local.Casino.CardGames.Blackjack;

import main.java.local.Casino.CardGames.Card;
import main.java.local.Casino.CardGames.Deck;

import main.java.local.Casino.Account.Account;

import java.util.Scanner; 

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

    // Black-Box function (will get input from buttons most likely)
    private enum Decision {
        HIT,
        STAND,
        DOUBLE,
        SPLIT,
        SURRENDER;
    }
    private Decision hitOrStand() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Decision: ");  
            String decision = scanner.next();

            switch (decision) {
                case "Hit":
                    return Decision.HIT;
                case "Stand":
                    return Decision.STAND;
                case "Double":
                    return Decision.DOUBLE;
                case "Split":
                    return Decision.SPLIT;
                case "Surrender":
                    return Decision.SURRENDER;
                default:
                    break;
            }
        }
    }

}