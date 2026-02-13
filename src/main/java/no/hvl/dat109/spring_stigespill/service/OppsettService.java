package no.hvl.dat109.spring_stigespill.service;

import java.util.ArrayList;
import java.util.List;

import no.hvl.dat109.spring_stigespill.dto.SpillOppsett;
import no.hvl.dat109.spring_stigespill.model.Spiller;

public class OppsettService {
	
	public List<Spiller> lagSpillerListe(SpillOppsett spillOppsett) {
		
		int antall = spillOppsett.getAntallSpillere();
		List <String> navnene = spillOppsett.getSpillerNavn();
		
		List<Spiller> spillere = new ArrayList<>();
		
		for (int i=0; i<antall; i++) {
			String navn = navnene.get(i);
			if(navn!=null && !navn.isEmpty()){
				spillere.add(new Spiller(navn, "F"+(i+1)));
			}
		}
		return spillere;
	}
}
