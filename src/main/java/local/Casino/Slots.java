package main.java.local.Casino;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Slots {

    public enum Symbols {

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
        private static final List<Symbols> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        public static Symbols randomLetter()  {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

    }
    
}
