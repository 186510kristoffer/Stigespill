package no.hvl.dat109.spring_stigespill.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.hvl.dat109.spring_stigespill.model.Brett;
import no.hvl.dat109.spring_stigespill.model.Rute;
import no.hvl.dat109.spring_stigespill.model.Spill;
import no.hvl.dat109.spring_stigespill.model.Spiller;
import no.hvl.dat109.spring_stigespill.model.Terning;
import no.hvl.dat109.spring_stigespill.model.Trekk;
import no.hvl.dat109.spring_stigespill.repository.RuteRepository;
import no.hvl.dat109.spring_stigespill.repository.SpillRepository;
import no.hvl.dat109.spring_stigespill.repository.SpillerRepository;
import no.hvl.dat109.spring_stigespill.repository.TrekkRepository;

/**
 * Håndterer forretningslogikken og spillreglene i Stigespillet.
 */
@Service
public class StigespillService {
    
    @Autowired
    private SpillRepository spillRepository;
    @Autowired
    private SpillerRepository spillerRepository;
    @Autowired
    private TrekkRepository trekkRepository;
    @Autowired 
    private Terning terning;//for å kunne testes
    @Autowired 
    private Brett brett;
    
    /**
     * Oppretter et nytt spillobjekt med gitte spillere.
     * @param spillere Liste med spillere som skal delta.
     * @return Det lagrede spillet.
     */
    public Spill opprettNyttSpill(List<Spiller> spillere) {
        Spill spill = new Spill(spillere);
        return spillRepository.save(spill);
    }

    /**
     * Hovedmetoden for en spilltur.
     * Koordinerer logikk for terningkast, spesialregler og flytting.
     * @param spillId ID-en til spillet.
     * @return En tekststreng som beskriver hva som skjedde.
     */
    @Transactional
    public Trekk spillTur(Long spillId) {
        Spill spill = spillRepository.findById(spillId).orElse(null);

        if (spill == null) return null;
        if (spill.erFerdig()) return null;
        
        spill.getSpillere().sort(Comparator.comparing(Spiller::getId));
       
        Spiller spiller = spill.nesteSpiller();
        int gammelPlass = spiller.getPosisjon();
        
        int kast = terning.trill();
        String type=sjekkRegler(spiller, kast);
        
        if (type.equals("TRE_SEKSERE")) {
            return avsluttTur(spill, spiller, kast, gammelPlass, spiller.getPosisjon(), false, type);
       }
        
        oppdaterSekserTeller(spiller, kast);
        
        if(!type.equals("VANLIG")) {
        	boolean byttTur=(kast!=6);
        	return avsluttTur(spill, spiller, kast, gammelPlass, spiller.getPosisjon(), byttTur, type);
        }

        return utforFlytting(spill, spiller, brett, kast, gammelPlass);
    }

    /**
     * Oppdaterer antall seksere på rad for spilleren.
     * @param spiller Spilleren som kastet.
     * @param kast Verdien på terningen.
     */
    private void oppdaterSekserTeller(Spiller spiller, int kast) {
        if (kast == 6) {
            spiller.setAntallSekserePaaRad(spiller.getAntallSekserePaaRad() + 1);
        } else {
            spiller.setAntallSekserePaaRad(0);
        }
    }

    /**
     * Sjekker reglene for start (må ha 6) og straff for tre seksere.
     * @param spill Det aktuelle spillet.
     * @param s Spilleren som har tur.
     * @param kast Terningkastet.
     * @param plass Spillerens posisjon før kastet.
     * @param seksereStart Antall seksere spilleren hadde FØR dette kastet.
     * @return Melding hvis en regel inntraff, ellers null.
     */
    private String sjekkRegler(Spiller s, int kast) {
        if (s.getAntallSekserePaaRad() == 3) {
            s.setPosisjon(1);
            s.setAntallSekserePaaRad(0);
            return "TRE_SEKSERE";
        }

        if (s.getPosisjon() == 1) {
        	if(s.getAntallSekserePaaRad()==0) {
        		if(kast==6) {
        			return "START_UT";
        		}else {
        			return "START_BLOKKERT";
        		}
        	}
        }
        return "VANLIG"; 
    }

    /**
     * Håndterer selve forflytningen, inkludert >100 regel, slanger og stiger.
     * Sjekker først om man er forbi mål, og avslutter hvis man er det
     * Så vil flytting bli gjort, og type bli satt
     * @param spill Det aktuelle spillet.
     * @param spiller Spilleren som flytter.
     * @param brett Brett-objektet for å sjekke ruter.
     * @param kast Terningkastet.
     * @param gammelPlass Posisjon før flytting.
     * @return trekk i form av avsluttTur(...).
     */
private Trekk utforFlytting(Spill spill, Spiller spiller, Brett brett, int kast, int gammelPlass) {
        
        if (gammelPlass + kast > 100) {
            boolean byttTur = (kast != 6);
            return avsluttTur(spill, spiller, kast, gammelPlass, gammelPlass, byttTur, "FORBI");
        }
        
        int landerPaa = gammelPlass + kast;
        int nyPlass = brett.finnDestinasjon(gammelPlass, kast);
        spiller.setPosisjon(nyPlass);
        
        String type = "VANLIG";
        if (nyPlass == 100) {
            type = "VINNER";
        } else if (nyPlass > landerPaa) {
            type = "STIGE";
        } else if (nyPlass < landerPaa) {
            type = "SLANGE";
        } else if (gammelPlass == 1) {
            type = "START_UT";
        }

        boolean byttTur = (kast != 6 && !type.equals("VINNER"));
        return avsluttTur(spill, spiller, kast, gammelPlass, nyPlass, byttTur, type);
    }

    /**
     * Hjelpemetode for å lagre trekket og oppdatere spiller/spill i basen.
     * @param spill Gjeldende spill.
     * @param s Gjeldende spiller.
     * @param k Terningkast.
     * @param fra Rute før flytting.
     * @param til Rute etter flytting.
     * @param byttTur Om turen skal gå videre til neste.
     */
	private Trekk avsluttTur(Spill spill, Spiller s, int k, int fra, int til, boolean byttTur, String trekkType) {
	    
	    Trekk trekk = new Trekk(spill, k, fra, til, s.getNavn(), LocalDateTime.now(), trekkType);
	    
	    if (trekkType.equals("VINNER")) {
	        spill.setFerdig(true);
	    }
	    
	    trekkRepository.save(trekk);
	    
	    if (spill.erFerdig()) {
	    	
	    } else if (byttTur) {
	        spill.setNesteSpillerIndex((spill.getNesteSpillerIndex() + 1) % spill.getSpillere().size());
	    }
	    
	    spillerRepository.save(s);
	    spillRepository.save(spill);
	    
	    return trekk;
	}
    
    /**
     * Henter spillet fra databasen.
     * @param spillId ID til spillet.
     * @return Spill-objektet.
     */
    @Transactional(readOnly = true)
    public Spill hentSpill(Long spillId) {
        return spillRepository.findById(spillId).orElse(null);
    }
    
    /**
     * Henter en forenklet logg for replay.
     * @param spillId ID for spillet.
     * @return Liste med enkle strenger som beskriver trekkene.
     */
    @Transactional(readOnly = true)
    public List<Trekk> hentLogg(Long spillId) {
        return trekkRepository.findBySpillIdOrderByTidspunktAsc(spillId);
    }
}