package local.Casino.CardGames.Blackjack;

import local.Casino.CardGames.Card;
import local.Casino.CardGames.Deck;
import local.Casino.Account.Account;

public class Blackjack {

    boolean gameOver = false;

    private final static int DEALER_MIN_STAND = 18;

    final Account player;
    final int bet;
    public Deck deck;
    public Card[] dealerCards;
    public Card[] playerCards;

    int playerCardCount = 0;
    int dealerCardCount = 0;
    
    public Blackjack(Account player, int bet) {

        dealerCards = new Card[11];
        playerCards = new Card[11];

        this.player = player;
        this.bet = bet;
        deck = new Deck();
        deck.shuffle();

        playerCards[playerCardCount++] = deck.deal();
        playerCards[playerCardCount++] = deck.deal();

        dealerCards[dealerCardCount++] = deck.deal();
        dealerCards[dealerCardCount++] = deck.deal();

    }


    int getCardValue(Card card) {
        switch (card.cardValue) {
            case Card.Value.TWO:
                return 2;
            case Card.Value.THREE:
                return 3;
            case Card.Value.FOUR:
                return 4;
            case Card.Value.FIVE:
                return 5;
            case Card.Value.SIX:
                return 6;
            case Card.Value.SEVEN:
                return 7;
            case Card.Value.EIGHT:
                return 8;
            case Card.Value.NINE:
                return 9;
            case Card.Value.TEN:
                return 10;
            case Card.Value.JACK:
                return 10;
            case Card.Value.QUEEN:
                return 10;
            case Card.Value.KING:
                return 10;
            case Card.Value.ACE:
                return 11;
            default:
                return 0;
        }
    }

    // returns true if game is over 
    boolean hit() {
        playerCards[playerCardCount++] = deck.deal();
        if (getTotal(playerCards, playerCardCount) >= 21) {
            while (getTotal(dealerCards, dealerCardCount) < DEALER_MIN_STAND) {
                dealerCards[dealerCardCount++] = deck.deal();
            }
            gameOver = true;
            return true;
        }
        return false;
    }

    // returns true always since game is always over
    boolean stand() {
        while (getTotal(dealerCards, dealerCardCount) < DEALER_MIN_STAND) {
            dealerCards[dealerCardCount++] = deck.deal();
        }
        gameOver = true;
        return true;
    }

    int getTotal(Card[] cards, int cardCount) {
        int total = 0;
        int aceCount = 0;
        for (int i = 0; i < cardCount; i++) {
            if (cards[i].cardValue != Card.Value.ACE) {
                total += getCardValue(cards[i]);
            } else {
                aceCount++;
            }
        }
        if (total + (aceCount*11) <= 21 || aceCount == 0) {
            return total + (aceCount*11);
        }

        // resolve aces that need to be 1
        while (total + (aceCount*11) > 21 && aceCount > 0) {
            total++;
            aceCount--;
        }

        return total + (aceCount*11);
    }

    double getPayout() {
        int dealerTotal = getTotal(dealerCards, dealerCardCount);
        int playerTotal = getTotal(playerCards, playerCardCount);
        if (playerTotal > 21) {
            return 0;
        }
        if (playerTotal == 21) {
            if (dealerTotal == 21) {
                return 1.0;
            }
            return 2.5;
        }
        if (playerTotal > dealerTotal) {
            return 2.0;
        }
        return 0.0;
    }

}