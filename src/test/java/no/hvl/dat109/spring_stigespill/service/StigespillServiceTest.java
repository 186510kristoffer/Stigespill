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

import no.hvl.dat109.spring_stigespill.model.Rute;
import no.hvl.dat109.spring_stigespill.model.Spill;
import no.hvl.dat109.spring_stigespill.model.Spiller;
import no.hvl.dat109.spring_stigespill.model.Terning;
import no.hvl.dat109.spring_stigespill.repository.RuteRepository;
import no.hvl.dat109.spring_stigespill.repository.SpillRepository;
import no.hvl.dat109.spring_stigespill.repository.SpillerRepository;
import no.hvl.dat109.spring_stigespill.repository.TrekkRepository;
import no.hvl.dat109.spring_stigespill.service.StigespillService;

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

    @InjectMocks
    private StigespillService service;

    private Spill spill;
    private Spiller spiller;
    private List<Rute> ruter;

    /**
     * Klargjør et test-scenario før hver test.
     * Oppretter spiller, spill og et tomt brett.
     */
    @BeforeEach
    void oppsett() {
        spiller = new Spiller("TestSpiller", "F1");
        List<Spiller> spillere = new ArrayList<>();
        spillere.add(spiller);

        spill = new Spill(spillere);
        spill.setId(1L);

        ruter = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ruter.add(new Rute(i, 0)); 
        }

        when(spillRepository.findById(1L)).thenReturn(Optional.of(spill));
        when(ruteRepository.findAll()).thenReturn(ruter);
    }

    /**
     * Tester at spiller må få 6 for å flytte ut fra start.
     */
    @Test
    void maaHaSeksForAaStarteTest() {
        when(terning.trill()).thenReturn(5);

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("Må ha 6"));
        assertEquals(1, spiller.getPosisjon());
    }

    /**
     * Tester at spiller får beskjed om å være ute ved kast 6 på start.
     */
    @Test
    void faarSeksPaaStartTest() {
        when(terning.trill()).thenReturn(6);

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("Ute av start"));
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

        service.spillTur(1L);

        assertEquals(15, spiller.getPosisjon());
    }

    /**
     * Tester at stige-funksjonalitet fungerer (flytter opp).
     */
    @Test
    void stigeTest() {
        ruter.get(2).setFlyttTil(10); 
        
        spiller.setPosisjon(1); 
        spiller.setAntallSekserePaaRad(1); 
        
        when(terning.trill()).thenReturn(2);

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("stige"));
        assertEquals(10, spiller.getPosisjon());
    }

    /**
     * Tester at slange-funksjonalitet fungerer (flytter ned).
     */
    @Test
    void slangeTest() {
        spiller.setPosisjon(90);
        ruter.get(94).setFlyttTil(50); 

        when(terning.trill()).thenReturn(5);

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("slange"));
        assertEquals(50, spiller.getPosisjon());
    }

    /**
     * Tester at spiller vinner ved nøyaktig kast til 100.
     */
    @Test
    void vinneSpillTest() {
        spiller.setPosisjon(98);
        when(terning.trill()).thenReturn(2);

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("MÅL"));
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

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("Blir stående"));
        assertEquals(98, spiller.getPosisjon());
    }

    /**
     * Tester regelen om at 3 seksere på rad sender spiller tilbake til start.
     */
    @Test
    void treSekserePaaRadTest() {
        spiller.setPosisjon(50);
        spiller.setAntallSekserePaaRad(2);
        
        when(terning.trill()).thenReturn(6);

        String melding = service.spillTur(1L);

        assertTrue(melding.contains("Tilbake til start"));
        assertEquals(1, spiller.getPosisjon());
        assertEquals(0, spiller.getAntallSekserePaaRad());
    }
}