package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import local.API.Responses.InternalResponse;

public class GetBalResponse extends InternalResponse {

	private double BALANCE = -1.0;

	public double get_BALANCE() {
		return BALANCE;
	}
	public void set_BALANCE(double BALANCE) {
		this.BALANCE = BALANCE;
	}

	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		jo.put("BALANCE", this.get_BALANCE());
		return jo;
	}

	public ResponseEntity<String> toResponseEntity() {
		return new ResponseEntity<String>(this.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}

	public String toString() {
		return this.getJSON().toString();
	}
}