package no.hvl.dat109.spring_stigespill.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.hvl.dat109.spring_stigespill.model.Brett;
import no.hvl.dat109.spring_stigespill.model.Spill;
import no.hvl.dat109.spring_stigespill.model.Spiller;
import no.hvl.dat109.spring_stigespill.model.Terning;
import no.hvl.dat109.spring_stigespill.model.Trekk;
import no.hvl.dat109.spring_stigespill.repository.SpillRepository;
import no.hvl.dat109.spring_stigespill.repository.SpillerRepository;
import no.hvl.dat109.spring_stigespill.repository.TrekkRepository;

/**
 * Håndterer forretningslogikken og spillreglene i Stigespillet.
 * Fungerer som bindeledd mellom kontrollere, domenemodellen og databasen.
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
    private Terning terning;
    @Autowired 
    private Brett brett;
    
    /**
     * Oppretter et nytt spillobjekt med gitte spillere og lagrer det i databasen.
     * * @param spillere Liste med spillere som skal delta.
     * @return Det lagrede Spill-objektet.
     */
    public Spill opprettNyttSpill(List<Spiller> spillere) {
        Spill spill = new Spill(spillere);
        return spillRepository.save(spill);
    }

    /**
     * Hovedmetoden for å utføre en spilltur.
     * Koordinerer terningkast, regelkontroll, flytting og lagring av historikk.
     * * @param spillId ID-en til spillet som skal oppdateres.
     * @return Et Trekk-objekt som beskriver resultatet av turen.
     */
    @Transactional
    public Trekk spillTur(Long spillId) {
        Spill spill = spillRepository.findById(spillId).orElse(null);

        if (spill == null || spill.erFerdig()) return null;
        
        // Sorterer spillere for å sikre riktig tur-rekkefølge
        spill.getSpillere().sort(Comparator.comparing(Spiller::getId));
       
        Spiller spiller = spill.nesteSpiller();
        int gammelPlass = spiller.getPosisjon();
        
        int kast = terning.trill();
        String type = sjekkRegler(spiller, kast);
        
        if (type.equals("TRE_SEKSERE")) {
            return avsluttTur(spill, spiller, kast, gammelPlass, spiller.getPosisjon(), false, type);
        }
        
        oppdaterSekserTeller(spiller, kast);
        
        if (!type.equals("VANLIG")) {
            boolean byttTur = (kast != 6);
            return avsluttTur(spill, spiller, kast, gammelPlass, spiller.getPosisjon(), byttTur, type);
        }

        return utforFlytting(spill, spiller, brett, kast, gammelPlass);
    }

    /**
     * Oppdaterer telleren for antall seksere på rad for en spiller.
     * * @param spiller Spilleren som har kastet.
     * @param kast Verdien på det siste terningkastet.
     */
    private void oppdaterSekserTeller(Spiller spiller, int kast) {
        if (kast == 6) {
            spiller.setAntallSekserePaaRad(spiller.getAntallSekserePaaRad() + 1);
        } else {
            spiller.setAntallSekserePaaRad(0);
        }
    }

    /**
     * Sjekker om spesielle regler inntreffer (f.eks. start-regler eller tre seksere).
     * * @param s Spilleren som utfører trekket.
     * @param kast Verdien på terningkastet.
     * @return En streng som beskriver regeltypen ("TRE_SEKSERE", "START_UT", osv.).
     */
    private String sjekkRegler(Spiller s, int kast) {
        if (s.getAntallSekserePaaRad() == 3) {
            s.setPosisjon(1);
            s.setAntallSekserePaaRad(0);
            return "TRE_SEKSERE";
        }

        if (s.getPosisjon() == 1) {
            if (s.getAntallSekserePaaRad() == 0) {
                if (kast == 6) {
                    return "START_UT";
                } else {
                    return "START_BLOKKERT";
                }
            }
        }
        return "VANLIG"; 
    }

    /**
     * Beregner og utfører selve forflytningen på brettet.
     * Håndterer mål-passering, stiger og slanger via Brett-objektet.
     * * @param spill Det aktuelle spillet.
     * @param spiller Spilleren som skal flyttes.
     * @param brett Brett-objektet som kjenner rutenes egenskaper.
     * @param kast Terningkastet som ble trillet.
     * @param gammelPlass Posisjonen spilleren sto på før kastet.
     * @return Et Trekk-objekt generert via avsluttTur.
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
     * Lagrer resultatet av en tur, oppdaterer spillstatus og bytter tur om nødvendig.
     * * @param spill Gjeldende spill-entitet.
     * @param s Spilleren som har utført turen.
     * @param k Verdien på terningkastet.
     * @param fra Ruten spilleren startet på.
     * @param til Ruten spilleren landet på.
     * @param byttTur Angir om turen skal gå videre til neste spiller.
     * @param trekkType Beskrivelse av hva slags trekk som ble gjort.
     * @return Det lagrede Trekk-objektet.
     */
    private Trekk avsluttTur(Spill spill, Spiller s, int k, int fra, int til, boolean byttTur, String trekkType) {
        
        Trekk trekk = new Trekk(spill, k, fra, til, s.getNavn(), LocalDateTime.now(), trekkType);
        
        if (trekkType.equals("VINNER")) {
            spill.setFerdig(true);
        }
        
        trekkRepository.save(trekk);
        
        if (!spill.erFerdig() && byttTur) {
            spill.setNesteSpillerIndex((spill.getNesteSpillerIndex() + 1) % spill.getSpillere().size());
        }
        
        spillerRepository.save(s);
        spillRepository.save(spill);
        
        return trekk;
    }
    
    /**
     * Henter et spesifikt spill basert på ID.
     * * @param spillId ID til spillet som skal hentes.
     * @return Spill-objektet, eller null hvis det ikke finnes.
     */
    @Transactional(readOnly = true)
    public Spill hentSpill(Long spillId) {
        return spillRepository.findById(spillId).orElse(null);
    }
    
    /**
     * Henter den komplette historikken av trekk for et gitt spill.
     * * @param spillId ID til spillet loggen skal hentes for.
     * @return En liste med Trekk-objekter sortert etter tidspunkt.
     */
    @Transactional(readOnly = true)
    public List<Trekk> hentLogg(Long spillId) {
        return trekkRepository.findBySpillIdOrderByTidspunktAsc(spillId);
    }
}