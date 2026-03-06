package no.hvl.dat109.spring_stigespill.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import no.hvl.dat109.spring_stigespill.dto.SpillOppsett;
import no.hvl.dat109.spring_stigespill.model.Spiller;

@Service
public class OppsettService {
	
	public List<Spiller> lagSpillerListe(SpillOppsett spillOppsett) {
		
		int antall = spillOppsett.getAntallSpillere();
		List <String> navnene = spillOppsett.getSpillerNavn();
		List<String> fargene = spillOppsett.getSpillerFarge();
		List<Spiller> spillere = new ArrayList<>();
		
		
		for (int i=0; i<antall; i++) {
			String navn="Spiller"+(i+1);
			String farge="red";
			
			
			if(i<navnene.size()) {
				if(navnene.get(i)!=null&&!navnene.get(i).trim().isEmpty()) {
					navn=navnene.get(i);
				}
			}
			if(i<fargene.size()) {
				if(fargene.get(i)!=null&&!fargene.get(i).isEmpty()) {
					farge=fargene.get(i);
				}
			}
			spillere.add(new Spiller(navn, farge));
		}
		return spillere;
	}
}
