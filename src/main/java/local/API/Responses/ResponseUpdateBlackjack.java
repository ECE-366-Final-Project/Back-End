package local.API.Responses;
import org.springframework.http.ResponseEntity;

public class UpdateBlackjackReponse extends InternalResponse {

	private String GAME_RESULT = "N/A";
	private String WINNER = "N/A";
	private int PLAYER_SCORE = -1;
	private int DEALER_SCORE = -1;
	private double PAYOUT = -1;

	public UpdateBlackjackReponse() {
		super();
	}

	public String getGAME_RESULT() {
		return GAME_RESULT;
	}
	public void setGAME_RESULT(String GAME_RESULT) {
		this.GAME_RESULT = GAME_RESULT;
	}

	public String getWINNER() {
		return WINNER;
	}
	public void setWINNER(String WINNER) {
		this.WINNER = WINNER;
	}

	public int getPLAYER_SCORE() {
		return PLAYER_SCORE;
	}
	public void setPLAYER_SCORE(int PLAYER_SCORE) {
		this.PLAYER_SCORE = PLAYER_SCORE;
	}

	public int getDEALER_SCORE() {
		return DEALER_SCORE;
	}
	public void setDEALER_SCORE(int DEALER_SCORE) {
		this.DEALER_SCORE = DEALER_SCORE;
	}

	public double getPAYOUT() {
		return PAYOUT;
	}
	public void setPAYOUT(double PAYOUT) {
		this.PAYOUT = PAYOUT;
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", MESSAGE);
		jo.put("GAME_RESULT", GAME_RESULT);
		jo.put("WINNER", WINNER);
		jo.put("PLAYER_SCORE", PLAYER_SCORE);
		jo.put("DEALER_SCORE", DEALER_SCORE);
		jo.put("PAYOUT", PAYOUT);
		jo.put("GAME_ENDED", GAME_ENDED);
		jo.put("PLAYERS_CARDS", PLAYERS_CARDS);
		jo.put("DEALERS_CARDS", DEALERS_CARDS);
		return new ResponseEntity<String>(jo.toString(), responseCode);
	}
}