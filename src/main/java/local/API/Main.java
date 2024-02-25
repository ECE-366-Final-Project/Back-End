// Demo API Skeleton, James Ryan
// GPL v3

package local.API;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
	public static void main(String[] args){
		// Start Spring with the args passed to our program on first-run.
		SpringApplication.run(Main.class, args);
	}

	// Specify an API path to make the request to
	// type `curl localhost:8080/Demo` to make the request
	// @GetMapping("/Demo")
	// public String hello(@RequestParam(value = "name", defaultValue = "Gamer") String name,
	// 					@RequestParam(value = "name2", defaultValue = "Gamer2") String name2) {
	// 	return String.format("Hello %s and %s!~\n", name, name2);
	// }

	// ping api
	@GetMapping("/Status")
	public String Status() {
		return "CooperCasino (Status: Online)\n";
	}
	@GetMapping("/Ping")
	public String ping() {
		return "CooperCasino (Status: Online)\n";
	}


	private boolean isValidGame(String game) {
		switch (game) {
			case "Slots":
				return true;
			case "Blackjack":
				return true;
			default:
				return false;
		}
	}

	final int INVALID_INPUT = 400; //not sure what the error code is for this 
	final int OK = 200;

	String createBlackjack(int id, int bet) {
		return OK+": RETURNING FROM createBlackjack()";
	}

	String playSlots(int id, int bet) {
		// roll slots
		// put stuff in database
		// return information
		return OK+": RETURNING FROM playSlots()";
	}


	@GetMapping("/NewGame")
	public String newGame(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
							@RequestParam(value = "game", defaultValue = "-1") String game,
							@RequestParam(value = "bet", defaultValue = "-1") String bet) {
		if (!isValidGame(game)) {
			return INVALID_INPUT+": Invalid Game Type";
		}

		int id = -1;
		int betAmount = -1;

		try {
			id = Integer.parseInt(userID);
		} catch(Exception e) {
			return INVALID_INPUT+": Invalid User ID";
		};

		try {
			betAmount = Integer.parseInt(bet);
		} catch(Exception e) {
			return INVALID_INPUT+": Invalid Bet";
		};

		if (id < 0) {
						return INVALID_INPUT+": Invalid User ID";
		}

		if (betAmount < 0) {
						return INVALID_INPUT+": Invalid Bet";
		}


		if (game.equals("Slots")) {
			return playSlots(id, betAmount);
		}

		if (game.equals("Blackjack")) {
			return createBlackjack(id, betAmount);
		}
		return OK+": RETURNING FROM newGame()";
	}

}

// PARAM FORMATING 
// $curl "localhost:<PORT>/Demo?<param1>=<value>&<param2>=<value>"
// Example:
// $curl "localhost:8080/Demo?name=james&name2=evan"