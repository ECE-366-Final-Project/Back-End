package local.API;

import java.util.Random;
import java.util.Optional;

public class BlackjackGame {

    final private static int TIME_TO_LIVE_SECONDS = 240;

    final private static String NEW_DECK = "AH2H3H4H5H6H7H8H9H1HJHQHKHAC2C3C4C5C6C7C8C9C1CJCQCKCAD2D3D4D5D6D7D8D9D1DJDQDKDAS2S3S4S5S6S7S8S9S1SJSQSKS";

    Random rand = new Random();

    private static final int DEALER_MIN_STAND = 17;

    private long timeToKill_SECONDS = 0;
    public final String username;
    public double bet;
    private String deck;
    private String playersCards;
    private String dealersCards;

    public BlackjackGame next;
    public BlackjackGame prev;

    public BlackjackGame(String username, Optional<String> deck, Optional<String> playersCards, Optional<String> dealersCards, double bet) {
        // If not provided with deck, playersCards AND dealersCards, the constructor will reset the game
        timeToKill_SECONDS = (System.currentTimeMillis() / 1000L) + TIME_TO_LIVE_SECONDS;
        this.username = username;
        this.bet = bet;
        if (deck.isPresent() && playersCards.isPresent() && dealersCards.isPresent()) {
            this.deck = deck.get();
            this.playersCards = playersCards.get();
            this.dealersCards = dealersCards.get();
            return;
        }
        resetGame();
    }

    public String deal(int size) {
        if (2*size > deck.length()) {
            return null;
        }
        String cards = deck.substring(0, 2*size);
        deck = deck.substring(2*size);
        return cards;
    }

    public String dealPlayer(int size) {
        String card = deal(1);
        playersCards += card;
        return card;
    }

    private static int valueOf(char rank) {
        switch (rank) {
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case '1':
                return 10;
            case 'J':
                return 10;
            case 'Q':
                return 10;
            case 'K':
                return 10;
            default:
                return 0;
        }
    }

    public static int getScore(String hand) {
        int score = 0;
        int aceCount = 0;
        for (int i = 0; i < hand.length(); i += 2) {
            char rank = hand.charAt(i);
            if (rank != 'A') {
                score += valueOf(rank);
            } else {
                aceCount++;
            }
        }
        score += 11*aceCount;
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }
        return score;
    }

    public int resolveDealersHand() {
        while (getScore(dealersCards) < DEALER_MIN_STAND) {
            dealersCards += deal(1);
        }
        return getScore(dealersCards);
    }

    private void resetGame() {
        deck = NEW_DECK;
        shuffleDeck();
        playersCards = deal(2);
        dealersCards = deal(2);
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() / 1000L) > timeToKill_SECONDS;
    }

    public void resetTimeToKill() {
        timeToKill_SECONDS = (System.currentTimeMillis() / 1000L) + TIME_TO_LIVE_SECONDS;
    }

    
    public void shuffleDeck() {
        char[] charArr = deck.toCharArray();
        int r;
        char tempRank, tempSuit;
        for (int i = 0; i < deck.length()/2; i++) {

            tempRank = charArr[2*i];
            tempSuit = charArr[2*i+1];

            r = rand.nextInt(deck.length()/2);

            charArr[2*i] = charArr[2*r];
            charArr[2*i+1] = charArr[2*r+1];

            charArr[2*r] = tempRank;
            charArr[2*r+1] = tempSuit;
        }
        deck = new String(charArr);
    }

    public String getDeck() {
        return deck;
    }

    public String getPlayersCards() {
        return playersCards;
    }

    public String getDealersCards() {
        return dealersCards;
    }
}