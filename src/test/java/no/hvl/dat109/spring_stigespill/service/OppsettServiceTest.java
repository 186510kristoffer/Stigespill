package no.hvl.dat109.spring_stigespill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.hvl.dat109.spring_stigespill.dto.SpillOppsett;
import no.hvl.dat109.spring_stigespill.model.Spiller;

class OppsettServiceTest {
    private OppsettService service = new OppsettService();

    @Test
    void lagSpillerListeStandardVerdierTest() {
        SpillOppsett oppsett = new SpillOppsett();
        oppsett.setAntallSpillere(2);
        oppsett.setSpillerNavn(List.of("", "   ")); // Tomme navn

        List<Spiller> resultat = service.lagSpillerListe(oppsett);

        assertEquals("Spiller1", resultat.get(0).getNavn());
        assertEquals("Spiller2", resultat.get(1).getNavn());
    }
}