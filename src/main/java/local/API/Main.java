// Demo API Skeleton, James Ryan
// GPL v3

package local.API;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import local.Casino.Slots.Slots;

import java.sql.* ;  // for standard JDBC programs

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
	public String status() {
		return "CooperCasino (Status: Online)\n";
	}
	@GetMapping("/Ping")
	public String ping() {
		return "CooperCasino (Ping: Sucsessful)\n";
	}

	String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
	String USER = "postgres";
	String PASS = "password";


	// String QUERY = "INSERT INTO slots_symbols(symbol_id, symbol_name) VALUES (69, 'TEST SYMBOL FOR JDBC');";
	// String Q2 = "DELETE FROM slots_symbols WHERE symbol_id = 69;";
	// @GetMapping("/Test")
	// public String test() {
	// 	// return "hello";

	// 	try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
	// 		Statement stmt = conn.createStatement();
	// 		ResultSet rs = stmt.executeQuery(QUERY);
	// 		Statement stmt2 = conn.createStatement();
	// 		ResultSet rs2 = stmt2.executeQuery(Q2);) {
	// 	} catch (SQLException e) {
	// 		e.printStackTrace();
	// 	} 
	// 	return "CooperCasino (Status: Online)\n";
	// }

	boolean isValidAccount(int userID) {
		String QUERY = "SELECT COUNT(1) FROM postgres.public.user WHERE user_id = "+userID+";";
		try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);) {
			return (rs.getInt("count") == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;
	}

	boolean isValidBet(int userID, int bet) {
		return true;
	}

	@GetMapping("/PlaySlots")
	public String playSlots(@RequestParam(value = "userID", defaultValue = "-1") String userID,
							@RequestParam(value = "bet", defaultValue = "-1") String bet) {
		// if (isValidAccount(Integer.parseInt(userID))) {
		// 	return "true";
		// }
		// return "false";

		String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
		String USER = "postgres";
		String PASS = "password";
		String QUERY = "INSERT INTO slots_symbols(symbol_id, symbol_name) VALUES (69, 'TEST SYMBOL FOR JDBC');";

		try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);) {
		} catch (SQLException e) {
			e.printStackTrace();
			return "e";
		} 

		return "e2";

		// String QUERY = "SELECT COUNT(1) FROM public.\"user\" WHERE user_id = "+userID+";";
		// try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		// 	Statement stmt = conn.createStatement();
		// 	/* ResultSet rs = stmt.executeQuery(QUERY); */) {
		// 	// return ""+rs.getInt(1);
		// 	return "didn't error";
		// } catch (SQLException e) {
		// 	System.out.println("\n\n\n\n\n\n");
		// 	e.printStackTrace();
		// } 
		// return "error";



		// 1. Is Valid Account
		// 2. Balance >= Bet
		// 3. Generate Roll
		// 4. Get Payout From DB
		// 5. Update Database (userBalance, slotsHistory)...
		// 6. Return Payout, Payout_ID, CODE
	}

	@GetMapping("/NewBlackjack")
	public String newBlackjack() {
		return "1";
	}

	@GetMapping("/UpdateBlackjack")
	public String updateBlackjack() {
		return "2";
	}

	@GetMapping("/RejoinBlackjack")
	public String rejoinBlackjack() {
		return "3";
	}

	// private boolean isValidGame(String game) {
	// 	switch (game) {
	// 		case "Slots":
	// 			return true;
	// 		case "Blackjack":
	// 			return true;
	// 		default:
	// 			return false;
	// 	}
	// }

	// @GetMapping("/NewGame")
	// public String newGame(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
	// 						@RequestParam(value = "game", defaultValue = "-1") String game,
	// 						@RequestParam(value = "bet", defaultValue = "-1") String bet) {
	// 	if (!isValidGame(game)) {
	// 		return INVALID_INPUT+": Invalid Game Type";
	// 	}

	// 	int id = -1;
	// 	int betAmount = -1;

	// 	try {
	// 		id = Integer.parseInt(userID);
	// 	} catch(Exception e) {
	// 		return INVALID_INPUT+": Invalid User ID";
	// 	};

	// 	try {
	// 		betAmount = Integer.parseInt(bet);
	// 	} catch(Exception e) {
	// 		return INVALID_INPUT+": Invalid Bet";
	// 	};

	// 	if (id < 0) {
	// 					return INVALID_INPUT+": Invalid User ID";
	// 	}

	// 	if (betAmount < 0) {
	// 					return INVALID_INPUT+": Invalid Bet";
	// 	}


	// 	if (game.equals("Slots")) {
	// 		return playSlots(id, betAmount);
	// 	}

	// 	if (game.equals("Blackjack")) {
	// 		return createBlackjack(id, betAmount);
	// 	}
	// 	return OK+": RETURNING FROM newGame()";
	// }

}

// PARAM FORMATING 
// $curl "localhost:<PORT>/Demo?<param1>=<value>&<param2>=<value>"
// Example:
// $curl "localhost:8080/Demo?name=james&name2=evan"