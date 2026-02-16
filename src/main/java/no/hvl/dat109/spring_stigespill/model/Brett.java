package no.hvl.dat109.spring_stigespill.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import no.hvl.dat109.spring_stigespill.repository.RuteRepository;

/**
 * Representerer spillbrettet og holder oversikt over alle ruter, 
 * inkludert stiger og slanger.
 */

@Configuration
public class Brett {
	
	@Autowired RuteRepository ruteRepository;
	
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

		Rute landerPaa = ruteRepository.findById(nyPlassering).orElse(null);
		
		if(landerPaa != null && landerPaa.erSpesialRute()) {
			return landerPaa.getFlyttTil();
		}
		return nyPlassering;
	}
}