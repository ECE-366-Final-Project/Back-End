package local.API;

import java.util.List;

// built-to-order response class
// provides a basic response code, and an optional list to provide
// necessary user-defined information
public class InternalResponse {
	public int responseCode;
	public List data;

	public InternalResponse(int code, List d){
		this.responseCode = code;
		this.data = d;
	}
}
