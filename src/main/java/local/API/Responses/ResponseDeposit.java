package local.API.Responses;
import org.springframework.http.ResponseEntity;

public class DepositReponse extends InternalResponse {


	public DepositReponse() {
		super();
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", MESSAGE);
		return new ResponseEntity<String>(jo.toString(), responseCode);
	}
}