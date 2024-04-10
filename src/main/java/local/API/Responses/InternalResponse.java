package local.API.Responses;

import org.springframework.http.ResponseEntity;

// built-to-order response class
// provides a basic response code, and an optional list to provide
// necessary user-defined information
public abstract class InternalResponse {

	private final int responseCode = 503;
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	private String message = 'Service Unavailable';
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public InternalResponse() {}

	public abstract ResponseEntity<String> toResponseEntity();
}
