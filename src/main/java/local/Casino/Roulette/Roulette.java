//	Roulette Logic Package

package local.Casino.Roulette;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

//	American Roulette
//		
public class Roulette {
	
	//order: greens, reds, blacks
	public enum Roll {
		// this order MUST remain the same.
		ZERO,
		ONE,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		ELEVEN,
		TWELVE,
		THIRTEEN,
		FOURTEEN,
		FIFTEEN,
		SIXTEEN,
		SEVENTEEN,
		EIGHTEEN,
		NINETEEN,
		TWENTY,
		TWENTYONE,
		TWENTYTWO,
		TWENTYTHREE,
		TWENTYFOUR,
		TWENTYFIVE,
		TWENTYSIX,
		TWENTYSEVEN,
		TWENTYEIGHT,
		TWENTYNINE,
		THIRTY,
		THIRTYONE,
		THIRTYTWO,
		THIRTYTHREE,
		THIRTYFOUR,
		THIRTYFIVE,
		THIRTYSIX,
		DOUBLEZERO;
		
		private static final List<Roll> ROLL_OUTCOMES = 
			Collections.unmodifiableList(Arrays.asList(values()));	
		private static final int SIZE = ROLL_OUTCOMES.size();
		private static final Random RANDOM = new Random();


		// Grabs a random outcome, and returns that Roll.
		public static Roll spinWheel() {
			return ROLL_OUTCOMES.get(RANDOM.nextInt(SIZE));
		}

		// Returns the int associated with each enum.
		// Relies on the order in which each enum object is declared!!
		public int toInt() {
			return this.ordinal();
		}
		
		// rollColor
		// determines color of rolled value, given number
		// 0 - green
		// 1 - red
		// 2 - black
		// 
		// -1 for invalid
		public int rollColor() {
			String isRed = "^[13579]$|^[1][24689]$|^[2][1357]$|^[3][0246]$";
			
			// is it green?
			if(this == Roll.ZERO | this == Roll.DOUBLEZERO) {
				return 0;
			}

			// is it red?
			if(Integer.toString(this.toInt()).matches(isRed)) {
				return 1;
			}
			
			// its black.
			return 2;
		}
	
	}
}
