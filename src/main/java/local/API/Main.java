// Demo API Skeleton, James Ryan
// GPL v3

package local.API;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import local.Casino.Slots.Slots;

import java.util.Random;
import java.sql.* ;  // for standard JDBC programs

@SpringBootApplication
@RestController
public class Main {
	public static void main(String[] args){
		// Start Spring with the args passed to our program on first-run.
		SpringApplication.run(Main.class, args);
	}

	Random rand = new Random();

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
		String QUERY = "SELECT COUNT(1) FROM public.\"user\" WHERE user_id = "+userID+";";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			int count = rs.getInt(1);
			conn.close();
			System.out.println(""+count+"\n\n");
			return (count == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	boolean isValidBet(int userID, double bet) {
		String QUERY = "SELECT balance FROM public.\"user\" WHERE user_id = "+userID+";";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			Double bal = rs.getObject(1) != null ? rs.getDouble(1) : null;
			conn.close();
			if (bal == null) {
				return false;
			}
			return (bal >= bet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	double getPayout(int payout_id) {
		String QUERY = "SELECT payout FROM public.\"slots_payouts\" WHERE payout_id = "+payout_id+";";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			Double payout = rs.getObject(1) != null ? rs.getDouble(1) : null;
			conn.close();
			return (double) payout;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// @GetMapping("/UserCount")
	// public String userCount(@RequestParam(value="userID", defaultValue = "-1") String userID) {
	// 	String QUERY = "SELECT COUNT(1) FROM \"user\" WHERE user_id = "+userID+";";
	// 	try {
	// 		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
	// 		Statement stmt = conn.createStatement();
	// 		ResultSet rs = stmt.executeQuery(QUERY);
	// 		rs.next();
	// 		int count = rs.getInt(1);
	// 		conn.close();
	// 		return userID +": "+count+"\n";
	// 	} catch (SQLException e) {
	// 		e.printStackTrace();
	// 	}
	// 	return "ERROR: "+userID+"\n";
	// }

	@GetMapping("/PlaySlots")
	public String playSlots(@RequestParam(value = "userID", defaultValue = "-1") String userID,
							@RequestParam(value = "bet", defaultValue = "-1") String bet) {

		// 1. Is Valid Account
		if (!isValidAccount(Integer.parseInt(userID))) {
			return "INVALID USER ID";
		}

		// 2. Balance >= Bet
		if (!isValidBet(Integer.parseInt(userID), Double.parseDouble(bet))) {
			return "INVALID BET";
		}

		// 3. Generate Roll
		int payout_id = rand.nextInt(1000);

		// 4. Get Payout And Winnings From DB
		double payout = getPayout(payout_id);
		if (payout < 0) {
			return "ERROR: INVALID PAYOUT";
		}
		double winnings = Double.parseDouble(bet) * payout;

		// 5. Update Database (userBalance, slotsHistory)...		
		String QUERY_user = "UPDATE public.\"user\" SET balance = balance - "+bet+" + "+winnings+" WHERE user_id = "+userID+";";
		String QUERY_slots = "INSERT INTO public.\"slots\"(user_id, bet, payout_id, winnings) VALUES ("+userID+", "+bet+", "+payout_id+", "+winnings+")";

		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY_user);
			stmt.executeUpdate(QUERY_slots);
			conn.close();
			return winnings+", "+payout+", "+payout_id+", 200;";
		} catch (SQLException e) {
			e.printStackTrace();
			return "400";
		}
		return "400";
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