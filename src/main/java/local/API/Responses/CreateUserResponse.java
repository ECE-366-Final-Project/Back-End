package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import local.API.Responses.InternalResponse;

public class CreateUserResponse extends InternalResponse {

	public JSONObject getJSON() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		return jo;
	}

	public ResponseEntity<String> toResponseEntity() {
		return new ResponseEntity<String>(this.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}

	public String toString() {
		return this.getJSON().toString();
	}
}