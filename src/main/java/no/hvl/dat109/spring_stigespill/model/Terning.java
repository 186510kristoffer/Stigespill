package no.hvl.dat109.spring_stigespill.model;

import java.util.Random;
import org.springframework.stereotype.Component;

/**
 * Representerer en sekssidet terning.
 */
@Component
public class Terning {
	private Random random;
	
	public Terning() {
		this.random= new Random();
	}
	
	/**
	 * Triller terningen og returnerer resultatet (1-6).
	 */
	public int trill() {
		return random.nextInt(6) + 1;
	}
}