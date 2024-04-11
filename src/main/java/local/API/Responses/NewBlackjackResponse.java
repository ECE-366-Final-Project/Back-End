package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import local.API.Responses.InternalResponse;

public class NewBlackjackResponse extends InternalResponse {

	private int PLAYER_SCORE = -1;

	private String PLAYERS_CARDS = "";

	private String DEALERS_CARDS = "";

	private boolean GAME_ENDED = false;

	public int get_PLAYER_SCORE() {
		return PLAYER_SCORE;
	}
	public void set_PLAYER_SCORE(int PLAYER_SCORE) {
		this.PLAYER_SCORE = PLAYER_SCORE;
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

	public boolean get_GAME_ENDED() {
		return GAME_ENDED;
	}
	public void set_GAME_ENDED(boolean GAME_ENDED) {
		this.GAME_ENDED = GAME_ENDED;
	}

	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		jo.put("PLAYER_SCORE", this.get_PLAYER_SCORE());
		jo.put("PLAYERS_CARDS", this.get_PLAYERS_CARDS());
		jo.put("DEALERS_CARDS", this.get_DEALERS_CARDS());
		jo.put("GAME_ENDED", this.get_GAME_ENDED());
		return jo;
	}

	public ResponseEntity<String> toResponseEntity() {
		return new ResponseEntity<String>(this.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}

	public String toString() {
		return this.getJSON().toString();
	}
}