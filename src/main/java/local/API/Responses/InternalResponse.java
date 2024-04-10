package local.API.Responses;

import org.springframework.http.ResponseEntity;

// built-to-order response class
// provides a basic response code, and an optional list to provide
// necessary user-defined information
public abstract class InternalResponse {

	private int RESPONSE_CODE = 503;
	public int get_RESPONSE_CODE() {
		return RESPONSE_CODE;
	}
	public void set_RESPONSE_CODE(String responseCode) {
		this.RESPONSE_CODE = RESPONSE_CODE;
	}
	
	private String MESSAGE = "Service Unavailable";
	public String get_MESSAGE() {
		return MESSAGE;
	}
	public void set_Message(String MESSAGE) {
		this.MESSAGE = MESSAGE;
	}

	public abstract ResponseEntity<String> toResponseEntity();
}
