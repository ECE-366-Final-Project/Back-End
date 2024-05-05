//	Roulette Logic Package

package local.Casino;

import local.Casino.RouletteBetPair;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Iterator;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;

import java.sql.*;

	

public class Roulette {

	private static final String DB = System.getenv("POSTGRES_DB");
	private static final String DB_URL ="jdbc:postgresql://db:5432/"+DB;
	private static final String USER = System.getenv("POSTGRES_USER");
	private static final String PASS = System.getenv("POSTGRES_PASSWORD");
	
	private LinkedList<RouletteBetPair> pairlist = new LinkedList<RouletteBetPair>();

	// On class initialization, we get unique rolled number. 
	// A map of stats are generated for the given Roll, whether it "wins"
	// 	on a specified bet
	private Roll rolledNumber = Roll.spinWheel(); 
	private HashMap<String, Integer> rolledStats = new HashMap<String, Integer>();	
	//		A variable bet is a bet which depends on the SPECIFIC number you roll.
	// 		A lump bet is a bet which bets on a specific QUALITY (red, first
	// 		dozen, etc) and NOT ON Specific numbers.
	private static final String[] variableBets = {"single", "split", "horizontal",
	 												"vertical"};
	private static final String[] lumpedBets = {"red", "black", "first_half", 
												"second_half", "first_dozen",
												"second_dozen", "third_dozen"};

	private boolean failedToGenerate = false;

	private double totalPayout = -1.0;
	private double totalBet = -1.0;
	

	public Roulette(String body){
		if(!loadBody(body)){
			// catch an invalid json load
			failedToGenerate = true;
		}
		
		return;
	}
	
	public void runGame(){
		generateRollStats();

		totalPayout = 0.0;
		// generate payouts based on input
		for(RouletteBetPair pair : pairlist){
			totalPayout += pair.getBetValue() * didBetWin(pair) * 
				fetchMultiplier(pair);
		}

		System.out.println("Payout: " + totalPayout);

		return;
	}

	public double returnWinnings(){
		return totalPayout;
	}

	public int returnRoll(){
		return rolledNumber.toInt();
	}

	public double returnTotalBet(){
		return totalBet;
	}

	private boolean loadBody(String rawBody){
		totalBet = 0.0;
		try {
			JSONObject jo = new JSONObject(rawBody);
			// parse variable bets
			for (String betType : variableBets){
				JSONObject jo_betType = jo.getJSONObject(betType);
				try{
					for (Iterator key=jo_betType.keys(); key.hasNext(); ) {
						String betMade = (String) key.next();
						double betAmt = jo_betType.getDouble(betMade);
					
						if(betAmt < 0.0){
							return false;
						}

						String[] numbersBetOn = { betMade };
						switch (betType){
							case "split":
								numbersBetOn = betMade.split("[.]");
						}

						RouletteBetPair bet = new RouletteBetPair(numbersBetOn, 
							betAmt, betType);
						System.out.println(bet.toString());
						pairlist.add(bet);
						totalBet += betAmt;
					}
				} catch(JSONException e) {
					continue;
				}
			}

			// parse lumped bets
			for(String betMade : lumpedBets){
				double betAmt = 0.0;
				try {
					betAmt = jo.getDouble(betMade);
				} catch(JSONException e) {
					continue;
				}
				
				if(betAmt < 0.0){
					return false;
				}

				String[] numbersBetOn = { betMade };

				RouletteBetPair bet = new RouletteBetPair(numbersBetOn, betAmt, 
					"LUMP");
				System.out.println(bet.toString());
				pairlist.add(bet);
				totalBet += betAmt;
			}
		} catch(JSONException e) {
			System.out.println("INVALID REQUEST: " + rawBody);
			return false;
		} 
		return true;
	}

