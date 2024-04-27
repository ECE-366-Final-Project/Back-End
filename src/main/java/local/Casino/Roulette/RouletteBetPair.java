package local.Casino;

public class RouletteBetPair {
	public String[] betNum; // LUMP (reds, dozen, etc), 1.2, 1, 37, etc
	public Float betValue; // vbucks bet
	public String betType; //single, LUMP, split, etc

	public RouletteBetPair(String[] bN, float bV, String bT){
		betNum = bN;
		betValue = bV;
		betType = bT;
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
