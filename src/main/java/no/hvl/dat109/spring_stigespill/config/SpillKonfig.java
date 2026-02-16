package no.hvl.dat109.spring_stigespill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.hvl.dat109.spring_stigespill.model.Brett;
import no.hvl.dat109.spring_stigespill.repository.RuteRepository;

@Configuration
public class SpillKonfig {
	
	@Bean
	public Brett ferdigBrett(RuteRepository repo) {
		return new Brett(repo.findAll());
	}

}
