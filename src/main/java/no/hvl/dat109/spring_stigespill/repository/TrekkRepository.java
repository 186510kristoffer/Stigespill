package no.hvl.dat109.spring_stigespill.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import no.hvl.dat109.spring_stigespill.model.Trekk;

@Repository
public interface TrekkRepository extends JpaRepository<Trekk, Long> {

    /**
     * Henter alle trekk tilhørende en spesifikk spill-ID, sortert etter tidspunkt (eldst først).
     * Spring Data JPA genererer automatisk SQL basert på navnet.
     */
    List<Trekk> findBySpillIdOrderByTidspunktAsc(Long spillId);

}