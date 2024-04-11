package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import local.API.Responses.InternalResponse;

public class UpdateBlackjackResponse extends InternalResponse {

	private String GAME_RESULT = "N/A";

	private String WINNER = "N/A";

	private int PLAYER_SCORE = -1;

	private int DEALER_SCORE = -1;

	private double PAYOUT = -1;

	private boolean GAME_ENDED = false;

	private String PLAYERS_CARDS = "";

	private String DEALERS_CARDS = "";

	public String get_GAME_RESULT() {
		return GAME_RESULT;
	}
	public void set_GAME_RESULT(String GAME_RESULT) {
		this.GAME_RESULT = GAME_RESULT;
	}

	public String get_WINNER() {
		return WINNER;
	}
	public void set_WINNER(String WINNER) {
		this.WINNER = WINNER;
	}

	public int get_PLAYER_SCORE() {
		return PLAYER_SCORE;
	}
	public void set_PLAYER_SCORE(int PLAYER_SCORE) {
		this.PLAYER_SCORE = PLAYER_SCORE;
	}

	public int get_DEALER_SCORE() {
		return DEALER_SCORE;
	}
	public void set_DEALER_SCORE(int DEALER_SCORE) {
		this.DEALER_SCORE = DEALER_SCORE;
	}

	public double get_PAYOUT() {
		return PAYOUT;
	}
	public void set_PAYOUT(double PAYOUT) {
		this.PAYOUT = PAYOUT;
	}

	public boolean get_GAME_ENDED() {
		return GAME_ENDED;
	}
	public void set_GAME_ENDED(boolean GAME_ENDED) {
		this.GAME_ENDED = GAME_ENDED;
	}

	public String get_PLAYERS_CARDS() {
		return PLAYERS_CARDS;
	}
	public void set_PLAYERS_CARDS(String PLAYERS_CARDS) {
		this.PLAYERS_CARDS = PLAYERS_CARDS;
	}

	public String get_DEALERS_CARDS() {
		return DEALERS_CARDS;
	}
	public void set_DEALERS_CARDS(String DEALERS_CARDS) {
		this.DEALERS_CARDS = DEALERS_CARDS;
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		jo.put("GAME_RESULT", this.get_GAME_RESULT());
		jo.put("WINNER", this.get_WINNER());
		jo.put("PLAYER_SCORE", this.get_PLAYER_SCORE());
		jo.put("DEALER_SCORE", this.get_DEALER_SCORE());
		jo.put("PAYOUT", this.get_PAYOUT());
		jo.put("GAME_ENDED", this.get_GAME_ENDED());
		jo.put("PLAYERS_CARDS", this.get_PLAYERS_CARDS());
		jo.put("DEALERS_CARDS", this.get_DEALERS_CARDS());
		return new ResponseEntity<String>(jo.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}
}