package local.Casino;

public class RouletteBetPair {
	public String betType;
	public Float betValue;
	
	public RouletteBetPair(String bT, float bV){
		betType = bT;
		betValue = bV;
	}

	public String toString(){
		return betType + " " + betValue.toString() + "\n";
	}
}
