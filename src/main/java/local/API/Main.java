// Demo API Skeleton, James Ryan
// GPL v3

package local.API;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import local.Casino.Slots.Slots;
import local.API.BlackjackGame;

import java.util.HashMap;
import java.util.Optional;
import java.util.LinkedList;
import java.util.Random;
import java.sql.* ;  // for standard JDBC programs

@SpringBootApplication
@RestController
public class Main {
	private static final String DB = System.getenv("POSTGRES_DB");
	private static final String DB_URL ="jdbc:postgresql://db:5432/"+DB; 
	private static final String USER = System.getenv("POSTGRES_USER");
	private static final String PASS = System.getenv("POSTGRES_PASSWORD");
	
	public static void main(String[] args){
		// Test connection to database. If its not active, fail loudly.
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}

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

//	String DB_URL = "jdbc:postgresql://db:5432/postgres";
//	String USER = "postgres";
//	String PASS = "password";


	private boolean isValidAccount(int userID) {
		String QUERY = "SELECT COUNT(1) FROM public.\"user\" WHERE user_id = "+userID+";";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			int count = rs.getInt(1);
			conn.close();
			return (count == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isValidBet(int userID, double bet) {
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

	private double getPayout(int payout_id) {
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

	@GetMapping("/PlaySlots")
	public String playSlots(@RequestParam(value = "userID", defaultValue = "-1") String userID,
							@RequestParam(value = "bet", defaultValue = "-1") String bet) {

		// 1. Is Valid Account
		if (!isValidAccount(Integer.parseInt(userID))) {
			return "400, INVALID USER ID;";
		}

		// 2. Balance >= Bet
		if (!isValidBet(Integer.parseInt(userID), Double.parseDouble(bet))) {
			return "400, INVALID BET;";
		}

		// 3. Generate Roll
		int payout_id = rand.nextInt(1000);

		// 4. Get Payout And Winnings From DB
		double payout = getPayout(payout_id);
		if (payout < 0) {
			return "400, INVALID PAYOUT;";
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
			return "200,"+winnings+", "+payout+", "+payout_id+";";
		} catch (SQLException e) {
			e.printStackTrace();
			// return "400";
		}
		return "400;";
	}

	private boolean hasActiveGame(int userID) {
		String QUERY = "SELECT COUNT(1) FROM public.\"blackjack\" WHERE active = true;";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			int count = rs.getInt(1);
			conn.close();
			return (count > 0);
		} catch (SQLException e) {
			e.printStackTrace();
			// return false;
		}
		return false;
	}

	private boolean insertBlackjackGameDB(BlackjackGame game, double bet) {
		String QUERY = "INSERT INTO public.\"blackjack\" (user_id, bet, player_hand, dealer_hand) VALUES ("+game.userID+", "+bet+", \'"+game.getPlayersCards()+"\', \'"+game.getDealersCards()+"\');";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	LinkedList<BlackjackGame> cachedBlackjackGames = new LinkedList<BlackjackGame>();
	HashMap<Integer, BlackjackGame> activeGameLookup = new HashMap<Integer, BlackjackGame>();

	@GetMapping("/NewBlackjack")
	public String newBlackjack(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
								@RequestParam(value = "bet", defaultValue = "-1") String bet) {
		// 1. Is Valid Account
		if (!isValidAccount(Integer.parseInt(userID))) {
			return "400, INVALID USER ID;";
		}
		// 2. Balance >= Bet
		if (!isValidBet(Integer.parseInt(userID), Double.parseDouble(bet))) {
			return "400, INVALID BET;";
		}
		// 3. Check if user has active game
		if (!hasActiveGame(Integer.parseInt(userID))) {
			// add new blackjack game to cachedBlackjackGames and to blackjack table in databse
			BlackjackGame game = new BlackjackGame(Integer.parseInt(userID), Optional.empty(),  Optional.empty(),  Optional.empty());
			cachedBlackjackGames.add(game);
			activeGameLookup.put(Integer.parseInt(userID), game);
			// Insert game into "blackjack" table of database
			if (insertBlackjackGameDB(game, Double.parseDouble(bet))) {
				return "200, "+game.getPlayersCards()+", "+game.getDealersCards().substring(0, 2)+";";
			}
			return "400, ERROR INSERTING INTO DATABSE;";
		}
		return "400, GAME ALREADY IN PROGRESS;";
	}

	// LinkedList<BlackjackGame> cachedBlackjackGames = new LinkedList<BlackjackGame>();
	// HashMap<Integer, BlackjackGame> activeGameLookup = new HashMap<Integer, BlackjackGame>();

	@GetMapping("/UpdateBlackjack")
	public String updateBlackjack(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
									@RequestParam(value = "move", defaultValue = "-1") String move) {
		BlackjackGame game = activeGameLookup.get(Integer.parseInt(userID));
		if (game == null) {
			// check database
		} else {
			cachedBlackjackGames.remove(game);
		}
		game.resetTimeToKill();
		cachedBlackjackGames.add(game);
		return "300";
	}

	@GetMapping("/RejoinBlackjack")
	public String rejoinBlackjack() {
		return "300";
	}

	// Returns whether a given username is valid for user-creation or not
	// true -- can be used
	// false -- cant be used
	private boolean isValidUsername(String username) {
		//regex of valid character patterns
		if (username.equals("-1")) {
			return false;
		}
		String pattern= "^[0-9]*[a-zA-Z][a-zA-Z0-9]*$";
		if(!username.matches(pattern)){
			return false;
		}
		//finally, check if this exists in db
		String QUERY = "SELECT COUNT(1) FROM public.\"user\" WHERE username = "+username+";";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			int count = rs.getInt(1);
			conn.close();
			return (count == 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@GetMapping("/CreateUser")
	public String createUser(@RequestParam(value = "username", defaultValue = "-1") String username) {
		if(!isValidUsername(username)) {
			return "400, INVALID USERNAME. DB UNCHANGED.";
		}

		// we have a valid username.
		String Q_ADDUSER = "INSERT INTO public.user(username, balance, created_at) VALUES ('"+username+"', 0, NOW());";
		try {
			// add the username
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(Q_ADDUSER);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return "500, INTERNAL SERVER ERROR";
		}

		return "200;";
	}
/*
	@GetMapping("/DeleteUser")
	public String rejoinBlackjack() {
		return "300";
	}

	@GetMapping("/UserInfo")
	public String rejoinBlackjack() {
		return "300";
	}
*/

/*
	@GetMapping("/Deposit")
	public String rejoinBlackjack() {
		return "300";
	}

	@GetMapping("/Withdraw")
	public String rejoinBlackjack() {
		return "300";
	}
*/
	@GetMapping("/UserInfo")
	public String userInfo() {
		return "300";
	}
}

// PARAM FORMATING 
// $curl "localhost:<PORT>/Demo?<param1>=<value>&<param2>=<value>"
// Example:
// $curl "localhost:8080/Demo?name=james&name2=evan"
