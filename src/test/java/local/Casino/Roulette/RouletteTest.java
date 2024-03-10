import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import local.Casino.Roulette.Roulette; // wtf? wahhh i dont understand maven

public class RouletteTest {

    @Test
    public void test_Roulette_Roll_toInt() {
		Assertions.assertEquals( 0, Roulette.Roll.ZERO.toInt());
		Assertions.assertEquals( 1, Roulette.Roll.ONE.toInt());
		Assertions.assertEquals( 2, Roulette.Roll.TWO.toInt());
		Assertions.assertEquals( 3, Roulette.Roll.THREE.toInt());
		Assertions.assertEquals( 4, Roulette.Roll.FOUR.toInt());
		Assertions.assertEquals( 5, Roulette.Roll.FIVE.toInt());
		Assertions.assertEquals( 6, Roulette.Roll.SIX.toInt());
		Assertions.assertEquals( 7, Roulette.Roll.SEVEN.toInt());
		Assertions.assertEquals( 8, Roulette.Roll.EIGHT.toInt());
		Assertions.assertEquals( 9, Roulette.Roll.NINE.toInt());
		Assertions.assertEquals(10, Roulette.Roll.TEN.toInt());
		Assertions.assertEquals(11, Roulette.Roll.ELEVEN.toInt());
		Assertions.assertEquals(12, Roulette.Roll.TWELVE.toInt());
		Assertions.assertEquals(13, Roulette.Roll.THIRTEEN.toInt());
		Assertions.assertEquals(14, Roulette.Roll.FOURTEEN.toInt());
		Assertions.assertEquals(15, Roulette.Roll.FIFTEEN.toInt());
		Assertions.assertEquals(16, Roulette.Roll.SIXTEEN.toInt());
		Assertions.assertEquals(17, Roulette.Roll.SEVENTEEN.toInt());
		Assertions.assertEquals(18, Roulette.Roll.EIGHTEEN.toInt());
		Assertions.assertEquals(19, Roulette.Roll.NINETEEN.toInt());
		Assertions.assertEquals(20, Roulette.Roll.TWENTY.toInt());
		Assertions.assertEquals(21, Roulette.Roll.TWENTYONE.toInt());
		Assertions.assertEquals(22, Roulette.Roll.TWENTYTWO.toInt());
		Assertions.assertEquals(23, Roulette.Roll.TWENTYTHREE.toInt());
		Assertions.assertEquals(24, Roulette.Roll.TWENTYFOUR.toInt());
		Assertions.assertEquals(25, Roulette.Roll.TWENTYFIVE.toInt());
		Assertions.assertEquals(26, Roulette.Roll.TWENTYSIX.toInt());
		Assertions.assertEquals(27, Roulette.Roll.TWENTYSEVEN.toInt());
		Assertions.assertEquals(28, Roulette.Roll.TWENTYEIGHT.toInt());
		Assertions.assertEquals(29, Roulette.Roll.TWENTYNINE.toInt());
		Assertions.assertEquals(30, Roulette.Roll.THIRTY.toInt());
		Assertions.assertEquals(31, Roulette.Roll.THIRTYONE.toInt());
		Assertions.assertEquals(32, Roulette.Roll.THIRTYTWO.toInt());
		Assertions.assertEquals(33, Roulette.Roll.THIRTYTHREE.toInt());
		Assertions.assertEquals(34, Roulette.Roll.THIRTYFOUR.toInt());
		Assertions.assertEquals(35, Roulette.Roll.THIRTYFIVE.toInt());
		Assertions.assertEquals(36, Roulette.Roll.THIRTYSIX.toInt());
		Assertions.assertEquals(37, Roulette.Roll.DOUBLEZERO.toInt());
	}

	@Test
	public void test_Roulette_Roll_rollColor(){
		// green
		Assertions.assertEquals(0, Roulette.Roll.DOUBLEZERO.rollColor());
		Assertions.assertEquals(0, Roulette.Roll.ZERO.rollColor());
		// red
		Assertions.assertEquals(1, Roulette.Roll.EIGHTEEN.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.FIVE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.FOURTEEN.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.NINE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.NINETEEN.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.ONE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.SEVEN.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.SIXTEEN.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.THIRTYFOUR.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.THIRTY.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.THIRTYSIX.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.THIRTYTWO.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.THREE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.TWELVE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.TWENTYFIVE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.TWENTYONE.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.TWENTYSEVEN.rollColor());
		Assertions.assertEquals(1, Roulette.Roll.TWENTYTHREE.rollColor());
		// black
		Assertions.assertEquals(2, Roulette.Roll.EIGHT.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.ELEVEN.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.FIFTEEN.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.FOUR.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.SEVENTEEN.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.SIX.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TEN.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.THIRTEEN.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.THIRTYFIVE.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.THIRTYONE.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.THIRTYTHREE.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWENTYEIGHT.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWENTYFOUR.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWENTYNINE.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWENTY.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWENTYSIX.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWENTYTWO.rollColor());
		Assertions.assertEquals(2, Roulette.Roll.TWO.rollColor());
	}
}
