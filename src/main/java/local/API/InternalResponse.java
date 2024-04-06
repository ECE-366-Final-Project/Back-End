package local.API;

// built-to-order response class
// provides a basic response code, and an optional list to provide
// necessary user-defined information
public class InternalResponse<T> {
	private static int responseCode;
	private static T data;

	// response data is RO, we shouldnt really be changing it	
	public static int getResponseCode(){
		return responseCode;
	}
	public static List getData(){
		return data;
	}

	public InternalResponse(int code, T d){
		this.responseCode = code;
		this.data = d;
	}
}
