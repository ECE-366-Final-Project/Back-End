package local.Casino;

public class RouletteBetPair {
	public String betNum;
	public Float betValue;
	public String betType;

	public RouletteBetPair(String bN, float bV, String bT){
		betNum = bN;
		betValue = bV;
		betType = bT;
	}

	public String toString(){
		return betType + ": " + betNum + ", " + betValue.toString();
	}
}
