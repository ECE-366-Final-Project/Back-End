package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import local.API.Responses.InternalResponse;

public class PlaySlotsResponse extends InternalResponse {

	private int PAYOUT_ID = -1;

	private double PAYOUT = -1.0;

	private double WINNINGS = -1.0;

	public int get_PAYOUT_ID() {
		return PAYOUT_ID;
	}
	public void set_PAYOUT_ID(int PAYOUT_ID) {
		this.PAYOUT_ID = PAYOUT_ID;
	}

	public double get_PAYOUT() {
		return PAYOUT;
	}
	public void set_PAYOUT(double PAYOUT) {
		this.PAYOUT = PAYOUT;
	}

	public double get_WINNINGS() {
		return WINNINGS;
	}
	public void set_WINNINGS(double WINNINGS) {
		this.WINNINGS = WINNINGS;
	}

	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		jo.put("PAYOUT_ID", this.get_PAYOUT_ID());
		jo.put("PAYOUT", this.get_PAYOUT());
		jo.put("WINNINGS", this.get_WINNINGS());
		return jo;
	}

	public ResponseEntity<String> toResponseEntity() {
		return new ResponseEntity<String>(this.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}

	public String toString() {
		return this.getJSON().toString();
	}
}