	// 	Generates a list of stats useful for checking if a lumped bet is valid
	private void generateRollStats(){
		System.out.println("PROCESSING BET: " + rolledNumber.toString());
		for(String betType : lumpedBets){
			switch (betType) {
				case "red":
					if(rolledNumber.rollColor() == 1){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;
				case "black":
					if(rolledNumber.rollColor() != 1){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;			
				case "first_half":
					if(rolledNumber.toInt() <= 18){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;
				case "second_half":					
					if(rolledNumber.toInt() > 18){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;
				case "first_dozen":
					if(rolledNumber.toInt() <= 12){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;
				case "second_dozen":
					if(rolledNumber.toInt() >= 13 && rolledNumber.toInt() < 25){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;
				case "third_dozen":
					if(rolledNumber.toInt() >= 25){
						rolledStats.put(betType, 1);
					} else {
						rolledStats.put(betType, 0);
					}
					System.out.println(betType + " " + rolledStats.get(betType).toString());
					break;
				default:
					// erm... unimplemented!
					break;
			}
		}
	}

	// 	0 - no win
	// 	1 - win
	// BUG TODO: FIX HORIZONTAL AND VERTICAL BETS!!!
	private int didBetWin(RouletteBetPair pair){
		// 	checks the pair's betType
		// 	condition for variable bets: pair betNums must have at least
		// 		one value which equals our rolled number.
		

		switch (pair.getBetType()) {
			case "LUMP":
				return rolledStats.getOrDefault(pair.getBetType(), 0);
			case "single":
				if(pair.getBetNums()[0].equals(rolledNumber.toString())){
					return 1;
				}
				return 0;
			case "split":
				for(String betNum : pair.getBetNums()){
					if(betNum.equals(rolledNumber.toString())){
						return 1;
					}
				}
				return 0;
			case "horizontal":
				if(pair.getBetNums()[0].equals(horizontalIndex(rolledNumber.toString()))){
					return 1;
				}
				return 0;
			case "vertical":
				if(pair.getBetNums()[0].equals(verticalIndex(rolledNumber.toString()))){
					return 1;
				}
				return 0;
			default:
				return 0;
		}
	}

	private int horizontalIndex(String betNum){
		int bet = Integer.parseInt(betNum);
		if(bet == 0 | bet == 37) {
			return 0;
		}
		return (bet / 3) + 1;
	}

	private int verticalIndex(String betNum){
		int bet = Integer.parseInt(betNum);
		if(bet == 0 | bet == 37) {
			return 0;
		}
		return (bet / 12) + 1;
	}

	// TODO make this a private static final map
	private double fetchMultiplier(RouletteBetPair pair){
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣿⣿⣿⣿⣮⣝⡯⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⢼⣧⣿⣿⣿⡿⠻⠿⢿⣯⣿⣮⣀⡁⢑⡀⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣾⣿⡟⠁⠀⠙⠧⠞⠈⢓⣿⣿⣿⣿⢿⣾⣷⡀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⣫⠟⠀⠀⠀⠀⠀⠀⠀⠀⡙⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠢⢈⢻⣿⣿⣿⣿⡇⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠠⣿⠁⠚⣛⣒⠀⠀⠀⡀⠐⢒⡒⠳⠤⢺⣟⣿⣿⡇⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠋⠀⠋⠚⠛⠃⢈⣩⣓⢮⠿⠯⠷⠀⠀⢽⣿⠏⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⡜⠄⠀⠀⠀⠀⠀⠀⢸⠩⠀⠀⠀⠀⠀⠀⠀⢻⡤⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠔⡸⡎⠀⠀⠀⠀⠀⠀⠀⠀⠠⠁⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠂⠀⠀⠀⠀⠈⠀⡳⣿⠆⠄⠀⠀⠁⠀⠠⠀⠀⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠘⠤⡔⢎⣵⣸⢯⠜⠀⠀⠀⠀⡀⠀⠀⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣇⠀⣠⣆⣿⣿⣾⣹⣏⢳⣄⡀⠀⠀⠀⠃⠀⠀⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⠀⠀⠀⠀⠀⠠⣸⠤⠋⠙⠓⠶⠖⣾⠾⠟⠋⢣⣲⣦⡾⠀⠀⠀⠀⠀⠀⠀⠀
//		⠀⠀⠀⠀⢀⣤⣶⣶⣾⣽⣿⢷⠀⢈⠃⢙⠃⠀⠀⠀⢐⡾⣾⡿⠃⠀⠀⠠⣄⠀⠀⠀⠀
//		⠀⢀⣤⣾⣿⣿⣿⣿⣿⣿⣯⣆⢣⣑⣄⠴⡇⣽⣦⣢⣾⣾⠋⡀⠐⠀⠁⢀⣿⣷⣄⠀⠀
//		⣰⣿⣿⣿⣿⣿⣿⣿⣿⣿⣻⣗⣉⣛⣿⣶⣟⣿⣿⣛⣁⣐⣀⣀⣀⣠⣶⣿⣡⣨⣟⣑⣢
		switch (pair.getBetType()) {
			case "single":
				return 37.0;
			case "split":
				return 18.0;
			case "horizontal":
				return 11.66666666667;
			case "vertical":
				return 2.166666666667;
			case "red":
				return 1.111111111111;
			case "black":
				return 1.111111111111;
			case "first_half":
				return 1.111111111111;
			case "second_half":
				return 1.111111111111;
			case "first_dozen":
				return 2.166666666667;
			case "second_dozen":
				return 2.166666666667;
			case "third_dozen":
				return 2.166666666667;
			default:
				return -1.0;
		}
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
		
		// Returns Enum int as string.
		// God left me today.
		public String toString(){
			return Integer.toString(this.toInt());
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
