package local.API.Responses;

import org.springframework.http.ResponseEntity;

// built-to-order response class
// provides a basic response code, and an optional list to provide
// necessary user-defined information
public abstract class InternalResponse {
	private final int responseCode;
	private final String message;

	// response data is RO, we shouldnt really be changing it	
	public int getResponseCode(){
		return responseCode;
	}
	public String getMessage(){
		return message;
	}

	public InternalResponse(int code, String m){
		this.responseCode = code;
		this.message = m;
	}

	public abstract ResponseEntity<String> toResponseEntity();
}
