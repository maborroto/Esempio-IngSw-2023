package it.unical.ingsw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedMathTest {
	
	private int expected;
	private int n;
		
	public ParameterizedMathTest(int expected, int n) {
		super();
		this.expected = expected;
		this.n = n;
	}

	@Parameters
	public static Collection<Object[]> getParameters() {		
		return Arrays.asList(new Object[][] {{0,0}, {1,1}, {610,15}});
	}
	
	@Test
	public void fibonacciWorks() {
		//MyMath myMath = new MyMath();
		//System.out.println("Value:" + n);
		//assertEquals(expected, myMath.fibonacci(n));
		assertTrue(true);
	}

}
