package no.hvl.dat109.spring_stigespill.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representerer spillbrettet og holder oversikt over alle ruter, 
 * inkludert stiger og slanger.
 */
public class Brett {
	
	private final Map <Integer, Rute> ruter = new HashMap<>();

	/**
	 * Oppretter et brett basert på en liste med ruter fra databasen.
	 */
	public Brett (List<Rute> alleRuter) {
		for(Rute r : alleRuter) {
			this.ruter.put(r.getId(), r);
		}
	}
	
	/**
	 * Beregner hvor en spiller ender opp etter et kast, 
	 * inkludert sjekk for stiger og slanger.
	 * * @param plassering Nåværende rute
	 * @param kast Antall øyne på terningen
	 * @return Den endelige ruten spilleren lander på
	 */
	public int finnDestinasjon(int plassering, int kast) {
		int nyPlassering = plassering + kast;
		
		if (nyPlassering > 100) return plassering; 

		Rute landerPaa = ruter.get(nyPlassering);
		
		if(landerPaa != null && landerPaa.erSpesialRute()) {
			return landerPaa.getFlyttTil();
		}
		return nyPlassering;
	}
	
	/**
	 * Henter rute-objektet for et spesifikt nummer.
	 */
	public Rute hentRute(int nr) {
		return ruter.get(nr);
	}
}