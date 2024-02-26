package local.Casino.Slots;
import local.Casino.Slots.Slots.Symbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//Static functions for emulating database calls, until DB is fully fleshed out
public class db_emulator {

    public static double Payout(Symbol[] roll) throws FileNotFoundException {

        Scanner s = new Scanner(new File("Notes/payouts.csv"));
        String curline;
        while(s.hasNextLine()) {
            curline = s.nextLine();
            String[] elements = curline.split("");
            boolean isright = true;
            for(int i = 0; i < 3; i++){
                if(!elements[i].equals(roll[i].name())){
                    isright = false;
                }
            }
            if(isright) {
                s.close();
                return Double.parseDouble(elements[4]);
            }
        }
        s.close();
        return 0.0;
    }

}
