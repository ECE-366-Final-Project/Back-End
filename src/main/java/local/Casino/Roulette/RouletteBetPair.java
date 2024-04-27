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
		return betType + ": " + betNum + ", " + betValue.toString();
	}
}
