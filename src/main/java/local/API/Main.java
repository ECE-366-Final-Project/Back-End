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

import org.json.JSONObject;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Optional;
import java.util.LinkedList;
import java.util.Random;
import java.sql.* ;  // for standard JDBC programs

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@RestController
public class Main {

	private static final String DB = System.getenv("POSTGRES_DB");
	private static final String DB_URL ="jdbc:postgresql://db:5432/"+DB; 
	private static final String USER = System.getenv("POSTGRES_USER");
	private static final String PASS = System.getenv("POSTGRES_PASSWORD");

	private static final double[] payoutTable = new double[1000];

    HashMap<String, String> cachedSessionTokens = new HashMap<String, String>();

    Random rand = new Random();

	String API_SESSION_SALT = String.valueOf(rand.nextInt(65535));

	public static void main(String[] args){
		// Test connection to database. If its not active, fail loudly.
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String QUERY = "SELECT payout FROM public.\"slots_payouts\" LIMIT 1000;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
            int i = 0;
            while (rs.next() && i < 1000) {
                payoutTable[i++] = rs.getDouble(1);
            }
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}

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
	public ResponseEntity<String> status() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", "CooperCasino (Status: Online)");
		return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
	}
	@GetMapping("/Ping")
	public ResponseEntity<String> ping() {
		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", "CooperCasino (Ping: Sucessful)");
		return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
	}

    private boolean isValidAccount(String token) {
        return cachedSessionTokens.containsKey(token);
    }

	private boolean isValidBet(String username, String bet_string) {
		double bet;
		try {
			bet = Double.parseDouble(bet_string);
		} catch (Exception e) {
			return false;
		}
		if (bet <= 0) {
			return false;
		}
		String QUERY = "SELECT balance FROM public.\"user\" WHERE username = \'"+username+"\';";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			Double bal = rs.getDouble(1);
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
		return payoutTable[payout_id];
	}

	@GetMapping("/PlaySlots")
	public ResponseEntity<String> playSlots(@RequestParam(value = "token", defaultValue = "") String token,
											@RequestParam(value = "bet", defaultValue = "-1") String bet) {

		// 1. Is Valid Account
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}

        String username = cachedSessionTokens.get(token);

		// 2. Balance >= Bet
		if (!isValidBet(username, bet)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID BET");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// 3. Generate Roll
		int payout_id = rand.nextInt(1000);

		// 4. Get Payout And Winnings From DB
		double payout = getPayout(payout_id);
		if (payout < 0) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: INVALID PAYOUT");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		double winnings;
		try {
			winnings = Double.parseDouble(bet) * payout;
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: COULD NOT PARSE BET");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 5. Update Database (userBalance, slotsHistory)...		
		String QUERY_user = "UPDATE public.\"user\" SET balance = balance - "+bet+" + "+winnings+" WHERE username = \'"+username+"\';";
		String QUERY_slots = "INSERT INTO public.\"slots\"(username, bet, payout_id, winnings) VALUES (\'"+username+"\', "+bet+", "+payout_id+", "+winnings+");";

		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY_user);
			stmt.executeUpdate(QUERY_slots);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "SLOTS GAME SUCCESSFUL");
			jo.put("winnings", winnings);
			jo.put("payout", payout);
			jo.put("payout_id", payout_id);
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean hasActiveGame(String username) {
		String QUERY = "SELECT COUNT(1) FROM public.\"blackjack\" WHERE username = \'"+username+"\';";
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
			return false;
		}
	}

	private boolean insertBlackjackGameDB(BlackjackGame game) {
		String QUERY = "INSERT INTO public.\"blackjack\" (username, bet, player_hand, dealer_hand) VALUES ("+game.username+", "+game.bet+", \'"+game.getPlayersCards()+"\', \'"+game.getDealersCards()+"\');";
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
	HashMap<String, BlackjackGame> activeGameLookup = new HashMap<String, BlackjackGame>();

	@GetMapping("/NewBlackjack")
	public ResponseEntity<String> newBlackjack(	@RequestParam(value = "token", defaultValue = "") String token,
												@RequestParam(value = "bet", defaultValue = "-1") String bet) {
		// 1. Is Valid Account
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}

        String username = cachedSessionTokens.get(token);

		// 2. Balance >= Bet
		if (!isValidBet(username, bet)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID BET");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// 3. Check if user has active game
		if (!hasActiveGame(username)) {
			// add new blackjack game to cachedBlackjackGames and to blackjack table in databse
			BlackjackGame game = new BlackjackGame(username, Optional.empty(),  Optional.empty(),  Optional.empty(), Double.parseDouble(bet));
			cachedBlackjackGames.add(game);
			activeGameLookup.put(username, game);
			// Insert game into "blackjack" table of database
			if (insertBlackjackGameDB(game)) {
				JSONObject jo = new JSONObject();
				jo.put("MESSAGE", "CREATION OF NEW BLACKJACK GAME SUCCESSFUL");
				jo.put("players_cards", game.getPlayersCards());
				jo.put("dealers_cards", game.getDealersCards().substring(0, 2));
				return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
			}

			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE INSERTION ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", "BLACKJACK GAME ALREADY IN PROGRESS");
		return new ResponseEntity<String>(jo.toString(), HttpStatus.PRECONDITION_FAILED);
	}

	// LinkedList<BlackjackGame> cachedBlackjackGames = new LinkedList<BlackjackGame>();
	// HashMap<Integer, BlackjackGame> activeGameLookup = new HashMap<Integer, BlackjackGame>();

	@GetMapping("/UpdateBlackjack")
	public String updateBlackjack(	@RequestParam(value = "token", defaultValue = "") String token,
									@RequestParam(value = "move", defaultValue = "-1") String move) {
        // 1. Is Valid Account
		if (!isValidAccount(token)) {
			return "400, INVALID SESSION, TRY LOGGING IN;";
		}

        String username = cachedSessionTokens.get(token);

		BlackjackGame game = activeGameLookup.get(username);
		if (game == null) {
			// check database
			String QUERY_check = "SELECT Count(*) FROM public.\"active_blackjack_games\" WHERE username = \'"+username+"\';";
			String QUERY_get = "SELECT * FROM public.\"active_blackjack_games\" WHERE username = \'"+username+"\';";
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
				game = new BlackjackGame(	username, 
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
		activeGameLookup.put(username, game);
		return "400, TODO, ADD ACTUAL BLACKJACK LOGIC";
	}

	// @GetMapping("/RejoinBlackjack")
	// public String rejoinBlackjack(@RequestParam(value = "userID", defaultValue = "-1") String userID) {
	// 	int userID_int;
	// 	try {
	// 		userID_int = Integer.parseInt(userID);
	// 	} catch (Exception e) {
	// 		return "400, INVALID USER ID;";
	// 	}
	// 	BlackjackGame game = activeGameLookup.get(userID_int);
	// 	if (game == null) {
	// 		// check database
	// 		String QUERY_check = "SELECT Count(*) FROM public.\"active_blackjack_games\" WHERE userID = "+userID+";";
	// 		String QUERY_get = "SELECT * FROM public.\"active_blackjack_games\" WHERE userID = "+userID+";";
	// 		try {
	// 			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
	// 			Statement stmt = conn.createStatement();
	// 			ResultSet rs = stmt.executeQuery(QUERY_check);
	// 			rs.next();
	// 			if (rs.getInt(1) == 0) {
	// 				return "400, USER DOES NOT HAVE ANY ACTIVE GAMES";
	// 			}
	// 			Statement stmt2 = conn.createStatement();
	// 			ResultSet rs2 = stmt2.executeQuery(QUERY_get);
	// 			rs2.next();
	// 			game = new BlackjackGame(	userID_int, 
	// 										Optional.of(rs.getString("deck")), 
	// 										Optional.of(rs.getString("player_hand")),
	// 										Optional.of(rs.getString("dealer_hand")),
	// 										rs.getDouble("bet"));
	// 			conn.close();
	// 		} catch (SQLException e) {
	// 			e.printStackTrace();
	// 			return "500, INTERNAL SERVER ERROR";
	// 		}

	// 	} else {
	// 		cachedBlackjackGames.remove(game);
	// 	}
	// 	game.resetTimeToKill();
	// 	cachedBlackjackGames.add(game);
	// 	activeGameLookup.put(userID_int, game);
	// 	return "200, "+game.getPlayersCards()+", "+game.getDealersCards().substring(0, 2)+";";
	// }

	private int resolveTimeToLive() {
		BlackjackGame game = cachedBlackjackGames.getFirst();
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			while (game != null && (!game.isAlive())) {
				cachedBlackjackGames.remove(game);
				activeGameLookup.remove(game.username);
				String QUERY = "INSERT INTO public.\"active_blackjack_games\" (username, bet, deck, player_hand, dealer_hand) VALUES ("+game.username+", "+game.bet+", \'"+game.getPlayersCards()+"\', \'"+game.getPlayersCards()+"\', \'"+game.getDealersCards()+"\');";
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
		String pattern = "(?=.{4,16}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])";
		if(!username.matches(pattern)){
			return false;
		}
		return !usernameIsInUse(username);
	}


	// https://www.geeksforgeeks.org/sha-256-hash-in-java/
	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    
    public static String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

	@GetMapping("/LogIn")
	public ResponseEntity<String> logIn(@RequestParam(value = "username", defaultValue = "-1") String username,
										@RequestParam(value = "passkey", defaultValue = "-1") String passkey) {

		String QUERY = "SELECT passkey FROM public.\"user\" WHERE username = \'"+username+"\';";

		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			if (!rs.getString(1).equals(passkey)) {
				JSONObject jo = new JSONObject();
				jo.put("MESSAGE", "INVALID USERNAME OR PASSWORD");
				return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			String hash = toHexString(getSHA(username+API_SESSION_SALT));
			cachedSessionTokens.put(hash, username);
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "LOG IN SUCCESSFUL");
			jo.put("token", hash);
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: COULD NOT CREATE SESSION TOKEN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/CreateUser")
	public ResponseEntity<String> createUser(   @RequestParam(value = "username", defaultValue = "-1") String username,
                                				@RequestParam(value = "passkey", defaultValue = "-1") String passkey) {
		if(!isValidUsername(username)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID USERNAME");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// we have a valid username.
		String Q_ADDUSER = "INSERT INTO public.user(username, passkey, balance, created_at) VALUES (\'"+username+"\', \'"+passkey+"\', 0, NOW());";
		try {
			// add the username
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(Q_ADDUSER);
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		JSONObject jo = new JSONObject();
		jo.put("MESSAGE", "ACCOUNT CREATION SUCCESSFUL");
		return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
	}

	@GetMapping("/Deposit")
	public ResponseEntity<String> deposit(	@RequestParam(value = "token", defaultValue = "") String token,
											@RequestParam(value = "amount", defaultValue = "-1") String depositAmount) {
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}

        String username = cachedSessionTokens.get(token);

		double amount;
		try {
			amount = Double.parseDouble(depositAmount);
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID AMOUNT");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		String QUERY = "UPDATE public.\"user\" SET balance = balance + "+amount+" WHERE username = \'"+username+"\';";
		String QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (username, transaction_type, amount) VALUES (\'"+username+"\', 'DEPOSIT', "+depositAmount+");";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(QUERY_transaction_history);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "DEPOSIT SUCCESSFUL");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/Withdraw")
	public ResponseEntity<String> withdraw(	@RequestParam(value = "token", defaultValue = "") String token,
											@RequestParam(value = "amount", defaultValue = "-1") String withdrawAmount) {
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}

        String username = cachedSessionTokens.get(token);

		double amount;
		try {
			amount = Double.parseDouble(withdrawAmount);
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID AMOUNT");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		if (!isValidBet(username, withdrawAmount)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INSUFFICIENT FUNDS");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		String QUERY = "UPDATE public.\"user\" SET balance = balance - "+amount+" WHERE username = \'"+username+"\';";
		String QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (username, transaction_type, amount) VALUES (\'"+username+"\', 'WITHDRAWAL', "+withdrawAmount+");";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(QUERY_transaction_history);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "WITHDRAWAL SUCCESSFUL");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

// PARAM FORMATING 
// $curl "localhost:<PORT>/Demo?<param1>=<value>&<param2>=<value>"
// Example:
// $curl "localhost:8080/Demo?name=james&name2=evan"