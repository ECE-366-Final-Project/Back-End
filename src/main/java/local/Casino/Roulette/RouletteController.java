package local.Casino;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Vector;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import local.Casino.Roulette;
import local.Casino.Roulette.Roll;

public class RouletteController {

	Random rand = new Random();
	
	protected static String nextLobby;
	protected static String currentLobby;
	protected Roll nextRoll = null;
	protected Roll currentRoll = null;
	protected Vector<String> nextCache = null;
	protected Vector<String> currentCache = null;

	protected static boolean updating;

	protected static LocalDateTime ttl = null;
	protected static RouletteControllerThread update = null;

	protected static ArrayList<SimpleEntry<String, Roll>> history = null;

	protected class RouletteControllerThread extends Thread {
		public void run() {
			history.add(0, new SimpleEntry<String, Roll>(currentLobby, currentRoll));
			if(history.size() > 9){
				history.remove(history.size()-1);
			}

			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// irresponsibility
			}

			// pray to god
			currentLobby = nextLobby;
			currentRoll = nextRoll;
			currentCache = nextCache;

			ttl = LocalDateTime.now().plusSeconds(30);

			nextLobby = Integer.toHexString(rand.nextInt(0xFFFFFFFF));
			nextRoll = Roll.spinWheel();
			nextCache = new Vector<String>();
			System.out.println("thread done");
			updating = false;
		}
	}

	public String returnLobbyID(String username){
		// if the active lobby's ttd is < 10s to draw, DONT LET THEM JOIN
		// generate a new one, return futureID.
		if(currentCache.contains(username) || nextCache.contains(username)){
			return "-1";
		}

		if(ttl.isAfter(LocalDateTime.now().minusSeconds(11))){
			updating = true;
			update.run();
			nextCache.add(username);
			return nextLobby;
		}
		currentCache.add(username);
		return currentLobby;
	}

	public Roll returnRoll(String lobbyID){
		for(SimpleEntry<String, Roll> p : history){
			if(p.getKey().equals(lobbyID)){
				return p.getValue();
			}
		}
		return null;
	}

	public RouletteController(){
		RouletteControllerThread update = new RouletteControllerThread();
		nextLobby =  Integer.toHexString(rand.nextInt(0xFFFFFFFF));
		currentLobby = Integer.toHexString(rand.nextInt(0xFFFFFFFF));
		updating = false;

		nextRoll = Roll.spinWheel();
		currentRoll = Roll.spinWheel();

		ttl = LocalDateTime.now().plusSeconds(30);
	}
}
