//	Roulette Logic Package

package local.Casino;

import local.Casino.RouletteBetPair;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Iterator;

import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

public class Roulette {
	
	
	private LinkedList<RouletteBetPair> pairlist = new LinkedList<RouletteBetPair>();

	private Roll rolledNumber = Roll.spinWheel();

	private boolean failedToGenerate = false;

	public boolean loadBody(String rawBody){

		try {
			JSONObject jo = new JSONObject(rawBody);
			// parse variable bets
			String[] variableBets = {"single", "split", "horizontal", "vertical"};
			for (String betType : variableBets){
				JSONObject jo_betType = jo.getJSONObject(betType);

				for (Iterator key=jo_betType.keys(); key.hasNext(); ) {
					String betMade = (String) key.next();
					float betAmt = jo_betType.getFloat(betMade);
				
					String[] numbersBetOn = { betMade };
					switch (betType){
						case "split":
							numbersBetOn = betMade.split(".", 2);
					}

					RouletteBetPair bet = new RouletteBetPair(numbersBetOn, betAmt, betType);
					System.out.println(bet.toString());
					pairlist.add(bet);
				}
			}

			// parse lumped bets
			String[] lumpedBets = {"red", "black", "first_half", "second_half", "first_dozen", "second_dozen", "third_dozen"};
			for(String betMade : lumpedBets){
				float betAmt = jo.getFloat(betMade);
				String[] numbersBetOn = { betMade };

				RouletteBetPair bet = new RouletteBetPair(numbersBetOn, betAmt, "LUMP");
				System.out.println(bet.toString());
				pairlist.add(bet);
			}
		} catch(JSONException e) {
			e.printStackTrace();
			//return empty list
			return false;
		} 
		return true;
	}

	public Roulette(String body){
		if(!loadBody(body)){
			// catch an invalid json load
			failedToGenerate = true;
		}
		
		// generate payouts based on input


		return;
	}

	public boolean parseFailed(){
		return failedToGenerate;
	}

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
