package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import local.API.Responses.InternalResponse;

public class LogInResponse extends InternalResponse {

	private String TOKEN = "N/A";

	public String get_TOKEN() {
		return TOKEN;
	}
	public void set_TOKEN(String TOKEN) {
		this.TOKEN = TOKEN;
	}

	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		jo.put("TOKEN", this.get_TOKEN());
		return jo;
	}

	public ResponseEntity<String> toResponseEntity() {
		return new ResponseEntity<String>(this.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}

	public String toString() {
		return this.getJSON().toString();
	}
}