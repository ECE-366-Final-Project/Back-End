package main.java.local.Casino;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Slots {

    final static int NUMBER_OF_ROLLS = 3;

    public enum Symbol {

        DIAMOND,
        LEMON,
        WATERMELON,
        HEART,
        SEVEN,
        BELL,
        HORSESHOE,
        CHERRY,
        SINGLE_BAR,
        DOUBLE_BAR,
        TRIPLE_BAR;

        // https://stackoverflow.com/a/1972399
        private static final List<Symbol> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Symbol randomSymbol()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

    }

    public static int getWinnings(Symbol[] rolls, int bet) {
        // TODO
        for (int i = 0; i < NUMBER_OF_ROLLS; i++) {
            if (rolls[i] == Symbol.SEVEN) {
                return 3*bet;
            }
        }
        return bet/2;
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
