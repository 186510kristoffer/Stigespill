package no.hvl.dat109.spring_stigespill.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import no.hvl.dat109.spring_stigespill.model.Spiller;

@DataJpaTest
class SpillerRepositoryTest {

    @Autowired
    private SpillerRepository spillerRepository;

    @Test
    void lagreOgHenteSpillerTest() {
        Spiller spiller = new Spiller("Testbruker", "blå");
        
        
        Spiller lagretSpiller = spillerRepository.save(spiller);
        
        
        Spiller funnetSpiller = spillerRepository.findById(lagretSpiller.getId()).orElse(null);
        
        assertNotNull(funnetSpiller);
        assertEquals("Testbruker", funnetSpiller.getNavn());
        assertEquals(1, funnetSpiller.getPosisjon());
    }
}