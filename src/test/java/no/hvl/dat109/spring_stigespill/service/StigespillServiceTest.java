package no.hvl.dat109.spring_stigespill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * Enhetstester for forretningslogikken i StigespillService.
 * Tester reglene isolert ved bruk av Mockito.
 */
@ExtendWith(MockitoExtension.class)
class StigespillServiceTest {

    @Mock
    private SpillRepository spillRepository;
    @Mock
    private SpillerRepository spillerRepository;
    @Mock
    private RuteRepository ruteRepository;
    @Mock
    private TrekkRepository trekkRepository;
    @Mock
    private Terning terning; 
    @Mock
    private Brett brett; // Lagt til for å støtte ny logikk i servicen

    @InjectMocks
    private StigespillService service;

    private Spill spill;
    private Spiller spiller;

    /**
     * Klargjør et test-scenario før hver test.
     * Oppretter spiller og spill.
     */
    @BeforeEach
    void oppsett() {
        spiller = new Spiller("TestSpiller", "F1");
        spiller.setId(1L);
        List<Spiller> spillere = new ArrayList<>();
        spillere.add(spiller);

        spill = new Spill(spillere);
        spill.setId(1L);

        when(spillRepository.findById(1L)).thenReturn(Optional.of(spill));
    }

    /**
     * Tester at spiller må få 6 for å flytte ut fra start.
     */
    @Test
    void maaHaSeksForAaStarteTest() {
        when(terning.trill()).thenReturn(5);

        Trekk trekk = service.spillTur(1L);

        assertEquals("START_BLOKKERT", trekk.getTrekkType()); // Sjekker TrekkType i stedet for String
        assertEquals(1, spiller.getPosisjon());
    }

    /**
     * Tester at spiller får beskjed om å være ute ved kast 6 på start.
     */
    @Test
    void faarSeksPaaStartTest() {
        when(terning.trill()).thenReturn(6);

        Trekk trekk = service.spillTur(1L);

        assertEquals("START_UT", trekk.getTrekkType());
        assertEquals(1, spiller.getPosisjon()); 
        assertEquals(1, spiller.getAntallSekserePaaRad());
    }

    /**
     * Tester at spiller flytter korrekt etter å ha kommet ut av start (har seksere på rad > 0).
     */
    @Test
    void flytterEtterAaHaKommetUtTest() {
        spiller.setAntallSekserePaaRad(1); 
        when(terning.trill()).thenReturn(4);
        when(brett.finnDestinasjon(1, 4)).thenReturn(5); // Simulerer brett-logikk

        service.spillTur(1L);

        assertEquals(5, spiller.getPosisjon());
    }

    /**
     * Tester vanlig flytting midt på brettet.
     */
    @Test
    void vanligFlyttingTest() {
        spiller.setPosisjon(10);
        when(terning.trill()).thenReturn(5);
        when(brett.finnDestinasjon(10, 5)).thenReturn(15);

        service.spillTur(1L);

        assertEquals(15, spiller.getPosisjon());
    }

    /**
     * Tester at stige-funksjonalitet fungerer (flytter opp).
     */
    @Test
    void stigeTest() {
        spiller.setPosisjon(1); 
        spiller.setAntallSekserePaaRad(1); 
        
        when(terning.trill()).thenReturn(2);
        when(brett.finnDestinasjon(1, 2)).thenReturn(10); // Simulerer stige til rute 10

        Trekk trekk = service.spillTur(1L);

        assertEquals("STIGE", trekk.getTrekkType());
        assertEquals(10, spiller.getPosisjon());
    }

    /**
     * Tester at slange-funksjonalitet fungerer (flytter ned).
     */
    @Test
    void slangeTest() {
        spiller.setPosisjon(90);
        when(terning.trill()).thenReturn(5);
        when(brett.finnDestinasjon(90, 5)).thenReturn(50); // Simulerer slange til rute 50

        Trekk trekk = service.spillTur(1L);

        assertEquals("SLANGE", trekk.getTrekkType());
        assertEquals(50, spiller.getPosisjon());
    }

    /**
     * Tester at spiller vinner ved nøyaktig kast til 100.
     */
    @Test
    void vinneSpillTest() {
        spiller.setPosisjon(98);
        when(terning.trill()).thenReturn(2);
        when(brett.finnDestinasjon(98, 2)).thenReturn(100);

        Trekk trekk = service.spillTur(1L);

        assertEquals("VINNER", trekk.getTrekkType());
        assertEquals(100, spiller.getPosisjon());
        assertTrue(spill.erFerdig());
    }

    /**
     * Tester at spiller blir stående ved for høyt kast (over 100).
     */
    @Test
    void forHoytKastTest() {
        spiller.setPosisjon(98);
        when(terning.trill()).thenReturn(4);

        Trekk trekk = service.spillTur(1L);

        assertEquals("FORBI", trekk.getTrekkType());
        assertEquals(98, spiller.getPosisjon());
    }

    /**
     * Tester regelen om at 3 seksere på rad sender spiller tilbake til start.
     */
    @Test
    void treSekserePaaRadTest() {
        spiller.setPosisjon(50);
        spiller.setAntallSekserePaaRad(3); // Logikken din sjekker om teller er 3 ved starten av turen
        
        when(terning.trill()).thenReturn(6);

        Trekk trekk = service.spillTur(1L);

        assertEquals("TRE_SEKSERE", trekk.getTrekkType());
        assertEquals(1, spiller.getPosisjon());
        assertEquals(0, spiller.getAntallSekserePaaRad());
    }
}