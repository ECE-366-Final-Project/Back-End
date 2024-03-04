package local.Casino.Slots;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import local.Casino.Slots.Slots.Symbol;

public class SlotsTest {

    @Test
    public void testGetWinnings_NoWinningCombination() {
        Symbol[] rolls = {Symbol.LEMON, Symbol.WATERMELON, Symbol.HEART};
        double bet = 10.0;
        double expectedWinnings = 0.0;
        double actualWinnings = Slots.getWinnings(rolls, bet);
        Assertions.assertEquals(expectedWinnings, actualWinnings);
    }

  @Test
  public void testPlaySlots() {
      Symbol[] rolls = Slots.playSlots();
      Assertions.assertEquals(Slots.NUMBER_OF_ROLLS, rolls.length);
  
      for (Symbol symbol : rolls) {
          Assertions.assertNotNull(symbol);
      }
  }
}
