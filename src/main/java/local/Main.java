package main.java.local;

import main.java.local.Casino.Casino;
import main.java.local.Casino.Account.Account;
import main.java.local.Casino.Slots.Slots;

import main.java.local.Casino.CardGames.Card;
import main.java.local.Casino.CardGames.Deck;

public class Main {

    public static void main(String[] args) {

        // TESTING SLOTS

        Casino house = new Casino(10);
        house.createAccount("Evan R");

        Account evan = house.getAccount("Evan R");

        if (evan != null) {

        evan.increaseBalance(1000.0);
        System.out.printf("Balance: %.2f\n", evan.getBalance());
            
        } else {
            System.out.println("NULL");
        }

        Slots.Symbol[] rolls = Slots.playSlots();

        for (int i = 0; i < Slots.NUMBER_OF_ROLLS; i++) {
            System.out.printf("Roll #%d: %s\n", i+1, rolls[i]);
        }
        
        double bet = 100.0;

        evan.decreaseBalance(bet);

        double winnings = Slots.getWinnings(rolls, bet);

        System.out.printf("Winnings: %.2f\n", winnings);

        evan.increaseBalance(winnings);

        System.out.printf("Balance: %.2f\n", evan.getBalance());




        // TESTING BLACKJACK

        Deck myDeck = new Deck();
        System.out.println("NEW DECK:");
        System.out.println(myDeck.deal().toString());
        System.out.println(myDeck.deal().toString());
        System.out.println(myDeck.deal().toString());

        myDeck.shuffle();

        System.out.println("SHUFFLED DECK:");
        System.out.println(myDeck.deal().toString());
        System.out.println(myDeck.deal().toString());
        System.out.println(myDeck.deal().toString());

        myDeck.resetDeck();

        System.out.println("RESET DECK:");
        System.out.println(myDeck.deal().toString());
        System.out.println(myDeck.deal().toString());
        System.out.println(myDeck.deal().toString());

    }

}
