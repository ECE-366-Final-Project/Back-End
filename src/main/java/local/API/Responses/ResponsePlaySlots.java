package local.API.Responses;
import org.springframework.http.ResponseEntity;

public class PlaySlotsReponse extends InternalResponse {

	private int PAYOUT_ID = -1;
	private double PAYOUT = -1;
	private double WINNINGS = -1;

	public PlaySlotsReponse() {
		super();
	}

	public int getPAYOUT_ID() {
		return PAYOUT_ID;
	}
	public void setPAYOUT_ID(int PAYOUT_ID) {
		this.PAYOUT_ID = PAYOUT_ID;
	}

	public double getPAYOUT() {
		return PAYOUT;
	}
	public void setPAYOUT(double PAYOUT) {
		this.PAYOUT = PAYOUT;
	}

	public double getWINNINGS() {
		return WINNINGS;
	}
	public void setWINNINGS(double WINNINGS) {
		this.WINNINGS = WINNINGS;
	}

	public ResponseEntity<String> toResponseEntity() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", MESSAGE);
		jo.put("PAYOUT_ID", PAYOUT_ID);
		jo.put("PAYOUT", PAYOUT);
		jo.put("WINNINGS", WINNINGS);
		return new ResponseEntity<String>(jo.toString(), responseCode);
	}
}