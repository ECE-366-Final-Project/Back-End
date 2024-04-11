package local.API.Responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import local.API.Responses.InternalResponse;

public class StatusResponse extends InternalResponse {

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", this.get_MESSAGE());
		return new ResponseEntity<String>(jo.toString(), HttpStatusCode.valueOf(this.get_RESPONSE_CODE()));
	}
}