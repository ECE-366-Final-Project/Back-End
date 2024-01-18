package main.java.local.Casino;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class Slots {

    final public static int NUMBER_OF_ROLLS = 3; // 3 rolls
    final private static double TRIPLE_PAYOUT = 3.0; // 3x bet (temporary amount)
    final private static double JACKPOT_PAYOUT = 10.0; // 10x bet (temporary amount)
    final private static double PER_DUPLICATE_PAYOUT = 1.25; // 1.5*number of duplicate symbols * bet (temporary amount)

    public enum Symbol {

        DIAMOND,
        LEMON,
        WATERMELON,
        HEART,
        SEVEN,
        BELL,
        HORSESHOE,
        CHERRY,
        JACKPOT;

        // https://stackoverflow.com/a/1972399
        private static final List<Symbol> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Symbol randomSymbol()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

    }

    private static boolean allSymbolsMatch(Symbol[] rolls) {
        for (int i = 0; i < rolls.length; i++) {
            if (rolls[0] != rolls[i]) {
                return false;
            }
        }
        return true;
    }     

    private static int getDuplicatesAmount(Symbol[] rolls) {
        Set<Symbol> uniqueRolledSymbols = new HashSet<Symbol>();
        for (int i = 0; i < NUMBER_OF_ROLLS; i++) {
            uniqueRolledSymbols.add(rolls[i]);
        }
        return NUMBER_OF_ROLLS - uniqueRolledSymbols.size();
    }

    public static double getWinnings(Symbol[] rolls, double bet) {
        return bet*getPayout(rolls);
    }

    private static double getPayout(Symbol[] rolls) {
        if (allSymbolsMatch(rolls)) {
            if (rolls[0] == Symbol.JACKPOT) {
                return JACKPOT_PAYOUT;
            }
            return TRIPLE_PAYOUT;
        }
        return PER_DUPLICATE_PAYOUT*getDuplicatesAmount(rolls);
    }

    private static Symbol[] getRolls() {
        Symbol[] rolls = new Symbol[NUMBER_OF_ROLLS];
        for (int i = 0; i < NUMBER_OF_ROLLS; i++) {
            rolls[i] = Symbol.randomSymbol();
        }
        return rolls;
    }

    public static Symbol[] playSlots() {
        Symbol[] rolls = getRolls();
        return rolls;
    }

}
