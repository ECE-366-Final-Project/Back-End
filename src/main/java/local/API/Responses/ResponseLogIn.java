package local.API.Responses;
import org.springframework.http.ResponseEntity;

public class LogInReponse extends InternalResponse {

	private String TOKEN = "N/A";

	public LogInReponse() {
		super();
	}

	public String getTOKEN() {
		return TOKEN;
	}
	public void setTOKEN(String TOKEN) {
		this.TOKEN = TOKEN;
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", MESSAGE);
		jo.put("TOKEN", TOKEN);
		return new ResponseEntity<String>(jo.toString(), responseCode);
	}
}