package local.Casino;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.Vector;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import local.Casino.Roulette;
import local.Casino.Roulette.Roll;

public class RouletteController {

	Random rand = new Random();
	
	protected String nextLobby;
	protected volatile String currentLobby;
	protected Roll nextRoll = null;
	protected volatile Roll currentRoll = null;
	protected Vector<String> nextCache = new Vector<String>();
	protected volatile Vector<String> currentCache = new Vector<String>();

	protected static volatile boolean updating;

	private static final ReadWriteLock lock = new ReentrantReadWriteLock();

	protected static LocalDateTime ttl = null;
//	protected RouletteControllerThread update = new RouletteControllerThread();

	protected ArrayList<SimpleEntry<String, Roll>> history = new 
	    ArrayList<SimpleEntry<String, Roll>>();

//	protected class RouletteControllerThread extends Thread {
//		public RouletteControllerThread() {
//			return;
//		}
//		public void run() {
//			updating = true;
//			String oldLobby = currentLobby;
//			Roll oldRoll = currentRoll;
//
//
//			System.out.println("lobby expiring: " + currentLobby);
//
//			try {
//				TimeUnit.SECONDS.sleep(10);
//			} catch (InterruptedException e) {
//				return;
//			} finally {
//				lock.readLock().lock();
//				// pray to god
//				try {
//					currentLobby = nextLobby;
//					currentRoll = nextRoll;
//					currentCache = nextCache;
//				} finally {
//					lock.readLock().unlock();
//				}
//			}
//
//			history.add(0, new SimpleEntry<String, Roll>(oldLobby, oldRoll));
//			if(history.size() > 9){
//				history.remove(history.size()-1);
//			}
//			
//			ttl = LocalDateTime.now().plusSeconds(30);
//
//			nextLobby = getNewLobby();
//			nextRoll = Roll.spinWheel();
//			nextCache = new Vector<String>();
//			System.out.println("thread done, new lobby: "+ currentLobby);
//
//			updating = false;
//		}
//	}

	public String[] returnLobbyID(){
		// if the active lobby's ttd is < 10s to draw, DONT LET THEM JOIN
		// generate a new one, return futureID.

		if(ttl.isBefore(LocalDateTime.now())){
			ttl = LocalDateTime.now().plusSeconds(95);
			currentRoll = Roll.spinWheel();
			currentLobby = getNewLobby();

			history.add(0, new SimpleEntry<String, Roll>(currentLobby, currentRoll));
			if(history.size() > 9){
				history.remove(history.size()-1);
			}
		}
		
		return new String[] { currentLobby, ttl.toString() };
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
		//RouletteControllerThread update = new RouletteControllerThread();
		currentLobby = getNewLobby();
		currentRoll = Roll.spinWheel();

		history.add(0, new SimpleEntry<String, Roll>(currentLobby, currentRoll));
		
		ttl = LocalDateTime.now().plusSeconds(90);
	}
	
	private String getNewLobby() {
		//https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 20) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
