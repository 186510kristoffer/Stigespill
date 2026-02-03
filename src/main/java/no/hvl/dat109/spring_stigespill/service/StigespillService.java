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
    private RuteRepository ruteRepository;
    @Autowired
    private TrekkRepository trekkRepository;
    @Autowired 
    private Terning terning;//for å kunne testes
    
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
    public String spillTur(Long spillId) {
        Spill spill = spillRepository.findById(spillId).orElse(null);

        if (spill == null) return "Ukjent spill ID";
        if (spill.erFerdig()) return "Spillet er ferdig!";
        
        spill.getSpillere().sort(Comparator.comparing(Spiller::getId));
        List<Rute> alleRuter = ruteRepository.findAll();
        Brett brett = new Brett(alleRuter);
        
        Spiller spiller = spill.nesteSpiller();
        int gammelPlass = spiller.getPosisjon();
        int seksereStart = spiller.getAntallSekserePaaRad(); 
        
        int kast = terning.trill();
        oppdaterSekserTeller(spiller, kast);
        
        String spesialMelding = sjekkStartOgSekserRegler(spill, spiller, kast, gammelPlass, seksereStart);
        if (spesialMelding != null) {
            return spesialMelding;
        }

        return utførFlytting(spill, spiller, brett, kast, gammelPlass);
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
    private String sjekkStartOgSekserRegler(Spill spill, Spiller s, int kast, int plass, int seksereStart) {
        if (s.getAntallSekserePaaRad() == 3) {
            s.setPosisjon(1);
            s.setAntallSekserePaaRad(0);
            avsluttTur(spill, s, kast, plass, 1, true); 
            return s.getNavn() + ": triller 6 (3. gang!) -> rute 1 (Tilbake til start)";
        }

        if (plass == 1 && kast != 6 && seksereStart == 0) {
            avsluttTur(spill, s, kast, 1, 1, true); 
            return s.getNavn() + ": triller " + kast + ", rute 1 -> 1. Må ha 6.";
        }
        
        if (plass == 1 && kast == 6) {
            avsluttTur(spill, s, kast, 1, 1, false); 
            return s.getNavn() + ": triller 6! Ute av start.";
        }
        
        return null; 
    }

    /**
     * Håndterer selve forflytningen, inkludert >100 regel, slanger og stiger.
     * @param spill Det aktuelle spillet.
     * @param s Spilleren som flytter.
     * @param brett Brett-objektet for å sjekke ruter.
     * @param kast Terningkastet.
     * @param gammelPlass Posisjon før flytting.
     * @return Tekstlig beskrivelse av trekket.
     */
    private String utførFlytting(Spill spill, Spiller s, Brett brett, int kast, int gammelPlass) {
        if (gammelPlass + kast > 100) {
            avsluttTur(spill, s, kast, gammelPlass, gammelPlass, true);
            return s.getNavn() + ": triller " + kast + ". For høyt! Blir stående.";
        }
        
        int landerPaa = gammelPlass + kast;
        int nyPlass = brett.finnDestinasjon(gammelPlass, kast);
        s.setPosisjon(nyPlass);

        String ekstra = "";
        Rute rute = brett.hentRute(landerPaa);
        if (rute != null) {
            if (rute.erStige()) ekstra = " (stige " + landerPaa + " -> " + nyPlass + ")";
            else if (rute.erSlange()) ekstra = " (slange " + landerPaa + " -> " + nyPlass + ")";
        }

        if (nyPlass == 100) {
            spill.setFerdig(true);
            avsluttTur(spill, s, kast, gammelPlass, nyPlass, false);
            return s.getNavn() + ": triller " + kast + " -> MÅL!";
        } else {
            boolean nyttKast = (kast == 6);
            avsluttTur(spill, s, kast, gammelPlass, nyPlass, !nyttKast);
            
            String melding = s.getNavn() + ": triller " + kast 
                           + ", rute " + gammelPlass + " -> " + nyPlass + ekstra;
            if (nyttKast) melding += " (Nytt kast!)";
            
            return melding;
        }
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
    private void avsluttTur(Spill spill, Spiller s, int k, int fra, int til, boolean byttTur) {
        spill.leggTilTrekk(new Trekk(spill, k, fra, til, s.getNavn(), LocalDateTime.now()));
        
        if (byttTur) {
            spill.setNesteSpillerIndex((spill.getNesteSpillerIndex() + 1) % spill.getSpillere().size());
        }
        spillerRepository.save(s);
        spillRepository.save(spill);
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
    public List<String> hentLogg(Long spillId) {
        List<Trekk> trekkListe = trekkRepository.findBySpillIdOrderByTidspunktAsc(spillId);
        List<String> loggUt = new ArrayList<>();
        
        if (trekkListe == null) return loggUt;

        for (Trekk t : trekkListe) {
            loggUt.add(t.getSpillerNavn() + " trillet " + t.getTerningkast() + 
                       " (Rute " + t.getFraRute() + " -> " + t.getTilRute() + ")");
        }
        return loggUt;
    }
}