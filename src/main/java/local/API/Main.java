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

import local.API.Responses.*;

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
		// String QUERY_user = "UPDATE public.\"user\" SET balance = balance - "+bet+" + "+winnings+" WHERE username = \'"+username+"\';";
		String QUERY_slots = "INSERT INTO public.\"slots\"(username, bet, payout_id, winnings) VALUES (\'"+username+"\', "+bet+", "+payout_id+", "+winnings+");";

		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResponseEntity<String> withdrawResult = bet(token, bet);
			if (withdrawResult.getStatusCode() != HttpStatus.OK) {
				return withdrawResult;
			}
			ResponseEntity<String> depositResult = payout(token, Double.toString(winnings));
			if (depositResult.getStatusCode() != HttpStatus.OK) {
				return depositResult;
			}
			// stmt.executeUpdate(QUERY_user);
			stmt.executeUpdate(QUERY_slots);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "SLOTS GAME SUCCESSFUL");
			jo.put("WINNINGS", winnings);
			jo.put("PAYOUT", payout);
			jo.put("PAYOUT_ID", payout_id);
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean hasActiveGame(String username) {
		String QUERY = "SELECT COUNT(1) FROM public.\"blackjack\" WHERE username = \'"+username+"\' AND active = true;";
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
		String QUERY = "INSERT INTO public.\"blackjack\" (username, bet, player_hand, dealer_hand) VALUES (\'"+game.username+"\', "+game.bet+", \'"+game.getPlayersCards()+"\', \'"+game.getDealersCards()+"\');";
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

		ResponseEntity<String> withdrawResult = bet(token, bet);
		if (withdrawResult.getStatusCode() != HttpStatus.OK) {
			return withdrawResult;
		}

		// 3. Check if user has active game
		if (!hasActiveGame(username)) {
			// add new blackjack game to cachedBlackjackGames and to blackjack table in databse
			BlackjackGame game = new BlackjackGame(username, Optional.empty(),  Optional.empty(),  Optional.empty(), Double.parseDouble(bet));
			// Insert game into "blackjack" table of database
			if (insertBlackjackGameDB(game)) {
				JSONObject jo = new JSONObject();
				jo.put("MESSAGE", "CREATION OF NEW BLACKJACK GAME SUCCESSFUL");
				jo.put("PLAYERS_CARDS", game.getPlayersCards());
				jo.put("DEALERS_CARDS", game.getDealersCards().substring(0, 2));
				if (game.getScore(game.getPlayersCards()) == 21) {
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "PLAYER");
					jo.put("PLAYER_SCORE", 21);
					jo.put("GAME_RESULT", "BLACKJACK");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", 2.5);
					ResponseEntity<String> depositResult = payout(token, Double.toString(game.bet*2.5));
					if (depositResult.getStatusCode() != HttpStatus.OK) {
						return depositResult;
					}
					try {
						Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
						String QUERY = "UPDATE public.\"blackjack\" SET winnings = "+Double.toString(game.bet*2.5)+", active = false, dealer_hand = \'"+game.getDealersCards()+"\' WHERE blackjack_game_id IN (SELECT blackjack_game_id FROM public.\"blackjack\" WHERE username = \'"+game.username+"\' AND active = true ORDER BY blackjack_game_id DESC LIMIT 1 FOR UPDATE);";
						Statement stmt = conn.createStatement();
						stmt.executeUpdate(QUERY);
						game = cachedBlackjackGames.getFirst();
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
						jo = new JSONObject();
						jo.put("MESSAGE", "INTERNAL SERVER ERROR: COULD NOT STORE GAME INTO DATABASE");
						return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
					}
					return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
				}
				jo.put("GAME_ENDED", "false");
				jo.put("PLAYER_SCORE", game.getScore(game.getPlayersCards()));
				cachedBlackjackGames.add(game);
				activeGameLookup.put(username, game);
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

	@GetMapping("/UpdateBlackjack")
	public ResponseEntity<String> updateBlackjack(	@RequestParam(value = "token", defaultValue = "") String token,
													@RequestParam(value = "move", defaultValue = "-1") String move) {
        // 1. Is Valid Account
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
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
					JSONObject jo = new JSONObject();
					jo.put("MESSAGE", "USER DOES NOT HAVE ANY ACTIVE GAMES");
					return new ResponseEntity<String>(jo.toString(), HttpStatus.PRECONDITION_FAILED);
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
				JSONObject jo = new JSONObject();
				jo.put("MESSAGE", "INTERNAL SERVER ERROR");
				return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			cachedBlackjackGames.remove(game);
		}
		activeGameLookup.remove(game.username);
		JSONObject jo = new JSONObject();
		double payout = -1.0;
		int playerScore;
		int dealerScore;
		switch (move) {
			case "hit":
				game.dealPlayer(1);
				playerScore = game.getScore(game.getPlayersCards());
				if (playerScore > 21) {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "DEALER");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", game.getScore(game.getDealersCards()));
					jo.put("GAME_RESULT", "BUST");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(0.0));
					payout = 0.0;
				} else {
					jo.put("GAME_ENDED", "false");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards().substring(0, 2));
				}
				break;
			case "stand":
				playerScore = game.getScore(game.getPlayersCards());
				dealerScore = game.resolveDealersHand();
				String dealersCards = game.getDealersCards();
				if (dealerScore > 21 || playerScore > dealerScore) {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "PLAYER");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", dealerScore);
					jo.put("GAME_RESULT", "PLAYER_WIN");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(2.0));
					payout = 2.0;
				} else if (playerScore == dealerScore) {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "TIE");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", dealerScore);
					jo.put("GAME_RESULT", "PUSH");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(1.0));
					payout = 1.0;
				} else {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "DEALER");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", dealerScore);
					jo.put("GAME_RESULT", "DEALER_WIN");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(0.0));
					payout = 0.0;
				}
				break;
			case "double_down":
				ResponseEntity<String> depositResult = bet(token, Double.toString(game.bet), true);
				if (depositResult.getStatusCode() != HttpStatus.OK) {
					return depositResult;
				}
				game.bet *= 2;
				game.dealPlayer(1);
				playerScore = game.getScore(game.getPlayersCards());
				dealerScore = game.resolveDealersHand();
				if (playerScore > 21) {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "DEALER");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", game.getScore(game.getDealersCards()));
					jo.put("GAME_RESULT", "DOUBLE_DOWN_BUST");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(0.0));
					payout = 0.0;
				} else if (dealerScore > 21 || playerScore > dealerScore) {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "PLAYER");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", dealerScore);
					jo.put("GAME_RESULT", "DOUBLE_DOWN_PLAYER_WIN");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(2.0));
					payout = 2.0;
				} else if (playerScore == dealerScore) {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "TIE");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", dealerScore);
					jo.put("GAME_RESULT", "DOUBLE_DOWN_PUSH");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(1.0));
					payout = 1.0;
				} else {
					jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
					jo.put("GAME_ENDED", "true");
					jo.put("WINNER", "DEALER");
					jo.put("PLAYER_SCORE", playerScore);
					jo.put("DEALER_SCORE", dealerScore);
					jo.put("GAME_RESULT", "DOUBLE_DOWN_DEALER_WIN");
					jo.put("PLAYERS_CARDS", game.getPlayersCards());
					jo.put("DEALERS_CARDS", game.getDealersCards());
					jo.put("PAYOUT", Double.toString(0.0));
					payout = 0.0;
				}
				break;
			default:
				jo.put("MESSAGE", "INVALID ACTION");
				return new ResponseEntity<String>(jo.toString(), HttpStatus.PRECONDITION_FAILED);
		}
		if (payout >= 0) {
			ResponseEntity<String> depositResult = payout(token, Double.toString(game.bet*payout));
			if (depositResult.getStatusCode() != HttpStatus.OK) {
				return depositResult;
			}
			try {
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				String QUERY = "UPDATE public.\"blackjack\" SET bet = "+game.bet+", winnings = "+Double.toString(game.bet*payout)+", active = false, player_hand = \'"+game.getPlayersCards()+"\', dealer_hand = \'"+game.getDealersCards()+"\' WHERE blackjack_game_id IN (SELECT blackjack_game_id FROM public.\"blackjack\" WHERE username = \'"+game.username+"\' AND active = true ORDER BY blackjack_game_id DESC LIMIT 1 FOR UPDATE);";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(QUERY);
				game = cachedBlackjackGames.getFirst();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				jo = new JSONObject();
				jo.put("MESSAGE", "INTERNAL SERVER ERROR: COULD NOT STORE GAME INTO DATABASE");
				return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		}
		activeGameLookup.put(game.username, game);
		game.resetTimeToKill();
		cachedBlackjackGames.add(game);
		activeGameLookup.put(username, game);
		jo.put("MESSAGE", "BLACKJACK GAME UPDATE SUCCESSFULLY");
		return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
	}

	private int resolveTimeToLive() {
		BlackjackGame game = cachedBlackjackGames.getFirst();
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			while (game != null && (!game.isAlive())) {
				cachedBlackjackGames.remove(game);
				activeGameLookup.remove(game.username);
				String QUERY = "INSERT INTO public.\"active_blackjack_games\" (username, bet, deck, player_hand, dealer_hand) VALUES ("+game.username+", "+game.bet+", \'"+game.getDeck()+"\', \'"+game.getPlayersCards()+"\', \'"+game.getDealersCards()+"\');";
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
			if (!rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("MESSAGE", "INVALID USERNAME OR PASSWORD");
				return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
			}
			String key = rs.getString(1);
			if (!key.equals(passkey)) {
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
			jo.put("TOKEN", hash);
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
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
			if (amount <= 0) {
				JSONObject jo = new JSONObject();
				jo.put("MESSAGE", "INVALID AMOUNT");
				return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
			}
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

	public ResponseEntity<String> bet(String token, String betAmount) {
		return bet(token, betAmount, false);
	}

	public ResponseEntity<String> bet(String token, String betAmount, boolean isDoubleDown) {
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}

        String username = cachedSessionTokens.get(token);

		double amount;
		try {
			amount = Double.parseDouble(betAmount);
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID AMOUNT");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		if (!isValidBet(username, betAmount)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INSUFFICIENT FUNDS");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		String QUERY_transaction_history;

		if (isDoubleDown) {
			QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (username, transaction_type, amount) VALUES (\'"+username+"\', 'DOUBLE_DOWN', "+betAmount+");";
		} else {
			QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (username, transaction_type, amount) VALUES (\'"+username+"\', 'BET', "+betAmount+");";
		}
		String QUERY = "UPDATE public.\"user\" SET balance = balance - "+amount+" WHERE username = \'"+username+"\';";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(QUERY_transaction_history);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "BET SUCCESSFUL");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> payout(String token, String paymentAmount) {
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}

        String username = cachedSessionTokens.get(token);

		double amount;
		try {
			amount = Double.parseDouble(paymentAmount);
		} catch (Exception e) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID AMOUNT");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		String QUERY = "UPDATE public.\"user\" SET balance = balance + "+amount+" WHERE username = \'"+username+"\';";
		String QUERY_transaction_history = "INSERT INTO public.\"transaction_history\" (username, transaction_type, amount) VALUES (\'"+username+"\', 'PAYOUT', "+paymentAmount+");";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(QUERY);
			Statement stmt2 = conn.createStatement();
			stmt2.executeUpdate(QUERY_transaction_history);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "PAYOUT SUCCESSFUL");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR: DATABASE ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/GetBal")
	public ResponseEntity<String> getBal(@RequestParam(value = "token", defaultValue = "") String token) {
		if (!isValidAccount(token)) {
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.UNAUTHORIZED);
		}
		String username = cachedSessionTokens.get(token);
		double bet;
		String QUERY = "SELECT balance FROM public.\"user\" WHERE username = \'"+username+"\';";
		try {
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			rs.next();
			Double bal = rs.getDouble(1);
			conn.close();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INVALID SESSION, TRY LOGGING IN");
			jo.put("BALANCE", bal);
			return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			JSONObject jo = new JSONObject();
			jo.put("MESSAGE", "INTERNAL SERVER ERROR");
			return new ResponseEntity<String>(jo.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Roulette
	
	//@RequestMapping("/playRoulette")
}
// PARAM FORMATING 
// $curl "localhost:<PORT>/Demo?<param1>=<value>&<param2>=<value>"
// Example:
// $curl "localhost:8080/Demo?name=james&name2=evan"
