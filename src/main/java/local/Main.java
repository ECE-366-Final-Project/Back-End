package main.java.local;

import main.java.local.Casino.Casino;
import main.java.local.Casino.Account;
import main.java.local.Casino.Slots;

public class Main {

    public static void main(String[] args) {

      Casino house = new Casino(10);
      house.createAccount("Evan R");
  
      Account evan = house.getAccount("Evan R");

      if (evan != null) {

          evan.increaseBalance(100);
          System.out.println(evan.getBalance());
          
      } else {
          System.out.println("NULL");
      }

      Slots.Symbol[] rolls = Slots.playSlots();

      for (int i = 0; i < 3; i++) {
          System.out.println(rolls[i]);
      }
      
      System.out.println(Slots.getWinnings(rolls, 100));

    }

}
