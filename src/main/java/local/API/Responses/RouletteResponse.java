package local.API.Responses;

import local.API.Responses.InternalResponse;
import org.springframework.http.ResponseEntity;

public class RouletteResponse extends InternalResponse {
	private final int INVALID = -1;
	private final String INVALID_MESSAGE = "Service Unavailable";

	public InternalResponse returnInvalid(){
		return new RouletteResponse(INVALID, INVALID_MESSAGE);
	}
	public InternalResponse returnStatus(int c, String m){
		return new RouletteResponse(c, m);
	}

	public ResponseEntity<String> toResponseEntity() {
		return ResponseEntity.ok("Hello World!");
	}

	public RouletteResponse(int c, String m){
		super(c, m);
	}
}
