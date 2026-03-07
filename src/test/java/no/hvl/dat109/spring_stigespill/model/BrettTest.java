package no.hvl.dat109.spring_stigespill.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.hvl.dat109.spring_stigespill.repository.RuteRepository;

/**
 * Enhetstester for Brett-klassen.
 * Verifiserer logikk for flytting, inkludert stiger, slanger og mål-regler.
 */
@ExtendWith(MockitoExtension.class)
class BrettTest {

    @Mock
    private RuteRepository ruteRepository;

    @InjectMocks
    private Brett brett;

    /**
     * Tester vanlig flytting når man lander på en rute uten stige eller slange.
     */
    @Test
    void vanligFlyttingTest() {
        // Oppsett: Rute 10 er en vanlig rute (ikke spesialrute)
        Rute vanligRute = new Rute(10, 0); 
        when(ruteRepository.findById(10)).thenReturn(Optional.of(vanligRute));

        int resultat = brett.finnDestinasjon(5, 5);

        assertEquals(10, resultat);
    }

    /**
     * Tester at man flyttes opp når man lander på en stige.
     */
    @Test
    void landerPaaStigeTest() {
        // Oppsett: Rute 10 har en stige som flytter spiller til rute 20
        Rute stigeRute = new Rute(10, 20);
        when(ruteRepository.findById(10)).thenReturn(Optional.of(stigeRute));

        int resultat = brett.finnDestinasjon(4, 6);

        assertEquals(20, resultat);
    }

    /**
     * Tester at man blir stående hvis kastet går over rute 100.
     */
    @Test
    void kastOverHundreTest() {
        // Nåværende plass 98 + kast 5 = 103 (ugyldig)
        int resultat = brett.finnDestinasjon(98, 5);

        assertEquals(98, resultat);
    }
}