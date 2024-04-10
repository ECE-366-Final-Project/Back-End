package local.API.Responses;
import org.springframework.http.ResponseEntity;

public class NewBlackjackReponse extends InternalResponse {

	private int PLAYER_SCORE = -1;
	private String PLAYERS_CARDS = "";
	private String DEALERS_CARDS = "";

	public NewBlackjackReponse() {
		super();
	}

	public int getPLAYER_SCORE() {
		return PLAYER_SCORE;
	}
	public void setPLAYER_SCORE(int PLAYER_SCORE) {
		this.PLAYER_SCORE = PLAYER_SCORE;
	}

	public String getPLAYERS_CARDS() {
		return PLAYERS_CARDS;
	}
	public void setPLAYERS_CARDS(String PLAYERS_CARDS) {
		this.PLAYERS_CARDS = PLAYERS_CARDS;
	}

	public String getDEALERS_CARDS() {
		return DEALERS_CARDS;
	}
	public void setDEALERS_CARDS(String DEALERS_CARDS) {
		this.DEALERS_CARDS = DEALERS_CARDS;
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", MESSAGE);
		jo.put("PLAYER_SCORE", PLAYER_SCORE);
		jo.put("PLAYERS_CARDS", PLAYERS_CARDS);
		jo.put("DEALERS_CARDS", DEALERS_CARDS);
		jo.put("GAME_ENDED", GAME_ENDED);
		return new ResponseEntity<String>(jo.toString(), responseCode);
	}
}