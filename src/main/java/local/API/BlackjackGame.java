package local.API;

import java.util.Random;
import java.util.Optional;

public class BlackjackGame {

    final static int TIME_TO_LIVE_SECONDS = 240;

    final static String NEW_DECK = "AH2H3H4H5H6H7H8H9HJHQHKHAC2C3C4C5C6C7C8C9CJCQCKCAD2D3D4D5D6D7D8D9DJDQDKDAS2S3S4S5S6S7S8S9SJSQSKS";

    Random rand = new Random();

    private long timeToKill_SECONDS = 0;
    private final int userID;
    private String deck;
    private String playersCards;
    private String dealersCards;

    public BlackjackGame(int userID, Optional<String> deck) {
        timeToKill_SECONDS = (System.currentTimeMillis() / 1000L) + TIME_TO_LIVE_SECONDS;
        this.userID = userID;
        if (deck.isPresent()) {
            this.deck = deck.get();
        } else {
            this.deck = NEW_DECK;
            shuffleDeck();
        }
    }

    public boolean isAlive() {
        return (System.currentTimeMillis() / 1000L) > timeToKill_SECONDS;
    }

    public void resetTimeToKill() {
        timeToKill_SECONDS = (System.currentTimeMillis() / 1000L) + TIME_TO_LIVE_SECONDS;
    }

    public void shuffleDeck() {
        char[] charArr = deck.toCharArray();
        for (int i = 0; i < deck.length(); i++) {
            char temp = charArr[i];
            int r = rand.nextInt(deck.length());
            charArr[i] = charArr[r];
            charArr[r] = charArr[i];
        }
        deck = new String(charArr);
    }
}