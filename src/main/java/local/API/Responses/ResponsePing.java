package local.API.Responses;
import org.springframework.http.ResponseEntity;

public class PingReponse extends InternalResponse {


	public PingReponse() {
		super();
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", MESSAGE);
		return new ResponseEntity<String>(jo.toString(), responseCode);
	}
}