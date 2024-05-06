package local.Casino;

public class RouletteBetPair {
	private String[] betNum; // LUMP (reds, dozen, etc), 1.2, 1, 37, etc
	private Double betValue; // vbucks bet
	private String betType; //single, LUMP, split, etc

	public RouletteBetPair(String[] bN, double bV, String bT){
		betNum = bN;
		betValue = bV;
		betType = bT;
	}

	public String[] getBetNums(){
		return betNum;
	}

	public Double getBetValue(){
		return betValue;
	}

	public String getBetType(){
		return betType;
	}

	public String toString(){
		switch (betType) {
			case "split":
				return betType + ": " + betNum[0] + " & " + betNum[1] + ", " + ", " + betValue.toString();
			default:
				return betType + ": " + betNum[0] + ", " + betValue.toString();
		}
	}
}
