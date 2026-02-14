package no.hvl.dat109.spring_stigespill.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpillOppsett {
	private int antallSpillere;
	private List<String> spillerNavn= new ArrayList<>();
	private boolean simuler=false;
}
