package no.hvl.dat109.spring_stigespill.repository;

import no.hvl.dat109.spring_stigespill.model.Rute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuteRepository extends JpaRepository<Rute, Integer> {
}