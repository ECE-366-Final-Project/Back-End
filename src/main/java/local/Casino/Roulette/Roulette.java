//	Roulette Logic Package

package local.Casino.Roulette;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Roulette {
		
	private Map<String, String> betsMade = null;

	public class RouletteResponse<T> extends InternalResponse<T>{
		public static final int INVALID = -1;

		public returnInvalid(){
			return new InternalResponse<List>(-1, List.of());
		}
		public returnStatus(int c){
			return new InternalResponse<List>(c, List.of());
		}

		public RouletteResponse(int code, T d){
			super(c, d);
		}
	}
	
	// decodes a request to play the game
	// Map gets stored as a private variable
	// 0 - fucked up
	// 1 - yipee
	private int RequestDecode(String requestData){
		TypeToken<Map<String, String>> mapType = new TypeToken<Map<String, String>>(){};
		String json = "{\"key\": \"value\"}";

		betsMade = gson.fromJson(json, mapType);

		return 1;
	}

	//order: greens, reds, blacks
	protected enum Roll {
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
