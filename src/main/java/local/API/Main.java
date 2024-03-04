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
import local.API.BlackjackLinkedList;

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
		return "CooperCasino (Ping: Sucessful)\n";
	}

//	String DB_URL = "jdbc:postgresql://db:5432/postgres";
//	String USER = "postgres";
//	String PASS = "password";


	private boolean isValidAccount(String userID_string) {
		int userID;
		try {
			userID = Integer.parseInt(userID_string);
		} catch (Exception e) {
			return false;
		}
		String QUERY = "SELECT COUNT(1) FROM public.\"user\" WHERE user_id = "+userID+" AND active = true;";
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

	private boolean isValidBet(String userID_string, String bet_string) {
		int userID;
		double bet;
		try {
			userID = Integer.parseInt(userID_string);
			bet = Double.parseDouble(bet_string);
		} catch (Exception e) {
			return false;
		}
		if (bet <= 0) {
			return false;
		}
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
		if (!isValidAccount(userID)) {
			return "400, INVALID USER ID;";
		}

		// 2. Balance >= Bet
		if (!isValidBet(userID, bet)) {
			return "400, INVALID BET;";
		}

		// 3. Generate Roll
		int payout_id = rand.nextInt(1000);

		// 4. Get Payout And Winnings From DB
		double payout = getPayout(payout_id);
		if (payout < 0) {
			return "400, INVALID PAYOUT;";
		}
		double winnings;
		try {
			winnings = Double.parseDouble(bet) * payout;
		} catch (Exception e) {
			deposit(userID, bet);
			return "500, INTERNAL SERVER ERROR;";
		}

		// 5. Update Database (userBalance, slotsHistory)...		
		String QUERY_user = "UPDATE public.\"user\" SET balance = balance - "+bet+" + "+winnings+" WHERE user_id = "+userID+";";
		String QUERY_slots = "INSERT INTO public.\"slots\"(user_id, bet, payout_id, winnings) VALUES ("+userID+", "+bet+", "+payout_id+", "+winnings+")";

		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY_user);
			stmt.executeUpdate(QUERY_slots);
			conn.close();
			return "200, "+winnings+", "+payout+", "+payout_id+";";
		} catch (SQLException e) {
			e.printStackTrace();
			// return "400";
		}
		return "400;";
	}

	private boolean hasActiveGame(int userID) {
		String QUERY = "SELECT COUNT(1) FROM public.\"blackjack\" WHERE active = true AND user_id = "+userID+";";
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

	// LinkedList<BlackjackGame> cachedBlackjackGames = new LinkedList<BlackjackGame>();
	BlackjackLinkedList cachedBlackjackGames = new BlackjackLinkedList();
	HashMap<Integer, BlackjackGame> activeGameLookup = new HashMap<Integer, BlackjackGame>();

	@GetMapping("/NewBlackjack")
	public String newBlackjack(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
								@RequestParam(value = "bet", defaultValue = "-1") String bet) {
		// 1. Is Valid Account
		if (!isValidAccount(userID)) {
			return "400, INVALID USER ID;";
		}
		// 2. Balance >= Bet
		if (!isValidBet(userID, bet)) {
			return "400, INVALID BET;";
		}
		// 3. Check if user has active game
		if (!hasActiveGame(Integer.parseInt(userID))) {
			// add new blackjack game to cachedBlackjackGames and to blackjack table in databse
			BlackjackGame game = new BlackjackGame(Integer.parseInt(userID), Optional.empty(),  Optional.empty(),  Optional.empty(), Double.parseDouble(bet));
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
		int userID_int;
		try {
			userID_int = Integer.parseInt(userID);
		} catch (Exception e) {
			return "400, INVALID USER ID;";
		}
		BlackjackGame game = activeGameLookup.get(userID_int);
		if (game == null) {
			// check database
			String QUERY_check = "SELECT Count(*) FROM public.\"active_blackjack_games\" WHERE userID = "+userID+";";
			String QUERY_get = "SELECT * FROM public.\"active_blackjack_games\" WHERE userID = "+userID+";";
			try {
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(QUERY_check);
				rs.next();
				if (rs.getInt(1) == 0) {
					return "400, USER DOES NOT HAVE ANY ACTIVE GAMES";
				}
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(QUERY_get);
				rs2.next();
				game = new BlackjackGame(	userID_int, 
											Optional.of(rs.getString("deck")), 
											Optional.of(rs.getString("player_hand")),
											Optional.of(rs.getString("dealer_hand")),
											rs.getDouble("bet"));
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return "500, INTERNAL SERVER ERROR";
			}
		} else {
			cachedBlackjackGames.remove(game);
		}
		// resolve blackjack logic on game
		game.resetTimeToKill();
		cachedBlackjackGames.add(game);
		activeGameLookup.put(userID_int, game);
		return "400, TODO";
	}

	@GetMapping("/RejoinBlackjack")
	public String rejoinBlackjack(@RequestParam(value = "userID", defaultValue = "-1") String userID) {
		int userID_int;
		try {
			userID_int = Integer.parseInt(userID);
		} catch (Exception e) {
			return "400, INVALID USER ID;";
		}
		BlackjackGame game = activeGameLookup.get(userID_int);
		if (game == null) {
			// check database
			String QUERY_check = "SELECT Count(*) FROM public.\"active_blackjack_games\" WHERE userID = "+userID+";";
			String QUERY_get = "SELECT * FROM public.\"active_blackjack_games\" WHERE userID = "+userID+";";
			try {
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(QUERY_check);
				rs.next();
				if (rs.getInt(1) == 0) {
					return "400, USER DOES NOT HAVE ANY ACTIVE GAMES";
				}
				Statement stmt2 = conn.createStatement();
				ResultSet rs2 = stmt2.executeQuery(QUERY_get);
				rs2.next();
				game = new BlackjackGame(	userID_int, 
											Optional.of(rs.getString("deck")), 
											Optional.of(rs.getString("player_hand")),
											Optional.of(rs.getString("dealer_hand")),
											rs.getDouble("bet"));
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return "500, INTERNAL SERVER ERROR";
			}

		} else {
			cachedBlackjackGames.remove(game);
		}
		game.resetTimeToKill();
		cachedBlackjackGames.add(game);
		activeGameLookup.put(userID_int, game);
		return "200, "+game.getPlayersCards()+", "+game.getDealersCards().substring(0, 2)+";";
	}

	private int resolveTimeToLive() {
		BlackjackGame game = cachedBlackjackGames.getFirst();
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			while (game != null && (!game.isAlive())) {
				cachedBlackjackGames.remove(game);
				activeGameLookup.remove(game.userID);
				String QUERY = "INSERT INTO public.\"active_blackjack_games\" (user_id, bet, deck, player_hand, dealer_hand) VALUES ("+game.userID+", "+game.bet+", \'"+game.getPlayersCards()+"\', \'"+game.getPlayersCards()+"\', \'"+game.getDealersCards()+"\');";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(QUERY);
				game = cachedBlackjackGames.getFirst();
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	private boolean usernameIsInUse(String username) {
		String QUERY = "SELECT COUNT(1) FROM public.\"user\" WHERE username = \'"+username+"\';";
		try {
			// add the username
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			conn.close();
			return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Returns whether a given username is valid for user-creation or not
	// true -- can be used
	// false -- cant be used
	private boolean isValidUsername(String username) {
		//regex of valid character patterns
		if (username.equals("-1")) {
			return false;
		}
		// https://stackoverflow.com/a/12019115
		String pattern = "(?=.{2,15}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])";
		if(!username.matches(pattern)){
			return false;
		}
		return !usernameIsInUse(username);
	}

	@GetMapping("/CreateUser")
	public String createUser(@RequestParam(value = "username", defaultValue = "-1") String username) {
		if(!isValidUsername(username)) {
			return "400, INVALID USERNAME;";
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
			return "500, INTERNAL SERVER ERROR;";
		}

		return "200;";
	}

	// Sets a user to inactive but other API calls don't check if user is active yet
	@GetMapping("/DeleteUser")
	public String deleteUser(@RequestParam(value = "userID", defaultValue = "-1") String userID) {
		if (userID.equals("1")) {
			return "400, CANNOT DELETE ADMIN ACCOUNT;";
		}
		if (!isValidAccount(userID)) {
			return "400, INVALID USER ID;";
		}
		if (isValidBet(userID, "0.01")) {
			return "400, USER BALANCE EXCEEDS 0;";
		}
		String QUERY = "UPDATE public.\"user\" SET active = false WHERE user_id = "+userID+";";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			conn.close();
			return "200, DELETION SUCCESSFUL;";
		} catch (SQLException e) {
			e.printStackTrace();
			return "500, INTERNAL SERVER ERROR;";
		}
	}

	@GetMapping("/Deposit")
	public String deposit(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
							@RequestParam(value = "amount", defaultValue = "-1") String depositAmount) {
		if (!isValidAccount(userID)) {
			return "400, INVALID USER ID;";
		}
		double amount;
		try {
			amount = Double.parseDouble(depositAmount);
		} catch (Exception e) {
			return "400, INVALID AMOUNT;";
		}
		String QUERY = "UPDATE public.\"user\" SET balance = balance + "+amount+" WHERE user_id = "+userID+";";
		String QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (user_id, transaction_type, amount) VALUES ("+userID+", 'DEPOSIT', "+depositAmount+");";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(QUERY_transaction_history);
			conn.close();
			return "200, DEPOSIT SUCCESSFUL;";
		} catch (SQLException e) {
			e.printStackTrace();
			return "500, INTERNAL SERVER ERROR;";
		}
	}

	@GetMapping("/Withdraw")
	public String withdraw(	@RequestParam(value = "userID", defaultValue = "-1") String userID,
							@RequestParam(value = "amount", defaultValue = "-1") String withdrawAmount) {
		if (!isValidAccount(userID)) {
			return "400, INVALID USER ID;";
		}
		double amount;
		try {
			amount = Double.parseDouble(withdrawAmount);
		} catch (Exception e) {
			return "400, INVALID AMOUNT;";
		}
		if (!isValidBet(userID, withdrawAmount)) {
			return "400, INSUFFICIENT FUNDS";
		}
		String QUERY = "UPDATE public.\"user\" SET balance = balance - "+amount+" WHERE user_id = "+userID+";";
		String QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (user_id, transaction_type, amount) VALUES ("+userID+", 'WITHDRAWAL', "+withdrawAmount+");";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(QUERY_transaction_history);
			conn.close();
			return "200, WITHDRAWAL SUCCESSFUL;";
		} catch (SQLException e) {
			e.printStackTrace();
			return "500, INTERNAL SERVER ERROR;";
		}
	}

	@GetMapping("/UserInfo")
	public String userInfo(@RequestParam(value="username", defaultValue = "-1") String username) {
		String QUERY = "SELECT user_id, balance FROM public.\"user\" WHERE username = \'"+ username + "\' AND active = true;";
		if(!usernameIsInUse(username)){
			if(!isValidUsername(username)) {
				return "400, USERNAME INVALID;";
			}
			return "400, USER DOES NOT EXIST;";
		}
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			Double bal = rs.getDouble("balance");
			int userID = rs.getInt("user_id");
			return "200, " +userID+", "+bal+";";
		} catch (SQLException e) {
			e.printStackTrace();
			return "500, INTERNAL SERVER ERROR;";
		}
	}
}

// PARAM FORMATING 
// $curl "localhost:<PORT>/Demo?<param1>=<value>&<param2>=<value>"
// Example:
// $curl "localhost:8080/Demo?name=james&name2=evan"
