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
	protected Vector<String> nextCache = new Vector<String>();
	protected Vector<String> currentCache = new Vector<String>();

	protected static boolean updating;

	protected static LocalDateTime ttl = null;
	protected RouletteControllerThread update = new RouletteControllerThread();

	protected ArrayList<SimpleEntry<String, Roll>> history = new 
	    ArrayList<SimpleEntry<String, Roll>>();

	protected class RouletteControllerThread extends Thread {
		public RouletteControllerThread() {
			return;
		}
		public void run() {
			if(updating) {
				return;
			}
			updating = true;
			String oldLobby = currentLobby;
			Roll oldRoll = currentRoll;

			history.add(0, new SimpleEntry<String, Roll>(currentLobby, currentRoll));
			if(history.size() > 9){
				history.remove(history.size()-1);
			}
			System.out.println("lobby expiring: " + currentLobby);

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

			nextLobby = getNewLobby();
			nextRoll = Roll.spinWheel();
			nextCache = new Vector<String>();
			System.out.println("thread done, new lobby: "+ currentLobby);

			updating = false;
		}
	}

	public String returnLobbyID(String username){
		// if the active lobby's ttd is < 10s to draw, DONT LET THEM JOIN
		// generate a new one, return futureID.
		

		if(ttl.isAfter(LocalDateTime.now().minusSeconds(11))){
			System.out.println("ttl met");

			update.run();
		}
		
		if(currentCache.contains(username)){
			return "-1";
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
		nextLobby = getNewLobby();
		currentLobby = getNewLobby();
		updating = false;

		nextRoll = Roll.spinWheel();
		currentRoll = Roll.spinWheel();

		ttl = LocalDateTime.now().plusSeconds(30);
	}
	
	private String getNewLobby() {
		//https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
